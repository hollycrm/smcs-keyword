package com.hollycrm.smcs.task.notesboard.impl;

import com.hollycrm.smcs.assist.Avaliable;
import com.hollycrm.smcs.assist.AvaliableNote;
import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.entity.fetch.HtmlMaxId;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.httpclient.impl.PrivateHttpClientContainer;
import com.hollycrm.smcs.task.AbsGrabNormalHtml;
import com.hollycrm.smcs.task.IDispatcher;

/**
 * 抓留言
 * @author fly
 *
 */

public class GrabNoteMessageHtml extends AbsGrabNormalHtml{
	
	private final AvaliableNote avaliableNote;

	public GrabNoteMessageHtml(AvaliableNote avaliableNote, IDispatcher dispatcher) {
		super(dispatcher);
		this.avaliableNote = avaliableNote;
	}

	@Override
	protected Avaliable getAvaliable() {
		return avaliableNote;
	}

	@Override
	protected String getMentionType() {
		return MENTION_NOTE;
	}

	@Override
	protected Object getThreadId() {
		return avaliableNote.getBloggerId();
	}

	@Override
	protected HtmlMaxId findHtmlMaxId() {
		return maxIdService.findHtmlMaxIdByPrivate(avaliableNote.getBloggerId(), avaliableNote.getGroupId(),
				getMentionType(), mediaType);
	}

	@Override
	protected HtmlMaxId createdHtmlMaxId(Long maxId) {
		return createdHtmlMaxId(avaliableNote.getBloggerId(), avaliableNote.getGroupId(),
				null, maxId, mediaType, getMentionType(), null);
	}

	@Override
	protected void generateAndSaveFragment(Long maxId, Long uid) {
		this.createAndSaveFragment(new Fragment(avaliableNote.getGroupId(), getMentionType(), sinceId, maxId,
				avaliableNote.getBloggerId(), uid, null, avaliableNote.getType()));
	}

	@Override
	public IHttpClient obtainHttpClient() {
		return PrivateHttpClientContainer.obtainHttpClient(avaliableNote.getBloggerId());
	}

}
