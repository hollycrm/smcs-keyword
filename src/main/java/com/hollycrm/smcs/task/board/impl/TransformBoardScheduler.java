package com.hollycrm.smcs.task.board.impl;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.entity.base.IdOauth;
import com.hollycrm.smcs.service.base.IdOauthService;


public class TransformBoardScheduler implements Runnable{
	
	private final Logger logger = LoggerFactory.getLogger(TransformBoardScheduler.class);
	
	private final IdOauthService idOauthService;
	
	public TransformBoardScheduler(){
		idOauthService = ApplicationContextHolder.getBean(IdOauthService.class);
	}
	
	public void polling(){
		Thread thread = new Thread(this);
		thread.setName("转化board scheduler");
		thread.setDaemon(true);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	@Override
	public void run() {
		while(true){
			try{
				List<IdOauth> list = this.idOauthService.findDistinctOauth(Integer
						.parseInt(AppConfig.get("taskServer")));
				logger.info("get "+list.size()+" group to get self board trend");
				ExecutorService executorService = Executors.newFixedThreadPool(5);
				CountDownLatch countDownLatch = new CountDownLatch(list.size());
				try{		
					for(IdOauth oauth:list){
						executorService.execute(new TransformBoardWorker(oauth,countDownLatch));
					}
					countDownLatch.await();
				} catch (Exception e) {
					
					this.logger.error("fetch trend status has some problems", e);
				} finally {
					executorService.shutdown();
				}
				Thread.sleep(120000);
			}catch(Exception e){
				logger.error(e.getLocalizedMessage(),e);
			}
		}
		
	}

}
