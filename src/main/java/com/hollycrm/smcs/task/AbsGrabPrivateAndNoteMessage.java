package com.hollycrm.smcs.task;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.entity.base.ExtraFile;
import com.hollycrm.smcs.entity.event.Event;
import com.hollycrm.smcs.entity.message.DirectMessage;
import com.hollycrm.smcs.entity.message.Message;
import com.hollycrm.smcs.http.httpclient.impl.PrivateHttpClientContainer;
import com.hollycrm.smcs.service.message.MessageService;

/*
 * 抓留言私信
 */
public abstract class AbsGrabPrivateAndNoteMessage extends AbsGrabMessageWorker{	

	protected MessageService messageService;
	
	public AbsGrabPrivateAndNoteMessage(){
		
		messageService = ApplicationContextHolder.getBean(MessageService.class);		
	}
	
	/**
	 * 获取文件大小 
	 * @param fileSize
	 * @return
	 */
	protected  String getFileSize(String fileSize){
		if(StringUtils.isBlank(fileSize)){
			return null;
		}
		return fileSize.substring(1,fileSize.length()-1);
	}
	
	/**
	 * 获取文件后缀
	 * @param fileName
	 * @return
	 */
	protected  String getSuffix(String fileName){
		if(StringUtils.isBlank(fileName)){
			return ".jpg";
		}
		return fileName.substring(fileName.lastIndexOf("."));
	}
	
	/**
	 * 保存私信
	 * @param uid
	 * @param mid
	 * @param date
	 * @param text
	 * @param end
	 * @param list
	 * @param bloggerId
	 * @param groupId
	 * @param event
	 * @return
	 */
	protected Message saveDirectMessage(Long uid, Long mid, Date date, String text, boolean end,List<ExtraFile> list
			, Long bloggerId, Long groupId, Event event) {		
		DirectMessage dm = new DirectMessage();
		dm.setAnalyze(false);
		dm.setBloggerId(uid);
		dm.setText(text);
		dm.setCreatedAt(date);
		dm.setFetchTime(new Date());
		dm.setMediaType(SINA_MEDIA_TYPE);
		dm.setSid(mid);
		dm.setOauthBlogger(bloggerId);
		dm.setGroupId(groupId);
		dm.setDeal(end);
		dm.setOrigin(false);
		dm.setAnalyze(false);
		dm.setFavorited(false);
		dm.setTruncated(false);		
		dm.setExtra(false);		
		dm.setEvent(event);
		if(end){
			dm.setDealTime(new Date());
		}
		messageService.saveDirectMessageAndExtra(dm,list);
		return dm;
	}
	
	@Override
	protected void reloginHttpClient(Long bloggerId) {
		PrivateHttpClientContainer.addReLogin(bloggerId);
	}

}
