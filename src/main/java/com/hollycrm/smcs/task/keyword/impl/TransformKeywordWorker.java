package com.hollycrm.smcs.task.keyword.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weibo4j.model.WeiboException;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.assist.AvaliableKeywordGroup;
import com.hollycrm.smcs.atomic.impl.ConditionStatusAtomic;
import com.hollycrm.smcs.atomic.strategy.AbstractSinaStrategy;
import com.hollycrm.smcs.atomic.strategy.Exception.InvalidTokenException;
import com.hollycrm.smcs.entity.base.Trend;
import com.hollycrm.smcs.entity.fetch.Condition;
import com.hollycrm.smcs.entity.message.ConditionStatus;
import com.hollycrm.smcs.service.fetch.TrendService;
import com.hollycrm.smcs.task.IDispatcher;

public class TransformKeywordWorker implements Runnable{
	
	private final Logger logger = LoggerFactory.getLogger(TransformKeywordWorker.class);	

	public static final int FETCH_COUNT = 30;
	private final AvaliableKeywordGroup avaliable;
	
	private final TrendService trendService;
	
	private final IDispatcher dispatcher;
	
	
	public TransformKeywordWorker(AvaliableKeywordGroup avaliable,IDispatcher dispatcher){
		this.dispatcher = dispatcher;
		this.avaliable = avaliable;
		trendService = ApplicationContextHolder.getBean(TrendService.class);
		
	}
	
	@Override
	public void run() {
		
		int size = FETCH_COUNT;
		int sum = 0;
		Trend t = null;
		
		ConditionStatus conditionStatus = null;
		try{		
			Condition condition = new Condition();
			
				List<Trend> trendList=trendService.findTrend(2,avaliable.getGroupId(),FETCH_COUNT);
				
				size = trendList.size();
				logger.info("读取"+size+"trend进行关键字转化");
				if(size == 0){
					return;
				}
				ConditionStatusAtomic atomic = new ConditionStatusAtomic(AbstractSinaStrategy.FLAG);
				for(int i=0;i<size;i++){
					t = trendList.get(i);	
					try{
						if(trendService.existsSaveMessage(t.getMid(),t.getMediaType(),avaliable.getGroupId())){
							t.setDeal(Trend.THE_SAME_DEAL);
							trendService.updateTrendDeal(t);
							continue;
						}
						atomic.setSid(t.getMid());
						conditionStatus = atomic.call();
						condition.setId(t.getConditionId());						
						conditionStatus.setCondition(condition);
						conditionStatus.setGroupId(avaliable.getGroupId());
						t.setDeal(Trend.NORMAL_DEAL);
						trendService.saveConditionStatusAndUpdateTrend(conditionStatus, t);
						sum++;					
					}catch(WeiboException e){
						int errorCode = e.getErrorCode();					
						logger.error("转化关键字sid="+t.getMid()+"出错,原因："+e.getLocalizedMessage());											
						if(errorCode == AbstractSinaStrategy.WEIBO_NOT_EXISTS){
							t.setDeal(Trend.NO_EXISTS_DEAL);
							trendService.updateTrendDeal(t);
						}else if (errorCode == AbstractSinaStrategy.WEIBO_IMPERMISSIBLE) {
							t.setDeal(Trend.NO_EXISTS_DEAL);
							trendService.updateTrendDeal(t);
						}else if((errorCode ==-1) ||(e.getLocalizedMessage().indexOf("connect timed out") != -1)) {
							throw e;
						}
						
					}catch(InvalidTokenException e){						
						break;
					}catch(Exception e){
						logger.error(e.getLocalizedMessage(),e);
						if(t.getCount()==2){
							t.setDeal(Trend.ERROR_DEAL);                                 							
						}
						t.setCount(t.getCount()+1);
						trendService.updateTrendDeal(t);
					}
					
				}
			
			logger.info("总共转化"+sum+"关键字");
		}catch(Exception e){
			logger.error(e.getLocalizedMessage(),e);
		}finally{
			avaliable.countRuntime(sum);
			dispatcher.release(avaliable.getGroupId());
		}
		
	}

}
