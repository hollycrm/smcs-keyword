package com.hollycrm.smcs.http.pm.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.http.pm.AbsPrivateMessageList;



public class VPrivateMessageList extends AbsPrivateMessageList{

	public VPrivateMessageList(Long bloggerId, Long groupId) {
		super(bloggerId, groupId);
	}
	
	@Override
	protected Element getMidElement(Element element) {
		return element.getElementsByAttributeValue("action-type","forwardMessage").first();
	}

	@Override
	public String getAccessPageUrl() {
		return AppConfig.get("privateVMessageUrl");
	}

	@Override
	public Elements parsehtml(Document document) {
		return document.select("dl.private_list");
	}

	@Override
	protected String getSelectPageStyle() {
		return V_PAGE_STYLE;
	}

	

}
