package com.hollycrm.smcs.task.keyword.impl;

import com.hollycrm.smcs.assist.AvaliableKeywordGroup;
import com.hollycrm.smcs.task.AbsDispatcher;


public class TransformKeywordDispatcher extends AbsDispatcher<AvaliableKeywordGroup, Long>{

	
	

	@Override
	protected String getName() {
		return "转化关键字";
	}

	@Override
	protected Long getWaingKey(AvaliableKeywordGroup t) {
		return t.getGroupId();
	}

	@Override
	protected Long getRunningKey(AvaliableKeywordGroup t) {
		return	t.getGroupId();
	}

	@Override
	protected void handleAvaliable(AvaliableKeywordGroup t) {
		
	}

	

	@Override
	protected Runnable getWorder(AvaliableKeywordGroup t) {
		return new TransformKeywordWorker(t,this);
	}

	@Override
	protected long getSleepTime() {
		return ONE_SECOND*10;
	}

}
