package com.hollycrm.smcs.task.board.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.assist.AvaliableBoard;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.entity.base.GrabConfig;
import com.hollycrm.smcs.service.base.GrabConfigService;


public class PollingBoardScheduler implements Runnable{
	
	private final Logger logger = LoggerFactory.getLogger(PollingBoardScheduler.class);

	
	private final GrabConfigService grabConfigService;
	private final  GrabBoardMessageDispatcher dispatcher;
	
	public static final String SCHEDULE_SLEEP = "scheduler.sleep";
	
	public static final Long GRAB_BOARD_INTERVAL = Long.parseLong(AppConfig.get("grab.board.interval"));
	
	public static final Long SLEEP_TIME = Long.parseLong(AppConfig.get(SCHEDULE_SLEEP));
	
	public PollingBoardScheduler(GrabBoardMessageDispatcher dispatcher){
		this.dispatcher = dispatcher;
		grabConfigService = ApplicationContextHolder.getBean(GrabConfigService.class);
	}
	
	public void polling(){
		Thread thread = new Thread(this);
		thread.setName("polling bloggerId for board scheduler");
		thread.setDaemon(true);
		thread.start();
		logger.info("polling bloggerId for board thread is starting");
	}
	
	@Override
	public void run() {
		AvaliableBoard board = null;
		while(true){
			try{
				logger.info("开始获取有效的租户，进行留言板消息的抓取");				
				List<GrabConfig> list = this.grabConfigService.findGrabConfig(Integer
						.parseInt(AppConfig.get("taskServer")),"w");
				
				for(GrabConfig grabConfig:list){
					if(!grabConfig.isValid() || !grabConfig.isGrabBoard()){
						dispatcher.removeInvalidWaiting(grabConfig.getBloggerId());
						continue;
					}
					board = new AvaliableBoard(grabConfig.getBloggerId(), grabConfig.getGroupId(), 
								grabConfig.getMediaType(), grabConfig.getBoardInterval());
					dispatcher.append(board);
				}
				
			  Thread.sleep(SLEEP_TIME);
			}catch(Exception e){
				logger.error(e.getLocalizedMessage(),e);
			}
			
		}
		
	}

}
