package com.hollycrm.smcs.task.keyword.impl;

import com.hollycrm.smcs.assist.Avaliable;
import com.hollycrm.smcs.assist.AvaliableKeyword;
import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.entity.fetch.HtmlMaxId;
import com.hollycrm.smcs.task.AbsGrabNormalHtml;
import com.hollycrm.smcs.task.IDispatcher;


public class GrabKeywordHtml extends AbsGrabNormalHtml{

	private final AvaliableKeyword avaliableKeyword;	
	public GrabKeywordHtml(AvaliableKeyword avaliableKeyword, IDispatcher iDispatcher) {
		super(iDispatcher);
		this.avaliableKeyword = avaliableKeyword;
	}

	@Override
	protected String getMentionType() {
		return MENTION_SEARCH;
	}

	@Override
	protected HtmlMaxId findHtmlMaxId() {
		return maxIdService.findHtmlMaxIdByKeyword(avaliableKeyword.getKey(),avaliableKeyword.getGroupId(), getMentionType(), mediaType);
	}

	@Override
	protected Object getThreadId() {
		return avaliableKeyword.getConditionId();
	}

	@Override
	protected void generateAndSaveFragment(Long maxId, Long uid) {
		createAndSaveFragment(new Fragment(avaliableKeyword.getGroupId(), avaliableKeyword.getKey(), getMentionType(), 
				sinceId, maxId, avaliableKeyword.getConditionId(),avaliableKeyword.getExclusiveKey(), avaliableKeyword.getProvince(), 
				avaliableKeyword.getCity()));		
	}

	@Override
	protected HtmlMaxId createdHtmlMaxId(Long maxId) {
		return createdHtmlMaxId(null, avaliableKeyword.getGroupId(), avaliableKeyword.getKey(), maxId, "w", getMentionType(), null);
	}

	@Override
	protected Avaliable getAvaliable() {
		return avaliableKeyword;
	}

	


}
