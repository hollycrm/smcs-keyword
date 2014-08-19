package com.hollycrm.smcs.task.keyword.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.assist.AvaliableKeywordGroup;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.service.fetch.ConditionService;


public class TransformKeywordScheduler implements Runnable{
	private final Logger logger = LoggerFactory.getLogger(TransformKeywordScheduler.class);
	
	private final TransformKeywordDispatcher dispatcher;
	
	private final ConditionService conditionService;
	public TransformKeywordScheduler(TransformKeywordDispatcher dispatcher){
		this.dispatcher = dispatcher;
		conditionService = ApplicationContextHolder.getBean(ConditionService.class);
	}
	
	public void polling(){
		Thread thread = new Thread(this);
		thread.setName("transform keyword scheduler");
		thread.setDaemon(true);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
		logger.info("transform keyword thread is starting");
	}

	@Override
	public void run() {
		while(true){
			try{
				List<Long> groupList = conditionService.getAllConditionGroup(Integer.parseInt(AppConfig.get("taskServer")),"1","1");
				for(Long groupId:groupList){
					dispatcher.append(new AvaliableKeywordGroup(groupId));
					
				}				
				Thread.sleep(300000);
			}catch(Exception e){
				;
			}
		}
	}

}
