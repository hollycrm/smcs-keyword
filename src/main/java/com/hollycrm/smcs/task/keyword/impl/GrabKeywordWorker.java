package com.hollycrm.smcs.task.keyword.impl;

import com.hollycrm.smcs.assist.AvaliableKeyword;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.task.keyword.AbsGrabKeywordWorker;


public class GrabKeywordWorker extends AbsGrabKeywordWorker{
	
	public static final String GRAB_KEYWORD="search";
	
	public GrabKeywordWorker(GrabKeywordDispatcher dispatcher ,AvaliableKeyword avaliableKeyword) {		
		super(avaliableKeyword, new GrabKeywordHtml(avaliableKeyword,dispatcher));	
	}
	
	
	@Override
	protected boolean haveNewKeyword(IHttpClient client, String encodeKey) {	
		long beginTime = avaliableKeyword.getBeginTime();
		if(beginTime == 0){
			return true;
		}
		if(avaliableKeyword.atRightTimes()){
			return true;
		}
		if(this.getNewKeywordCount(client, encodeKey, avaliableKeyword.getBeginTime()) > 0){
			return true;
		}
		return false;
	}
	@Override
	protected void addSourceTime() {
		avaliableKeyword.setBeginTime(processSourceTime());
	}


	@Override
	protected String getLogType() {
		return "key";
	}
	
	
	

}
