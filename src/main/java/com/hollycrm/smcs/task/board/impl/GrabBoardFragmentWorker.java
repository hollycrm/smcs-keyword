package com.hollycrm.smcs.task.board.impl;

import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.task.IDispatcher;
import com.hollycrm.smcs.task.board.AbsGrabBoardMessageWorker;
import com.hollycrm.smcs.task.impl.GrabFragmentHtml;

public class GrabBoardFragmentWorker extends AbsGrabBoardMessageWorker{

	public GrabBoardFragmentWorker(Fragment fragment , IDispatcher dispatcher) {
		super(new GrabFragmentHtml(fragment,dispatcher));
		this.uid = fragment.getBloggerId();
		this.groupId = fragment.getGroupId();
	}

	@Override
	protected String getLogType() {
		return "boardfragment";
	}

}
