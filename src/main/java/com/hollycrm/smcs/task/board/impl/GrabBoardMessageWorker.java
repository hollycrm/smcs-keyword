package com.hollycrm.smcs.task.board.impl;


import com.hollycrm.smcs.assist.AvaliableBoard;
import com.hollycrm.smcs.task.IDispatcher;
import com.hollycrm.smcs.task.board.AbsGrabBoardMessageWorker;

public class GrabBoardMessageWorker extends AbsGrabBoardMessageWorker{

	public GrabBoardMessageWorker(IDispatcher iDispatcher,AvaliableBoard board) {
		super(new GrabBoardHtml(iDispatcher,board));
		this.uid = board.getBloggerId();
		this.groupId = board.getGroupId();
	}

	@Override
	protected String getLogType() {
		return "board";
	}

	

}
