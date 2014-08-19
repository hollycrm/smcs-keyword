package com.hollycrm.smcs.task.pm.impl;

import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.task.AbsGrabMessageWorker;
import com.hollycrm.smcs.task.GrabFragmentDispatcher;


public class GrabPrivateFragmentDispatcher extends GrabFragmentDispatcher{

	

	@Override
	public AbsGrabMessageWorker buildWorker(Fragment fragment) {
		return new GrabPrivateMessageFragmentWorker(fragment, this);
	}

	@Override
	protected String getFragmentType() {
		return PRIVATE_FRAGMENT;
	}



}
