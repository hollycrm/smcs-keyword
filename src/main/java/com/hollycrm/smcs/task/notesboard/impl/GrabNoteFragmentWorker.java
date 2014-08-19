package com.hollycrm.smcs.task.notesboard.impl;

import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.task.IDispatcher;
import com.hollycrm.smcs.task.impl.GrabPrivateFragmentHtml;
import com.hollycrm.smcs.task.notesboard.AbsGrabNoteMessageWorker;

public class GrabNoteFragmentWorker extends AbsGrabNoteMessageWorker{

	public GrabNoteFragmentWorker(Fragment fragment, IDispatcher dispatcher) {
		super(new GrabPrivateFragmentHtml(fragment, dispatcher), fragment.getBloggerId(), 
				fragment.getGroupId());
	}

	@Override
	protected String getLogType() {
		return "notefragment";
	}

}
