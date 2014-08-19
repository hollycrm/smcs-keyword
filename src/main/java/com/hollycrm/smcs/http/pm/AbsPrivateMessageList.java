package com.hollycrm.smcs.http.pm;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.task.AbsGrabMessageWorker;
import com.hollycrm.smcs.task.IGrabHtml;
import com.hollycrm.smcs.task.pm.AbsGrabPrivateMessageWorker;

public abstract class AbsPrivateMessageList extends AbsPrivateMessage implements IPrivateMessageList{

	public AbsPrivateMessageList(Long bloggerId, Long groupId){
		super(bloggerId, groupId);
	}

	
	@Override
	public void endBeforeMessage(Long currentId, Long uid, Long groupId) {
		
	}


	@Override
	public void setCurrentMaxId(Long maxId) {
		super.setCurrentMaxId(maxId);
	}

	@Override
	public boolean containsHtml(String html) {		
		return super.containsHtml(html);
	}

	@Override
	public Long getMid(Element element) {
		return super.getMid(element);
	}
	
	@Override
	public int parsePage(boolean isFirst, Document document) {
		return super.parsePage(isFirst, document);
	}
	

	@Override
	public void dealMsgElement(Element element, AbsGrabPrivateMessageWorker worker, IHttpClient client, Long mid) throws Exception {
		worker.readMessageList(element, client);
	}

	
	@Override
	protected String getContainsHtml() {
		return MESSAGE_LIST;
	}
	

	@Override
	public boolean isInRange(IGrabHtml grabHtml, Long mid, Long uidFirstMid) {
		return grabHtml.isInRange(mid);
	}

	@Override
	public void endGrab(IGrabHtml grabHtml) {
		grabHtml.endGrab(maxId);
	}

	@Override
	public void errorDeal(Long currentId, Long firstMid, IGrabHtml grabHtml) {
		grabHtml.endGrab(firstMid);
	}

	@Override
	protected int getPage(boolean isFirst, int page) {
		if(isFirst){
			if(page > AbsGrabMessageWorker.FIRST_GRAB_PAGE){
				return AbsGrabMessageWorker.FIRST_GRAB_PAGE;
			}			
		}
		return page;
		
	}


	@Override
	public long getPageSleepTime() {
		return 1000L;
	}

}
