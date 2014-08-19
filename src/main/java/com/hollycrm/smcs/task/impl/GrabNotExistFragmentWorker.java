package com.hollycrm.smcs.task.impl;

import java.util.concurrent.CountDownLatch;

import com.hollycrm.smcs.task.AbsGrabMessageWorker;


public class GrabNotExistFragmentWorker extends AbsGrabMessageWorker{
	private final CountDownLatch latch;
	public GrabNotExistFragmentWorker(CountDownLatch latch){
		
		this.latch = latch;
	}
	@Override
	public void run() {
		latch.countDown();
	}

	@Override
	protected void filterEntiry(Long bloggerId,String entity) throws Exception {
		
	}
	@Override
	protected String getLogType() {
		return null;
	}

}
