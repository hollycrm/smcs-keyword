package com.hollycrm.smcs.task.pm.impl;

import com.hollycrm.smcs.assist.Avaliable;
import com.hollycrm.smcs.assist.AvaliablePm;
import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.entity.fetch.HtmlMaxId;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.httpclient.impl.PrivateHttpClientContainer;
import com.hollycrm.smcs.task.AbsGrabNormalHtml;
import com.hollycrm.smcs.task.IDispatcher;

public class GrabPrivateMessageHtml extends AbsGrabNormalHtml{

	

	private final AvaliablePm avaliablePm;
	public GrabPrivateMessageHtml(IDispatcher dispatcher, AvaliablePm avaliablePm) {
		super(dispatcher);
		this.avaliablePm = avaliablePm;
	}

	@Override
	protected String getMentionType() {
		return MENTION_PRIVATE;
	}

	@Override
	protected Object getThreadId() {
		return avaliablePm.getBloggerId();
	}

	@Override
	protected HtmlMaxId findHtmlMaxId() {
		return maxIdService.findHtmlMaxIdByPrivate(avaliablePm.getBloggerId(), avaliablePm.getGroupId(), 
				getMentionType(), avaliablePm.getMediaType());
	}

	@Override
	protected HtmlMaxId createdHtmlMaxId(Long maxId) {
		return createdHtmlMaxId(avaliablePm.getBloggerId(), avaliablePm.getGroupId(), null, maxId, 
				avaliablePm.getMediaType(), getMentionType(), null);
	}

	@Override
	protected void generateAndSaveFragment(Long maxId, Long uid) {
		createAndSaveFragment(new Fragment(avaliablePm.getGroupId(), getMentionType(), sinceId, maxId, avaliablePm.getBloggerId(),
				uid, null,avaliablePm.getType()));
	}

	@Override
	protected Avaliable getAvaliable() {
		return avaliablePm;
	}

	@Override
	public IHttpClient obtainHttpClient() {
		return PrivateHttpClientContainer.obtainHttpClient(avaliablePm.getBloggerId());
	}

	@Override
	public void errorDeal(Long currentId, Long firstMid, Long uid) {
		if((sinceId != null) && (currentId != null) && (uid != null)) {			
			generateAndSaveFragment(currentId,uid);
		}
	}

	

}
