package com.hollycrm.smcs.task.pm.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.assist.AvaliableNote;
import com.hollycrm.smcs.assist.AvaliablePm;
import com.hollycrm.smcs.assist.AvaliableReceiveMessage;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.entity.base.GrabConfig;
import com.hollycrm.smcs.entity.base.OfficalBlog;
import com.hollycrm.smcs.service.base.GrabConfigService;
import com.hollycrm.smcs.task.AbsDispatcher;

public class PollingOfficalBlogScheduler implements Runnable{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**以html方式抓取私信**/
	private final AbsDispatcher<AvaliablePm, Long> privateDispatcher;
	
	/**消息接口**/
	private final AbsDispatcher<AvaliableReceiveMessage, Long> receiveMessageDispatcher;
	
	/**htmL留言**/
	private final AbsDispatcher<AvaliableNote, Long> noteDispatcher;
	
	
	public static final long SCHEDULER_SLEEP_TIME = 120000;
	
	private static final long GRAB_PRIVATE_INTERVAL = Long.parseLong(AppConfig.get("grab.private.interval"));
	
	private static final long RECEIVE_MESSAGE_TIME = Long.parseLong(AppConfig.get("receive.message.time"));
	
	private final GrabConfigService grabConfigService;
	
	public PollingOfficalBlogScheduler(AbsDispatcher<AvaliablePm, Long> privateDispatcher, 
			AbsDispatcher<AvaliableNote, Long> noteDispatcher, AbsDispatcher<AvaliableReceiveMessage, Long> receiveMessageDispatcher){
		this.privateDispatcher = privateDispatcher;
		this.noteDispatcher = noteDispatcher;
		this.receiveMessageDispatcher = receiveMessageDispatcher;
		grabConfigService = ApplicationContextHolder.getBean(GrabConfigService.class);
	}

	public void polling(){
		Thread thread = new Thread(this);
		thread.setName("polling officalblog");
		thread.setDaemon(true);
		thread.start();
		logger.info("polling officalblog thread is starting");
	}
	
	

	@Override
	public void run() {
		GrabConfig grabConfig = null;
		AvaliablePm avaliablePm = null;
		AvaliableNote avaliableNote = null;
		AvaliableReceiveMessage avaliableReceiveMessage = null;
		OfficalBlog officalBlog = null;
		while(true){
			try{
				//[GrabConfig, OfficalBlog]
				List<Object[]> list = grabConfigService.findPrivateGrabConfig(Integer.parseInt(AppConfig.get("taskServer")),"w");
				logger.info("polling " + list.size() + " private grabconfig ");
				for(Object[] obj :list){
					officalBlog = (OfficalBlog) obj[1];
					grabConfig = (GrabConfig) obj[0];
					if(!officalBlog.isStatus() || !grabConfig.isValid()){
						privateDispatcher.removeInvalidWaiting(officalBlog.getId());
						noteDispatcher.removeInvalidWaiting(officalBlog.getId());
						receiveMessageDispatcher.removeInvalidWaiting(officalBlog.getId());
						continue;
					}					
					if(grabConfig.isGrabPrivate()){
						avaliablePm = new AvaliablePm(grabConfig.getBloggerId(), grabConfig.getGroupId(), grabConfig.getMediaType(), 
								grabConfig.getGrabPrivateModle(), grabConfig.getPrivateMessageInterval());
						avaliableNote = new AvaliableNote(grabConfig.getBloggerId(), grabConfig.getGroupId(), grabConfig.getMediaType(), 
								grabConfig.getGrabPrivateModle(), grabConfig.getPrivateMessageInterval());
						privateDispatcher.append(avaliablePm);
						noteDispatcher.append(avaliableNote);
						receiveMessageDispatcher.removeInvalidWaiting(officalBlog.getId());
					}else if(!grabConfig.isGrabPrivate()&&grabConfig.isByReceive()){
						avaliableReceiveMessage = new AvaliableReceiveMessage(grabConfig.getBloggerId(), grabConfig.getGroupId(), grabConfig.getMediaType(), 
								 grabConfig.getReceiveMessageInterval(), grabConfig.getReceiveMessageTime());
						privateDispatcher.removeInvalidWaiting(officalBlog.getId());
						noteDispatcher.removeInvalidWaiting(officalBlog.getId());
						receiveMessageDispatcher.append(avaliableReceiveMessage);
					}else {
						privateDispatcher.removeInvalidWaiting(officalBlog.getId());
						noteDispatcher.removeInvalidWaiting(officalBlog.getId());
						receiveMessageDispatcher.removeInvalidWaiting(officalBlog.getId());
					}
					
				}
				
				Thread.sleep(SCHEDULER_SLEEP_TIME);
			}catch(Exception e){
				logger.error(e.getLocalizedMessage(),e);
			}
			
			
		}
		
	}
}
