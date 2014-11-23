package com.hollycrm.smcs.http.pm.impl;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.pm.AbsPrivateMessageDetail;
import com.hollycrm.smcs.task.AbsGrabMessageWorker;
import com.hollycrm.smcs.task.IGrabHtml;
import com.hollycrm.smcs.task.pm.AbsGrabPrivateMessageWorker;
import com.hollycrm.smcs.util.JsonUtil;

public class NoVPrivateMessageDetail extends AbsPrivateMessageDetail{
	
	
	private long currentId = Long.MAX_VALUE;
	
	private boolean isContinue = true;
	
	private boolean pageFirst = true;
	
	
	private boolean page = false;

	public Elements parsehtml(Document document) {
		return document.select("div.private_dialogue_cont").first().children();
	}

	private Date date;

	@Override
	public Long getMid(Element element) {
		if(element.hasClass("private_dialogue_prompt")){
			return -999L;
		}else if(element.hasClass("msg_bubble_list")){
			Long mid = Long.parseLong(element.attr("mid"));
			if(page) {
				currentId = mid - 1;
				page = false;
			}
			
			return mid;
		} else if(element.hasClass("private_dialogue_more")){
			return -999L;
		}
		return 1L;
	}

	public NoVPrivateMessageDetail(Long bloggerId, Long groupId) {
		super(bloggerId, groupId);
	}

	@Override
	public String getAccessPageUrl() {
		return AppConfig.get("privateTalkUrl");
	}

	@Override
	protected String getSelectPageStyle() {
		return NO_V_PAGE_STYLE;
	}

	

	@Override
	public void dealMsgElement(Element element, AbsGrabPrivateMessageWorker worker, IHttpClient client, Long mid) {
		if(element.hasClass("private_dialogue_prompt")){
			date = worker.date(element.select("legend.prompt_font").first().text());
		}else if(element.hasClass("msg_bubble_list")){
			super.dealMsgElement(element, worker, client, mid);
		} 
	}

	

	@Override
	public Date getMsgDate(Element element, AbsGrabPrivateMessageWorker worker) {
		return date;
	}

	@Override
	public boolean isReceiveMsg(Element element) {
		if(element.hasClass("bubble_l")){
			return true;
		}
		return false;
	}

	@Override
	public String getAttachStyle() {
		return "div.private_file_mod";
	}

	@Override
	public String getFileName(Element element) {
		Elements elements = element.select("span.name");
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
		return "p.page";
	}

	@Override
	protected String formatMsgText(String text) {
		return text;
	}

	@Override
	public Elements getTheElements(AbsGrabMessageWorker worker,
			IHttpClient client, IGrabHtml grabHtml) throws Exception {
		int count = 20;		
		String entity = client.simpleHttpGet(String.format(getAccessPageUrl(), currentId, uid, count, System.currentTimeMillis()));
		
		Map map = JsonUtil.getMap4Json(entity);
		if(!map.get("code").equals("100000")) {
			logger.info("entity:"+entity);
			throw new Exception(String.format("code{%s},message{%s}", map.get("code"), map.get("msg")));
		}
		Map map2 = JsonUtil.getMap4Json(map.get("data").toString());
		Document doc = Jsoup.parse("<div class='messageDetail'>"+map2.get("html")+"</div");	
		pageFirst = true;
		page = true;
		return doc.getElementsByClass("messageDetail").first().children(); 
	}

	@Override
	public boolean isContinue() {
		return isContinue;
		
	}
	
	@Override
	public boolean isInRange(IGrabHtml grabHtml, Long mid, Long uidFirstMid) {
		
		if(privateMaxId == null){
			isContinue = true;
			return true;
		}
		if(mid == -999L){
			return true;
		}
		if(pageFirst) {
			isContinue = mid > privateMaxId.getMaxId();
			pageFirst = false;
			return true;
		}
		return true;
	}

	@Override
	public void setCurrentId(Long mid) {
		currentId = mid;
	}

	@Override
	public String getPicStyle() {
		
		return "div.pic_box";
	}
	
	


	

}
