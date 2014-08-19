package com.hollycrm.smcs.http.pm;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public abstract class AbsPrivateMessage {
	

	protected static final String MESSAGE_DETAIL = "\"pid\":\"pl_content_messageDetail\"";
	
	protected static final String MESSAGE_LIST = "\"pid\":\"pl_content_messageList\"";
	
	protected static final String V_PAGE_STYLE = "div.W_pages";
	
	protected static final String NO_V_PAGE_STYLE = "div.W_pages_minibtn";
	
	protected Long maxId;
	
	protected Long uid;
	
	protected Long bloggerId;
	
	protected final Long groupId;
	
	
	public AbsPrivateMessage(Long bloggerId, Long groupId){
		this.bloggerId = bloggerId;
		this.groupId = groupId;
	}
	
	public void setCurrentMaxId(Long maxId) {
		this.maxId = maxId;
	}

	public boolean containsHtml(String html) {		
		if(html.contains(getContainsHtml())){
			return true;
		}
		return false;		
	}

	

	public Long getMid(Element element) {
		String mid = getMidElement(element).attr("action-data");
		mid = mid.substring(mid.indexOf("=") + 1, mid.indexOf("&"));// 抓取mid
		return Long.parseLong(mid);
	}
	
	public int parsePage(boolean isFirst, Document document) {
		int page = parseHtmlPage(document);
		return getPage(isFirst, page);
	}
	
	protected int parseHtmlPage(Document doc) {
		String maxNum = "1";
		Element pageList = doc.select(getSelectPageStyle()).first();
		if (pageList != null) {
			String pageStr = pageList.text().trim();
			if (pageStr.indexOf("下一页") != -1) {
				if (pageStr.indexOf("...") != -1) {
					maxNum = pageStr.substring(pageStr.indexOf("...") + 4, pageStr.indexOf("下一页") - 1);
				} else {
					maxNum = pageStr.substring(pageStr.indexOf("下一页") - 2, pageStr.indexOf("下一页") - 1);
				}
			}
		}

		return Integer.parseInt(maxNum);
	}
	
	protected abstract String getSelectPageStyle();
	
	
	
	

	protected abstract int getPage(boolean isFirst,int page);
	
	protected abstract String getContainsHtml();
	
	
	protected abstract Element getMidElement(Element element);

}
