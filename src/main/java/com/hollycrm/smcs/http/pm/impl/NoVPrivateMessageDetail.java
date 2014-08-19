package com.hollycrm.smcs.http.pm.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.pm.AbsPrivateMessageDetail;
import com.hollycrm.smcs.task.pm.AbsGrabPrivateMessageWorker;

public class NoVPrivateMessageDetail extends AbsPrivateMessageDetail{

	@Override
	public Elements parsehtml(Document document) {
		return document.select("div.msg_dialogue").first().children();
	}

	private Date date;

	@Override
	public Long getMid(Element element) {
		if(element.hasClass("msg_time_line")){
			return Long.MAX_VALUE;
		}else if(element.hasClass("msg_dialogue_list")){
			return Long.parseLong(element.attr("mid"));
		}
		return 1L;
	}

	public NoVPrivateMessageDetail(Long bloggerId, Long groupId) {
		super(bloggerId, groupId);
	}

	@Override
	public String getAccessPageUrl() {
		return AppConfig.get("privateTalkUrl")+uid;
	}

	@Override
	protected String getSelectPageStyle() {
		return NO_V_PAGE_STYLE;
	}

	

	@Override
	public void dealMsgElement(Element element, AbsGrabPrivateMessageWorker worker, IHttpClient client, Long mid) {
		if(element.hasClass("msg_time_line")){
			date = worker.date(element.select("legend.time_tit").first().text());
		}else if(element.hasClass("msg_dialogue_list")){
			super.dealMsgElement(element, worker, client, mid);
		}
	}

	

	@Override
	public Date getMsgDate(Element element, AbsGrabPrivateMessageWorker worker) {
		return date;
	}

	@Override
	public boolean isReceiveMsg(Element element) {
		String nDate = element.attr("n-data");
		if(StringUtils.isBlank(nDate)){
			return false;
		}else if(nDate.indexOf("black=0") != -1){
			return true;
		}
		return false;
	}

	@Override
	public String getAttachStyle() {
		return "div.msg_attachment";
	}

	@Override
	public String getFileName(Element element) {
		Elements elements = element.select("em.file_name");
		if(elements.isEmpty()){
			return "";
		}
		return parseFileName(elements.first().text());
		
	}

	@Override
	public String getFileSize(Element element) {
		Elements elements = element.select("em.file_size");
		if(elements.isEmpty()){
			return null;
		}
		return elements.first().text();
	}

	@Override
	public String getUploadUrl(Element element) {
		Elements elements = element.select("img");
		//文本附件
		if(elements.isEmpty()){
			elements = element.select("a");
			Element href = elements.first();
			if(href.text().indexOf("下载") != -1){
				return href.attr("href");
			}
		//图片附件
		}else{
			return elements.attr("src");
		}
		return null;
	}

	private String parseFileName(String fileName){
		
		fileName = fileName.replaceAll(":|：", ":");
		int index = fileName.indexOf(":");
		if(index != -1){
			return fileName.substring(index + 1);
		}
		return fileName;
		
	}
	

	@Override
	protected String getMsgTextStyle() {
		return "p.msg_dia_txt";
	}

	@Override
	protected String formatMsgText(String text) {
		return text;
	}
	
	


	

}
