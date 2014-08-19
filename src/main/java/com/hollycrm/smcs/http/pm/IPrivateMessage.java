package com.hollycrm.smcs.http.pm;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.task.IGrabHtml;
import com.hollycrm.smcs.task.pm.AbsGrabPrivateMessageWorker;

public interface IPrivateMessage {

	/**
	 * 判断html是否是包含有效信息(区分私信html)
	 * @param html
	 * @return
	 */
	boolean containsHtml(String html);
	
	Elements parsehtml(Document document);
	
	/**
	 * 解析mid
	 * @param element
	 * @return
	 */
	Long getMid(Element element);
	
	/**
	 * 解析页码
	 * @param isFirst
	 * @param document
	 * @return
	 */
	int parsePage(boolean isFirst,Document document);
	
	/**
	 * 判断mid是否在有效范围内
	 * @param grabHtml
	 * @param mid
	 * @return
	 */
	boolean isInRange(IGrabHtml grabHtml,Long mid, Long uidFirstMid);
	
	void endGrab(IGrabHtml grabHtml);
	
	void setCurrentMaxId(Long mid);
	
	
	
	void errorDeal(Long currentId, Long firstMid, IGrabHtml grabHtml);	
	
	long getPageSleepTime();
	
	String getAccessPageUrl();
	
	void dealMsgElement(Element element, AbsGrabPrivateMessageWorker worker , IHttpClient client, Long mid) throws Exception;
	
	void endBeforeMessage(Long currentId, Long uid, Long groupId);
	
}
