package com.hollycrm.smcs.task.notesboard.impl;

import com.hollycrm.smcs.assist.AvaliableNote;
import com.hollycrm.smcs.task.IDispatcher;
import com.hollycrm.smcs.task.notesboard.AbsGrabNoteMessageWorker;

public class GrabNoteMessageWorker extends AbsGrabNoteMessageWorker{
	
	
	public GrabNoteMessageWorker(AvaliableNote avaliableNote,IDispatcher iDispatcher){
		super(new GrabNoteMessageHtml(avaliableNote, iDispatcher), avaliableNote.getBloggerId(), 
				avaliableNote.getGroupId());
	}

	@Override
	protected String getLogType() {
		return "note";
	}

}
