package com.hollycrm.smcs.task.board.impl;

import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.task.AbsGrabMessageWorker;
import com.hollycrm.smcs.task.GrabFragmentDispatcher;


public class GrabBoardFragmentDispatcher extends GrabFragmentDispatcher{

	

	@Override
	public AbsGrabMessageWorker buildWorker(Fragment fragment) {
		return new GrabBoardFragmentWorker(fragment, this);
	}

	@Override
	protected String getFragmentType() {
		return BOARD_FRAGMENT;
	}



}
