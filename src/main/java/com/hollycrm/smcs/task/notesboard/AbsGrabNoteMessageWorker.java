package com.hollycrm.smcs.task.notesboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.entity.base.ExtraFile;
import com.hollycrm.smcs.entity.log.GrabLog;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.note.INoteMessage;
import com.hollycrm.smcs.http.note.impl.NoteMessage;
import com.hollycrm.smcs.log.impl.GrabLogger;
import com.hollycrm.smcs.task.AbsGrabPrivateAndNoteMessage;
import com.hollycrm.smcs.task.IGrabHtml;

/**
 * 抓留言信息(没有关注的用户发的私信)
 * @author fly
 *
 */
public abstract class AbsGrabNoteMessageWorker extends AbsGrabPrivateAndNoteMessage{
	
	protected static final String NOTE_URL = "http://weibo.com/notesboard?page=";
	
	protected static final Long PAGE_SLEEP_TIME = 2000L;
	
	private final Long bloggerId;
	
	private final Long groupId;
	
	private final INoteMessage noteMessage;
	
	
	
	
	public AbsGrabNoteMessageWorker(IGrabHtml grabHtml, Long bloggerId, Long groupId){
    this.grabHtml = grabHtml;
		this.bloggerId = bloggerId;
		this.groupId = groupId;
		noteMessage = new NoteMessage();
	}

	@Override
	public void run() {
		try{
			grabHtml.initSinceId();
			int pageNo = NORMAL_GRAB_PAGE;
			/** 如果第一次登录默认只取两页 **/
			if (grabHtml.isFirst()) {
				pageNo = FIRST_GRAB_PAGE;
			}
			IHttpClient client = grabHtml.obtainHttpClient(); 			
			if(client == null){
				logger.info("groupId{"+groupId+"},bloggerId{"+bloggerId+"抓取留言时获取httpclient失败");
				return;
			}
			Elements notePage = null;
			String eachElementHtml = null;
			Elements msgBoxs = null;
			Long mid = null;
			Long uid = null;
			String msgBoxText = null;
			Date msgBoxDate = null;
			lablePage:for(int i =1;i <= pageNo; i++){
				notePage = script(client,NOTE_URL + i);
				if(notePage.isEmpty()){
					break;
				}
				for(Element element : notePage){
					eachElementHtml = element.html();
					if(eachElementHtml.contains(noteMessage.contentNoteMessageScript())) {
						msgBoxs = noteMessage.getNoteMessageElements(doc(eachElementHtml));
						if(msgBoxs.isEmpty()){
							break lablePage;
						}
						for(Element msgBox:msgBoxs){
							if(msgBox.hasClass("msg_time_line")) {
								msgBoxDate = date(msgBox.select("legend.time_tit").first().text());
							}else if(msgBox.hasClass("msg_dialogue_list")){
								mid = Long.parseLong(msgBox.attr("mid"));
								if(!grabHtml.isInRange(mid)){
									break lablePage;
								}
								uid = Long.parseLong(msgBox.attr("uid"));
								msgBoxText = face(msgBox.select("p.msg_dia_txt").first().toString());									
								List<ExtraFile> attachFile = readAttachFile(msgBox.select("div.msg_attachment"), client, mid);
								logger.info(String.format("抓取留言：mid=%s,uid=%s,msgBoxText=%s,msgBoxDate=%s", mid,uid,msgBoxText,msgBoxDate));
								saveDirectMessage(uid, mid, msgBoxDate, msgBoxText, false, attachFile,bloggerId, groupId, null);
								dealCurrentMid(mid);
								sum++;
							}
						}
						break;
					}
					
				}
				Thread.sleep(PAGE_SLEEP_TIME);
			}
			logger.info(String.format("本次抓取留言信息{%s}条", sum));
			grabHtml.endGrab(firstMid);
		}catch(Exception e){
			logger.error(e.toString(),e);
		}finally{
			GrabLogger.logger(new GrabLog(groupId, getLogType(), new Date(), sum, true));
			grabHtml.countRuntime(sum);
			grabHtml.exit();
		}
		
	}
	

	
	private List<ExtraFile> readAttachFile(Elements elements, IHttpClient client, Long mid){
		if(elements.isEmpty()){
			return Collections.EMPTY_LIST;
		}
		List<ExtraFile> list = new ArrayList<ExtraFile>(elements.size());
		ExtraFile extraFile = null;
		for(Element element:elements){
			try{
				extraFile = new ExtraFile();				
				extraFile.setFileOldName(formatFileName(parseFileName(element)));
				extraFile.setUploadUrl(parseUploadUrl(element));
				if(StringUtils.isNotBlank(extraFile.getUploadUrl())){
					extraFile.setFileNewName(client.downloadPic(extraFile.getUploadUrl(), "upload", 
							getSuffix(extraFile.getFileOldName()), "private"));
				}
				
				extraFile.setFileSize(getFileSize(parseFileSize(element)));	
				Element url=element.select("img").first();
				if(url!=null){
					extraFile.setFileUrl(url.attr("src"));
				}
				extraFile.setSid(mid);
				list.add(extraFile);
			}catch(Exception e){
				logger.error(e.toString(),e);
			}
		}
		return list;
		
	}
	
	private String parseUploadUrl(Element element){
		Elements elements = element.select("a");
		for(Element aElement :elements){
			if(aElement.html().indexOf("下载") != -1){
				return aElement.attr("href");
			}
		}
		return null;
	}
	
	/**
	 * 获取文件大小
	 * @param element
	 * @return
	 */
	private String parseFileSize(Element element)
	{
		Elements elements = element.select("em.file_size");
		if(elements.isEmpty()){
			return null;
		}
		return elements.first().text();
	}	
	/****
	 * 获取文件名称
	 * @param element
	 * @return
	 */
	private String parseFileName(Element element){
		Elements elements = element.select("em.file_name");
		if(elements.isEmpty()){
			return null;
		}
		return elements.first().text();
	}
	
	private String formatFileName(String fileName){
		if(StringUtils.isBlank(fileName)){
			return null;
		}
		fileName = fileName.replaceAll(":|：", ":");
		return fileName.substring(fileName.indexOf(":")+1);
	}	
	
	public  String face(String talkText) {
		while (true) {
			Document body = Jsoup.parseBodyFragment(talkText);
			Element imgPart = body.select("img").first();
			if (imgPart == null) {
				break;
			}
			String title = imgPart.attr("title");
			talkText = talkText.replace(imgPart.toString(), title);
		}
		Document body1 = Jsoup.parseBodyFragment(talkText);		
		return body1.select("p.msg_dia_txt").first().text();
	}
	


	@Override
	protected void filterEntiry(Long bloggerId, String entity) throws Exception {
		
	}

}
