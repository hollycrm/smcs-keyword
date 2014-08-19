package com.hollycrm.smcs.task.board.impl;

import com.hollycrm.smcs.assist.Avaliable;
import com.hollycrm.smcs.assist.AvaliableBoard;
import com.hollycrm.smcs.task.AbsDispatcher;

public class GrabBoardMessageDispatcher extends AbsDispatcher<AvaliableBoard, Long> {

	protected static final String BOARD = "留言板";
	

	@Override
	protected Long getWaingKey(AvaliableBoard t) {
		return t.getBloggerId();
	}

	@Override
	protected Long getRunningKey(AvaliableBoard t) {
		return t.getBloggerId();
	}

	@Override
	protected void handleAvaliable(AvaliableBoard t) {
		AvaliableBoard avaliableBoard = waiting.get(t.getBloggerId());
		if(t.getGroupId() != avaliableBoard.getGroupId() ){
			avaliableBoard.setGroupId(t.getGroupId());
			
		}
		avaliableBoard.setInterval(t.getInterval());
		
	}

	@Override
	protected Runnable getWorder(AvaliableBoard t) {
		return new GrabBoardMessageWorker(this,t);
	}

	@Override
	protected String getName() {
		return BOARD;
	}



	@Override
	protected long getSleepTime() {
		return Avaliable.TWO_MINUTE;
	}

}
