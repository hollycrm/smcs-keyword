package com.hollycrm.smcs.task.pm.impl;

import com.hollycrm.smcs.assist.AvaliablePm;
import com.hollycrm.smcs.task.AbsDispatcher;

public class GrabPrivateMessageDispatcher extends AbsDispatcher<AvaliablePm, Long>{

	protected static final String PRIVATE = "私信";
	

	@Override
	protected String getName() {
		return PRIVATE;
	}

	@Override
	protected Long getWaingKey(AvaliablePm t) {
		return t.getBloggerId();
	}

	@Override
	protected Long getRunningKey(AvaliablePm t) {
		return t.getBloggerId();
	}

	@Override
	protected void handleAvaliable(AvaliablePm t) {
		waiting.get(t.getBloggerId()).compare(t);
		
	}

	

	@Override
	protected Runnable getWorder(AvaliablePm t) {
		return new GrabPrivateMessageWorker(t, this);
	}

	@Override
	protected long getSleepTime() {
		return HALF_OF_MINUTE;
	}



}
