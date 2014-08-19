package com.hollycrm.smcs.task.board.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.atomic.impl.MentionStatusAtomic;
import com.hollycrm.smcs.entity.base.IdOauth;
import com.hollycrm.smcs.entity.base.Trend;
import com.hollycrm.smcs.entity.message.MentionStatus;
import com.hollycrm.smcs.service.fetch.TrendService;


public class TransformBoardWorker implements Runnable{
	
	private final Logger logger = LoggerFactory.getLogger(TransformBoardWorker.class);
	
	private final IdOauth oauth;
	private final CountDownLatch latch;
	private final TrendService trendService;
	
	public TransformBoardWorker(IdOauth oauth, CountDownLatch latch){
		this.oauth = oauth;
		this.latch = latch;
		trendService = ApplicationContextHolder.getBean(TrendService.class);
	}

	@Override
	public void run() {
		try{
			logger.info("board tranform"+oauth.getIdGroup().getId()+"线程已启动");
			int size=30;
			MentionStatusAtomic atomic = null;
			MentionStatus mentionStatus = null;
			int sum = 0;
			Trend t = null;
			for(int i=0;(i<20) && (size==30);i++){
				List<Trend> trendList=trendService.findTrend(3,oauth.getIdGroup().getId(),size);
			 size=trendList.size();		
			 atomic = new MentionStatusAtomic("w");
			 for(int j=0;j<size;j++){
				 try{
					 t = trendList.get(j);
					 if(trendService.existsSaveMessage(t.getMid(), t.getMediaType(), oauth.getIdGroup().getId())){
						 t.setDeal(Trend.THE_SAME_DEAL);
						 trendService.updateTrendDeal(t);
					 }
					
					 atomic.setMid(t.getMid());
					 mentionStatus = atomic.call();
					 mentionStatus.setGroupId(oauth.getIdGroup().getId());
					 mentionStatus.setOauthId(oauth.getId());
					 mentionStatus.setOauthBlogger(oauth.getSid());
					 t.setDeal(Trend.NORMAL_DEAL);
					 t.setDateTime(new Date());
					 trendService.saveMentionStatusAndUpdate(mentionStatus,t);
					 sum++;
				 }catch(Exception e){
					 if(t.getCount()==2){
							t.setDeal(Trend.ERROR_DEAL);                                 							
						}
						t.setCount(t.getCount()+1);
						trendService.updateTrendDeal(t);
				 }
				 
			 }
			}
			logger.info("board tranform"+oauth.getIdGroup().getId()+"线程共转化的个数为:"+sum);
		}catch(Exception e){
			logger.error(e.getLocalizedMessage(),e);
		}finally{
			latch.countDown();
		}
		
	}

}
