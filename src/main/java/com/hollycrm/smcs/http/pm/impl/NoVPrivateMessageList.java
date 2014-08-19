package com.hollycrm.smcs.http.pm.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.http.pm.AbsPrivateMessageList;
import com.hollycrm.smcs.task.IGrabHtml;

public class NoVPrivateMessageList extends AbsPrivateMessageList{



	public NoVPrivateMessageList(Long bloggerId, Long groupId) {
		super(bloggerId, groupId);
	}


	@Override
	public Long getMid(Element element) {
		return null;
	}

	@Override
	public boolean isInRange(IGrabHtml grabHtml, Long mid, Long uidFirstMid) {
		if(uidFirstMid == null){
			return true;
		}
		return grabHtml.isInRange(uidFirstMid);
		
	}

	@Override
	protected Element getMidElement(Element element) {
		return null;
	}

	@Override
	public String getAccessPageUrl() {
		return AppConfig.get("privateMessageUrl");
	}

	@Override
	public Elements parsehtml(Document document) {
		return document.select("div.WB_msg_type");
	}


	@Override
	protected String getSelectPageStyle() {
		return NO_V_PAGE_STYLE;
	}



}
