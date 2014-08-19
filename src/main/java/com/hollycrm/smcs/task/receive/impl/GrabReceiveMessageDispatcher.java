package com.hollycrm.smcs.task.receive.impl;

import com.hollycrm.smcs.assist.AvaliableReceiveMessage;
import com.hollycrm.smcs.task.AbsDispatcher;
import com.hollycrm.smcs.task.receive.GrabReceiveMessageWorker;

/**
 * 
 * @author dingqj
 *
 */
public class GrabReceiveMessageDispatcher extends AbsDispatcher<AvaliableReceiveMessage, Long>{

	@Override
	protected String getName() {
		return "消息接口";
	}

	@Override
	protected Long getWaingKey(AvaliableReceiveMessage t) {
		return t.getBloggerId();
				
	}

	@Override
	protected Long getRunningKey(AvaliableReceiveMessage t) {
		return t.getBloggerId();
	}

	@Override
	protected void handleAvaliable(AvaliableReceiveMessage t) {
		
	}

	@Override
	protected Runnable getWorder(AvaliableReceiveMessage t) {
		return new GrabReceiveMessageWorker(t, this);
	}

	@Override
	protected long getSleepTime() {
		return HALF_OF_MINUTE;
	}

}
