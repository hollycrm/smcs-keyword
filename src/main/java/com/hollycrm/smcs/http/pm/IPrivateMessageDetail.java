package com.hollycrm.smcs.http.pm;

import java.util.Date;

import org.jsoup.nodes.Element;

import com.hollycrm.smcs.task.pm.AbsGrabPrivateMessageWorker;

public interface IPrivateMessageDetail extends IPrivateMessage{
	
	void setUid(Long uid);	
		
	String getMsgText(Element element);
	
	Date getMsgDate(Element element, AbsGrabPrivateMessageWorker worker);
	
	boolean isReceiveMsg(Element element);
	
	String getAttachStyle();
	
	String getPicStyle();
	
	String getFileName(Element element);
	
	String getFileSize(Element element);
	
	String getUploadUrl(Element element);
	
	
}
