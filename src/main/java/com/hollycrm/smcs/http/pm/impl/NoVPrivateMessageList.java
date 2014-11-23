package com.hollycrm.smcs.http.pm.impl;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.pm.AbsPrivateMessageList;
import com.hollycrm.smcs.task.AbsGrabMessageWorker;
import com.hollycrm.smcs.task.IGrabHtml;
import com.hollycrm.smcs.util.JsonUtil;

public class NoVPrivateMessageList extends AbsPrivateMessageList{

	private int page = 1;
	
	private boolean isContunue = false;


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
	protected String getSelectPageStyle() {
		return NO_V_PAGE_STYLE;
	}


	@Override
	public Elements getTheElements(AbsGrabMessageWorker worker, IHttpClient client, IGrabHtml grabHtml) throws Exception {
		String url = AppConfig.get("privateMessageUrl");
		Elements elements = worker.script(client, url + "&page="+page);
		page++;
		for(Element element :elements){
			String html = element.html();
			if (containsHtml(html)) {	
				Document messageListDoc = doc(html);
				if(isHasNextPage(messageListDoc)) {
					isContunue = true;
				} else {
					isContunue = false;
				}
				return parsehtml(messageListDoc);
			}
		}
		return null;
	}
	
	boolean isHasNextPage(Document doc){
		Element page = doc.select("div.W_pages").first();
		if(page != null ){
			Element next = page.select("a.next").first();
			if(!next.hasClass("page_dis")){
				return true;
			}
			return false;
		}
		return false;
	}
	
	public Elements parsehtml(Document document) {
		return document.select("div.private_list");
	}
	
	protected Document doc(String html) {
		String jsonList = html.substring(8, html.length() - 1);
		Map map = JsonUtil.getMap4Json(jsonList);
		return Jsoup.parse((String) map.get("html"));
	}


	@Override
	public boolean isContinue() {
		if(page == 1) {
			return true;
		}
		return isContunue;
	}


	@Override
	public void setCurrentId(Long mid) {
		// TODO Auto-generated method stub
		
	}



}
