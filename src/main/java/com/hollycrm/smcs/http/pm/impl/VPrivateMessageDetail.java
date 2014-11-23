package com.hollycrm.smcs.http.pm.impl;

import java.util.Date;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.pm.AbsPrivateMessageDetail;
import com.hollycrm.smcs.task.AbsGrabMessageWorker;
import com.hollycrm.smcs.task.IGrabHtml;
import com.hollycrm.smcs.task.pm.AbsGrabPrivateMessageWorker;



public class VPrivateMessageDetail extends AbsPrivateMessageDetail{

	public VPrivateMessageDetail(Long bloggerId, Long groupId) {
		super(bloggerId, groupId);
	}


	@Override
	public String getAccessPageUrl() {
		return AppConfig.get("privateVTalkUrl")+uid;
	}


	@Override
	protected String getSelectPageStyle() {
		return V_PAGE_STYLE;
	}


	@Override
	public Date getMsgDate(Element element, AbsGrabPrivateMessageWorker worker) {
		return worker.date(element.select("em.date").first().text().trim());
	}


	@Override
	public boolean isReceiveMsg(Element element) {
		return "0".equals(element.attr("is-send"));
	}


	@Override
	public String getAttachStyle() {
		return "dd.piclist";
	}


	@Override
	public String getFileName(Element element) {
		return element.select("span.imgname").first().text().trim();
	}


	@Override
	public String getFileSize(Element element) {
		return element.select("span.txt_filesize").first().text().trim();
	}


	@Override
	public String getUploadUrl(Element element) {
		Element href = element.select("a").first();// 获取附件地址		
		return "http://e.weibo.com"+href.attr("href");
		
	}
	

	@Override
	protected String getMsgTextStyle() {
		return "div.txt";
	}


	@Override
	protected String formatMsgText(String text) {
		return  text.substring(text.indexOf("：") + 1);
	}


	@Override
	public Elements getTheElements(AbsGrabMessageWorker worker,
			IHttpClient client, IGrabHtml grabHtml) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isContinue() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void setCurrentId(Long mid) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getPicStyle() {
		// TODO Auto-generated method stub
		return null;
	}


}
