package com.hollycrm.smcs.task.pm.impl;

import java.util.List;

import com.hollycrm.smcs.assist.AvaliablePm;
import com.hollycrm.smcs.atomic.impl.UnreadAtomic;
import com.hollycrm.smcs.entity.base.IdOauth;
import com.hollycrm.smcs.http.pm.IPrivateMessage;
import com.hollycrm.smcs.task.IDispatcher;
import com.hollycrm.smcs.task.pm.AbsGrabPrivateMessageWorker;

public class GrabPrivateMessageWorker extends AbsGrabPrivateMessageWorker{
	
	private final AvaliablePm avaliablePm;
	public GrabPrivateMessageWorker(AvaliablePm avaliablePm,IDispatcher iDispatcher){
		super(new GrabPrivateMessageHtml(iDispatcher,avaliablePm), avaliablePm.getBloggerId(), 
				avaliablePm.getGroupId(), avaliablePm.getType());
		this.avaliablePm = avaliablePm;
	}

	@Override
	protected boolean isExit(boolean isFirst) {
		if(isFirst){
			return false;
		}
		if(avaliablePm.atRightTimes()){
			return false;
		}
		List<IdOauth> list=idOauthService.getOauthList(avaliablePm.getGroupId(),avaliablePm.getMediaType());
		Long dm=0L;
		UnreadAtomic ua=new UnreadAtomic(list);
		try {
			dm=ua.call().getDm();
		} catch (Exception e) {
			return false;
		}
		if(dm==0){
			return true;
		}
		return false;
		
	}

	@Override
	protected IPrivateMessage getIPrivateMessage() {
		return iPrivateMessageList;
	}

	@Override
	protected String getLogType() {
		return "private";
	}

}
