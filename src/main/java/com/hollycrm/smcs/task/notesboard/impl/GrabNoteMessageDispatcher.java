package com.hollycrm.smcs.task.notesboard.impl;

import com.hollycrm.smcs.assist.AvaliableNote;
import com.hollycrm.smcs.task.AbsDispatcher;


public class GrabNoteMessageDispatcher extends AbsDispatcher<AvaliableNote, Long>{
	
	protected static final String NOTE_MESSAGE = "留言";

	@Override
	protected String getName() {
		return NOTE_MESSAGE;
	}

	@Override
	protected Long getWaingKey(AvaliableNote t) {
		return t.getBloggerId();
	}

	@Override
	protected Long getRunningKey(AvaliableNote t) {
		return t.getBloggerId();
	}

	@Override
	protected void handleAvaliable(AvaliableNote t) {
		waiting.get(t.getBloggerId()).compare(t);
	}

	@Override
	protected Runnable getWorder(AvaliableNote t) {
		return new GrabNoteMessageWorker(t,this);
	}

	@Override
	protected long getSleepTime() {
		return HALF_OF_MINUTE;
	}

}
