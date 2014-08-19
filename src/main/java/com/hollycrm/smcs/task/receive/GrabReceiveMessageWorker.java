package com.hollycrm.smcs.task.receive;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.assist.AvaliableReceiveMessage;
import com.hollycrm.smcs.entity.fetch.ReceiveMaxId;
import com.hollycrm.smcs.entity.log.GrabLog;
import com.hollycrm.smcs.entity.receive.EventMessage;
import com.hollycrm.smcs.entity.receive.ReceiveMessage;
import com.hollycrm.smcs.log.impl.GrabLogger;
import com.hollycrm.smcs.receive.IReceiveMessageContainer;
import com.hollycrm.smcs.receive.IReceiveMessageStrategy;
import com.hollycrm.smcs.receive.bean.ExitMessage;
import com.hollycrm.smcs.receive.bean.ReceiveMessageBlockArray;
import com.hollycrm.smcs.receive.impl.ReceiveMessageStrategy;
import com.hollycrm.smcs.service.fetch.MaxIdService;
import com.hollycrm.smcs.service.receive.ReceiveMessageService;
import com.hollycrm.smcs.task.IDispatcher;
import com.hollycrm.smcs.util.ThreadExecutor;

public class GrabReceiveMessageWorker implements Runnable{
	
	private  final Logger logger = LoggerFactory.getLogger(GrabReceiveMessageWorker.class);

	private final AvaliableReceiveMessage avaliableReceiveMessage;
	
	/**接收消息的maxId**/
	private ReceiveMaxId receiveMaxId;
	
	private final IDispatcher dispatcher;
	
	private final IReceiveMessageStrategy receiveMessageStrategy;
	
	/**存放消息容器**/
	private final IReceiveMessageContainer receiveMessageContainer;
	
	private  Long maxId = 0L;
	
	private Long sinceId;
	
	public GrabReceiveMessageWorker(AvaliableReceiveMessage avaliableReceiveMessage, IDispatcher dispatcher){
		this.avaliableReceiveMessage = avaliableReceiveMessage;
		this.dispatcher = dispatcher;
		receiveMessageContainer = new ReceiveMessageBlockArray(64);
		receiveMessageStrategy = new ReceiveMessageStrategy(avaliableReceiveMessage.getBloggerId()
				, avaliableReceiveMessage.getReceiveMessageTime(), receiveMessageContainer);
		
	}
	
	@Override
	public void run() {
		/**fetch size**/
		int size = 0;
		
		logger.info(String.format("开始接收bloggerId{%s},groupId{%s}的消息",
				avaliableReceiveMessage.getBloggerId(), avaliableReceiveMessage.getGroupId()));
		
		try {
			initMaxId();
			ThreadExecutor.execute(new Runnable(){
				@Override
				public void run() {
					receiveMessageStrategy.receive(sinceId);
				}
				
			});
			while(true){
				ReceiveMessage receiveMessage = receiveMessageContainer.pull();
				if(receiveMessage instanceof ExitMessage){
					break;
				}
				if(receiveMessage.getReceiveId() > maxId){
					maxId = receiveMessage.getReceiveId();
				}
				if(receiveMessage instanceof EventMessage){
					continue;
				}
				receiveMessage.setGroupId(avaliableReceiveMessage.getGroupId());
				save(receiveMessage);				
				size++;
			}
			
			logger.info(String.format("接收bloggerId{%s},groupId{%s}的消息量{%s}", avaliableReceiveMessage.getBloggerId(), avaliableReceiveMessage.getGroupId(), size));
			
			
		} catch (Exception e) {
			logger.error(e.toString(), e);
			receiveMessageStrategy.exit();
		}finally{
			saveMaxId();
			GrabLogger.logger(new GrabLog(avaliableReceiveMessage.getGroupId(), "receive", new Date(), size, true));
			avaliableReceiveMessage.sleep();
			dispatcher.release(avaliableReceiveMessage.getBloggerId());
		}
		
		
	}
	
	/**
	 * 保存最大的maxId
	 */
	private void saveMaxId(){
		if(maxId == 0L){
			return;
		}
		if(receiveMaxId != null){
			receiveMaxId.setMaxId(maxId);
		} else {
			receiveMaxId = new ReceiveMaxId();
			receiveMaxId.setBloggerId(avaliableReceiveMessage.getBloggerId());
			receiveMaxId.setIdGroup(avaliableReceiveMessage.getGroupId());
			receiveMaxId.setMaxId(maxId);
			receiveMaxId.setMediaType("w");
		}
		ApplicationContextHolder.getBean(MaxIdService.class).save(receiveMaxId);
	}
	
	/**
	 * 保存receiveMessage
	 * @param receiveMessage
	 */
	private void save(ReceiveMessage receiveMessage ){
		ApplicationContextHolder.getBean(ReceiveMessageService.class).saveReceiveMessageAndExtra(receiveMessage);
		
	}
	
	
	
	/**
	 * 
	 * 初始化上次抓取的最大的maxId;
	 * 
	 * **/
	private void initMaxId(){
		receiveMaxId = ApplicationContextHolder.getBean(MaxIdService.class).findReceiveMaxId(avaliableReceiveMessage.getBloggerId(), 
				avaliableReceiveMessage.getGroupId(), avaliableReceiveMessage.getMediaType());
		if(receiveMaxId != null){
			sinceId = receiveMaxId.getMaxId();
		}
	}

}
