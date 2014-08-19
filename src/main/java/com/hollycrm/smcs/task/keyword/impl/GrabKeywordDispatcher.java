package com.hollycrm.smcs.task.keyword.impl;

import com.hollycrm.smcs.assist.AvaliableKeyword;
import com.hollycrm.smcs.task.AbsDispatcher;

public class GrabKeywordDispatcher extends AbsDispatcher<AvaliableKeyword, Long> {

	protected static final String SEACHER = "关键字";

	@Override
	protected String getName() {
		return SEACHER;
	}

	@Override
	protected Long getWaingKey(AvaliableKeyword t) {
		return t.getConditionId();
	}

	@Override
	protected Long getRunningKey(AvaliableKeyword t) {
		return t.getConditionId();
	}

	@Override
	protected void handleAvaliable(AvaliableKeyword t) {
		AvaliableKeyword temp = waiting.get(t.getConditionId());
		temp.setKey(t.getKey());
		temp.setExclusiveKey(t.getExclusiveKey());
		temp.setProvince(t.getProvince());
		temp.setCity(t.getCity());
		temp.setInterval(t.getInterval());
	}

	

	@Override
	protected Runnable getWorder(AvaliableKeyword t) {
		return new GrabKeywordWorker(this,t);
	}

	@Override
	protected long getSleepTime() {
		//return HALF_OF_MINUTE;
		return 10000l;
	}



	

	

	

}
