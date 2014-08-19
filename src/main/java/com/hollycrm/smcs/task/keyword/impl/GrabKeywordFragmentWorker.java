package com.hollycrm.smcs.task.keyword.impl;

import com.hollycrm.smcs.assist.AvaliableKeyword;
import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.task.IDispatcher;
import com.hollycrm.smcs.task.impl.GrabFragmentHtml;
import com.hollycrm.smcs.task.keyword.AbsGrabKeywordWorker;

public class GrabKeywordFragmentWorker extends AbsGrabKeywordWorker{
	
	
	public GrabKeywordFragmentWorker(Fragment fragment ,IDispatcher dispatcher){
		super(new AvaliableKeyword(fragment.getKey(), fragment.getExclusiveKey(), fragment.getConditionId(), 
				fragment.getGroupId(), 60000L, fragment.getProvince(), fragment.getCity()), new GrabFragmentHtml(fragment,dispatcher));
	}

	
	@Override
	protected void addSourceTime() {
		
	}

	@Override
	protected boolean haveNewKeyword(IHttpClient client, String encodeKey) {
		return true;
	}


	@Override
	protected String getLogType() {
		return "keyfragment";
	}
	


}
