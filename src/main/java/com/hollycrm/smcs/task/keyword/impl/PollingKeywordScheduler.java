package com.hollycrm.smcs.task.keyword.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.assist.AvaliableKeyword;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.entity.fetch.Condition;
import com.hollycrm.smcs.entity.fetch.Keyword;
import com.hollycrm.smcs.service.fetch.ConditionService;

public class PollingKeywordScheduler implements Runnable{
	private  final Logger logger = LoggerFactory.getLogger(PollingKeywordScheduler.class);
	
	private final ConditionService conditionService;
	
	private final GrabKeywordDispatcher dispatcher;
	
	public static final long SCHEDULER_SLEEP_TIME = Long.parseLong(AppConfig.get("polling.keyword.scheduler"));
	
	public PollingKeywordScheduler(GrabKeywordDispatcher dispatcher){
		this.dispatcher = dispatcher;
		conditionService = ApplicationContextHolder.getBean(ConditionService.class);		
		
	}
	
	public void polling(){
		Thread thread = new Thread(this);
		thread.setName("polling keyword scheduler");
		thread.setDaemon(true);
		thread.start();
		logger.info("polling keyword thread is starting");
	}

	@Override
	public void run() {
		while(true){
			try{
				List<Condition> conditions = this.conditionService.getAllConditions(
						Integer.parseInt(AppConfig.get("taskServer")),"1","1");
				AvaliableKeyword avaliableKeyword = null;
				for (Condition condition : conditions) {
					String key = "";
					String exclusiveKey = "";
					for(Keyword keyword:condition.getKeywords()){
						if(keyword.isJunction()){							
							key = assembleKey(key,keyword.getKeyword());
						}else{
							exclusiveKey = assembleKey(exclusiveKey,keyword.getKeyword());
							
						}
					}
					key=key.trim();
					exclusiveKey = exclusiveKey.trim();
					if(StringUtils.isBlank(key)){
						continue;
					}
					
					avaliableKeyword = new AvaliableKeyword(key, exclusiveKey, condition.getId(), condition.getGroupId(), 
							condition.getInterval(), condition.getProvince(), condition.getCity());				
					dispatcher.append(avaliableKeyword);
				}
				
				List<Condition> invalidConditions =conditionService.getInvalidConditions(Integer.parseInt(AppConfig.get("taskServer")), "0");
				for(Condition invalidCondition : invalidConditions){
					dispatcher.removeInvalidWaiting(invalidCondition.getId());
				}
				Thread.sleep(SCHEDULER_SLEEP_TIME);
			}catch(Exception e){
				logger.error(e.getLocalizedMessage(),e);
			}			
		}
		
	}
	
	private String assembleKey(String key, String waitKey){
		key += waitKey;
		key += Condition.SPLIT_KEYWORD;
		return key;
	}
	

}
