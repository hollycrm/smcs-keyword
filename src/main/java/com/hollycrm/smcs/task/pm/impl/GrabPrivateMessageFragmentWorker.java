package com.hollycrm.smcs.task.pm.impl;

import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.http.pm.IPrivateMessage;
import com.hollycrm.smcs.task.IDispatcher;
import com.hollycrm.smcs.task.impl.GrabPrivateFragmentHtml;
import com.hollycrm.smcs.task.pm.AbsGrabPrivateMessageWorker;

public class GrabPrivateMessageFragmentWorker extends AbsGrabPrivateMessageWorker{

	
	
	public GrabPrivateMessageFragmentWorker(Fragment fragment, IDispatcher dispatcher) {
		super(new GrabPrivateFragmentHtml(fragment,dispatcher), fragment.getBloggerId(), 
				fragment.getGroupId(), fragment.getBlogType());	
		uid = fragment.getUid();
		iPrivateMessageDetail.setUid(uid);
		
	}

	@Override
	protected boolean isExit(boolean isFirst) {
		return false;
	}

	@Override
	protected IPrivateMessage getIPrivateMessage() {
		return iPrivateMessageDetail;
	}

	@Override
	protected String getLogType() {
		return "privatefragment";
	}

	


	

}
