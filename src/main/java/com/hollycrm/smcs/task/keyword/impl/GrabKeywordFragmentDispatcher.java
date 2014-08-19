package com.hollycrm.smcs.task.keyword.impl;

import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.task.AbsGrabMessageWorker;
import com.hollycrm.smcs.task.GrabFragmentDispatcher;

public class GrabKeywordFragmentDispatcher extends GrabFragmentDispatcher{

	

	@Override
	public AbsGrabMessageWorker buildWorker(Fragment fragment) {
		return new GrabKeywordFragmentWorker(fragment, this);
	}

	@Override
	protected String getFragmentType() {
		return KEYWORD_FRAGMENT;
	}



}
