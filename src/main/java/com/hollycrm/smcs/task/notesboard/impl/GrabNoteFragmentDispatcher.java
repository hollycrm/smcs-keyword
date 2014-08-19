package com.hollycrm.smcs.task.notesboard.impl;

import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.task.AbsGrabMessageWorker;
import com.hollycrm.smcs.task.GrabFragmentDispatcher;

/**
 * 抓取留言段
 * @author fly
 *
 */
public class GrabNoteFragmentDispatcher extends GrabFragmentDispatcher{

	@Override
	public AbsGrabMessageWorker buildWorker(Fragment fragment) {
		return new GrabNoteFragmentWorker(fragment, this);
	}

	@Override
	protected String getFragmentType() {
		return NOTE_FRAGMENT;
	}

}
