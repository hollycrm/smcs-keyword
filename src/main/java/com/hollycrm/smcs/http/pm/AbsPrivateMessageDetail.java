package com.hollycrm.smcs.http.pm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.entity.fetch.PrivateMax;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.service.fetch.PrivateMaxService;
import com.hollycrm.smcs.service.message.MessageService;
import com.hollycrm.smcs.task.IGrabHtml;
import com.hollycrm.smcs.task.pm.AbsGrabPrivateMessageWorker;

public abstract class AbsPrivateMessageDetail extends AbsPrivateMessage implements IPrivateMessageDetail {

	
	@Override
	public void setCurrentMaxId(Long maxId) {
		super.setCurrentMaxId(maxId);
	}

	@Override
	public boolean containsHtml(String html) {		
		return super.containsHtml(html);
	}

	@Override
	public Long getMid(Element element) {
		return super.getMid(element);
	}
	
	@Override
	public int parsePage(boolean isFirst, Document document) {
		return super.parsePage(isFirst, document);
	}
	
	@Override
	public void dealMsgElement(Element element, AbsGrabPrivateMessageWorker worker, IHttpClient client, Long mid) {
		worker.readMessageDetail(element, this, client, mid);
	}

	private PrivateMax privateMaxId;
	public AbsPrivateMessageDetail(Long bloggerId, Long groupId) {
		super(bloggerId, groupId);
	}
	
	

	@Override
	public boolean isInRange(IGrabHtml grabHtml, Long mid, Long uidFirstMid) {
		if(privateMaxId == null){
			return true;
		}
		return mid > privateMaxId.getMaxId();
	}

	@Override
	public void endGrab(IGrabHtml grabHtml) {
		if(maxId != null){
			if(privateMaxId != null){
				privateMaxId.setMaxId(maxId);
				ApplicationContextHolder.getBean(PrivateMaxService.class).save(privateMaxId);
				return;
			}
			PrivateMax privateMax = new PrivateMax();
			privateMax.setBloggerId(bloggerId);
			privateMax.setMaxId(maxId);
			privateMax.setMediaType("w");
			privateMax.setUid(uid);
			privateMax.setGroupId(groupId);
			ApplicationContextHolder.getBean(PrivateMaxService.class).save(privateMax);
	}
	}
	@Override
	public void setUid(Long uid) {
		this.uid = uid;		
		privateMaxId = ApplicationContextHolder.getBean(PrivateMaxService.class).find(uid,bloggerId,"w",groupId);
	}

	@Override
	public void errorDeal(Long currentId, Long firstMid, IGrabHtml grabHtml) {
		grabHtml.errorDeal(currentId, firstMid, uid);
		endGrab(grabHtml);
	}

	@Override
	public void endBeforeMessage(Long currentId, Long uid, Long groupId) {
		if(currentId == null){
			return;
		}
		ApplicationContextHolder.getBean(MessageService.class).endBeforePrivateMessage(currentId, uid, groupId);
	}

	@Override
	public long getPageSleepTime() {
		return 2000L;
	}

	@Override
	protected int getPage(boolean isFirst, int page) {
		return page;
	}

	@Override
	protected String getContainsHtml() {
		return MESSAGE_DETAIL;
	}



	@Override
	protected Element getMidElement(Element element) {
		return element.getElementsByAttributeValue("action-type","delMessage").first();
	}

	@Override
	public String getAccessPageUrl() {
		return AppConfig.get("privateTalkUrl");
	}

	@Override
	public Elements parsehtml(Document document) {
		return document.getElementsByAttributeValue("node-type", "messageList").first().select("div.R_msg");
	}

	@Override
	public String getMsgText(Element element) {
		return face(element.select(getMsgTextStyle()).first().toString());
	}
	
	private String face(String bodyHtml){
		while (true) {
			Document body = Jsoup.parseBodyFragment(bodyHtml);	
			Element imgPart = body.select("img").first();
			if (imgPart == null) {
				break;
			}
			String title = imgPart.attr("title");			
			bodyHtml = bodyHtml.replace(imgPart.toString(), title);
		}
		return formatMsgText(Jsoup.parseBodyFragment(bodyHtml).select(getMsgTextStyle()).first()
				.text().trim());
		
	}
	
	protected abstract String formatMsgText(String text);
	
	/**获取内容的样式**/
	protected abstract String getMsgTextStyle();

}
