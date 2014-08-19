package com.hollycrm.smcs.task.pm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.entity.base.ExtraFile;
import com.hollycrm.smcs.entity.base.IdUser;
import com.hollycrm.smcs.entity.event.Event;
import com.hollycrm.smcs.entity.event.EventLog;
import com.hollycrm.smcs.entity.log.GrabLog;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.httpclient.impl.PrivateHttpClientContainer;
import com.hollycrm.smcs.http.pm.IPrivateMessage;
import com.hollycrm.smcs.http.pm.IPrivateMessageDetail;
import com.hollycrm.smcs.http.pm.IPrivateMessageList;
import com.hollycrm.smcs.http.pm.impl.factory.PrivateMessageDetailFactory;
import com.hollycrm.smcs.http.pm.impl.factory.PrivateMessageListFactory;
import com.hollycrm.smcs.log.impl.GrabLogger;
import com.hollycrm.smcs.service.base.IdOauthService;
import com.hollycrm.smcs.service.base.IdUserService;
import com.hollycrm.smcs.service.event.EventLogService;
import com.hollycrm.smcs.service.event.EventService;
import com.hollycrm.smcs.task.AbsGrabPrivateAndNoteMessage;
import com.hollycrm.smcs.task.IGrabHtml;


/**
 * 私信
 * @author fly
 *
 */
public abstract class AbsGrabPrivateMessageWorker extends AbsGrabPrivateAndNoteMessage{
	
	protected IPrivateMessageList iPrivateMessageList;
	
	protected IPrivateMessageDetail iPrivateMessageDetail;
	
	protected EventLogService eventLogService;
	
	protected EventService eventService;
	
	protected IdOauthService idOauthService; 
	
	protected boolean deal;
	
	protected IdUser idUser;
	
	protected Long uid;
	
	private final Long bloggerId;
	
	private final Long groupId;
	
	private Event event;
	
	private boolean isUidFirst;
	
	protected Long uidMaxId;
	
	//private final List<EventLog> eventLogs = new ArrayList<EventLog>(2);
	
	/**区分是企业微博还是个人版微博**/
	protected int type;
	
	public AbsGrabPrivateMessageWorker(IGrabHtml grabHtml, Long bloggerId, Long groupId, int type){		
		this.bloggerId = bloggerId;
		this.groupId = groupId;
		this.grabHtml = grabHtml;		
		this.type = type;
		iPrivateMessageList = PrivateMessageListFactory.getPrivateMessageList(bloggerId, groupId, type);
		iPrivateMessageDetail = PrivateMessageDetailFactory.getprivateMessageDetail(bloggerId, groupId, type);
		eventLogService = ApplicationContextHolder.getBean(EventLogService.class);		
		eventService = ApplicationContextHolder.getBean(EventService.class);		
		idOauthService = ApplicationContextHolder.getBean(IdOauthService.class);		
	}
	
	@Override
	public void run() {		
		
		try{
			grabHtml.initSinceId();			
			if(isExit(grabHtml.isFirst())){
				return;
			}
			
			IHttpClient client = grabHtml.obtainHttpClient();			
			if(client == null){
				logger.info("groupId{"+groupId+"},bloggerId{"+bloggerId+"抓取私信时获取httpclient失败");
				return;
			}
			
			execute(client, getIPrivateMessage(), getIPrivateMessage().getAccessPageUrl());
		}catch(Exception e){
			logger.error(e.getLocalizedMessage());
		}finally{
			GrabLogger.logger(new GrabLog(groupId, getLogType(), new Date(), sum, true));
			grabHtml.exit();
		}
		
	}
	
	

	/**
	 * 按所处的页面处理页面的内容
	 * @param client
	 * @param iPrivateMessage 所处的页面类
	 * @param url
	 * @throws Exception
	 */
	
	public void execute(IHttpClient client, IPrivateMessage iPrivateMessage, String url) throws Exception{		
		int page = 1;
		String pageUrl = url+"&page=";
		try{
			label:	for(int i = 1;i <= page;i++) {				
				Elements elements = script(client, pageUrl+i);
				for(Element element :elements){
					String html = element.html();
					if (iPrivateMessage.containsHtml(html)) {	
						Document messageListDoc = doc(html);
							if(i == 1){
								page = iPrivateMessage.parsePage(grabHtml.isFirst(), messageListDoc);
							}
							Elements links = iPrivateMessage.parsehtml(messageListDoc);
							if(links.isEmpty()){
								break label;
							}
							for(Element bloggerDiv:links){							
								Long mid = iPrivateMessage.getMid(bloggerDiv);									
								if(!iPrivateMessage.isInRange(grabHtml, mid, uidMaxId)){
									break label;
								}
								doNextStep(bloggerDiv,iPrivateMessage, client, mid);
							}
						break;
					}
				}
				Thread.sleep(iPrivateMessage.getPageSleepTime());
			}
		
		if(deal && (currentId != null)){
			iPrivateMessage.endBeforeMessage(currentId,uid,groupId);
		}
		
		iPrivateMessage.endGrab(grabHtml);		
		}catch(Exception e){
			logger.error(e.getLocalizedMessage(),e);
			iPrivateMessage.errorDeal(currentId, firstMid,grabHtml);
		}
	}
	
	/**
	 * 抓取页面上的内容
	 * @param element
	 * @param iPrivateMessage
	 * @param client
	 * @param mid
	 * @throws Exception
	 */
	public void doNextStep(Element element, IPrivateMessage iPrivateMessage, IHttpClient client, Long mid) throws Exception {		
		iPrivateMessage.dealMsgElement(element, this, client, mid);
	}
	
	/**
	 * 读取私信 内容
	 * @param element
	 * @param iPrivateMessage
	 * @param client
	 * @param mid
	 */
	public void readMessageDetail(Element element, IPrivateMessageDetail iPrivateMessage, IHttpClient client, Long mid){
		String talkStr = iPrivateMessage.getMsgText(element);
		Date createdTime1 = iPrivateMessage.getMsgDate(element, this);
		event(uid);
		if (iPrivateMessage.isReceiveMsg(element)) {   //收到的私信
			if (messageService.existsSameMessage(mid, SINA_MEDIA_TYPE, groupId)) {
					return;
			}
			
			List<ExtraFile> extraList = readExtraFile( element, iPrivateMessage, client, mid);
			saveDirectMessage(uid,mid,createdTime1,talkStr,deal,extraList, bloggerId, groupId, null);
		} else {				
			if (!eventLogService.existLog(mid,SINA_MEDIA_TYPE)) {
				if(!deal){
					deal=true;
				}
				initIdUser();					
				saveEventLong(mid,event,createdTime1,talkStr,"2",idUser);										
			}
		}
		sum++;
		dealCurrentMid(mid);
	}
	
	
	/*private void updateEventLogs(Message message){
		if(eventLogs.isEmpty()){
			return;
		}
		
		eventLogService.updateEventLogMessage(message, eventLogs);
		eventLogs.clear();
	}*/
	
	/**
	 * 读取私信list
	 * 
	 * **/
	public void readMessageList(Element element, IHttpClient client) throws Exception{
		uid = Long.parseLong(element.attr("uid"));
		isUidFirst = true;
		uidMaxId = -1L;
		event = null;
		deal = false;
		iPrivateMessageDetail.setCurrentMaxId(null);
		iPrivateMessageDetail.setUid(uid);
		//eventLogs.clear();
		execute(client, iPrivateMessageDetail, iPrivateMessageDetail.getAccessPageUrl());
		iPrivateMessageDetail.setUid(null);
	}
	

	@Override
	protected void dealCurrentMid(Long mid) {
		if(isFirst){
			iPrivateMessageList.setCurrentMaxId(mid);
			isFirst = false;
			firstMid = mid;
		}
		currentId = mid;
		if(isUidFirst){
			iPrivateMessageDetail.setCurrentMaxId(mid);
			uidMaxId = mid;
			isUidFirst = false;
		}
		
	}

	@Override
	protected void filterEntiry(Long bloggerId, String entity) throws Exception {
		if((entity.indexOf("location.replace(\"http://weibo.com/sso/login.php")!=-1)||(entity.indexOf("<body class=\"MIB_loginphp\">")!=-1)){
			//移除过期的client
			logger.info(String.format("bloggerId为{%s}的httpClient session is expire", bloggerId));
			logger.info(entity);
			PrivateHttpClientContainer.removeHttpClient(bloggerId);
			throw new Exception("httpclient session is expire");
		}
	}

	
	private List<ExtraFile> readExtraFile(Element element, IPrivateMessageDetail iPrivateMessage, 
			IHttpClient client,Long mid){
		Elements picList = element.select(iPrivateMessage.getAttachStyle());
		List<ExtraFile> extraList = null;		
		if(picList.isEmpty()){
			extraList = Collections.EMPTY_LIST;
		}
		
		extraList = new ArrayList<ExtraFile>(picList.size());				
		for(Element picElement:picList){
			try{
			ExtraFile ef=new ExtraFile();
			ef.setFileOldName(iPrivateMessage.getFileName(picElement));
			ef.setFileSize(getFileSize(iPrivateMessage.getFileSize(picElement)));	
			String uploadUrl = iPrivateMessage.getUploadUrl(picElement);
			if(StringUtils.isNotBlank(uploadUrl)){
				ef.setFileNewName(client.downloadPic(uploadUrl, "upload", 
						getSuffix(ef.getFileOldName()), "private"));
			}
			ef.setUploadUrl(uploadUrl);
			Element url=picElement.select("img").first();
			ef.setSid(mid);
			if(url!=null){
				ef.setFileUrl(url.attr("src"));
			}
			extraList.add(ef);
			}catch(Exception e){
				continue;
			}
		}
		return extraList;
	}
	
	
	private void saveEventLong(Long mid, Event event, Date date, String text, String type,IdUser idUser) {
		EventLog log = new EventLog();
		log.setMediaType(SINA_MEDIA_TYPE);
		log.setUser(idUser);
		log.setSid(mid);
		log.setEvent(event);
		log.setText(text);
		log.setType(type);
		log.setFetch(true);
		log.setDealTime(date);
		eventLogService.save(log);
		//eventLogs.add(log);
	}
	
	public Event event(Long uid) {
		if(event == null){
			event = eventService.findEvent(uid, SINA_MEDIA_TYPE, groupId);
		}
		 
		if(event == null) {
			event = new Event();
			event.setBloggerId(uid);
			event.setCreatedAt(new Date());
			event.setGroupId(groupId);
			event.setMediaType(SINA_MEDIA_TYPE);
			event.setDone(false);
			eventService.save(event);
		}
		return event;
	}
	
	
	
	private void initIdUser(){
		if(idUser == null){
			idUser = ApplicationContextHolder.getBean(IdUserService.class).getAdminUser(groupId);
		}
	}
	
	
	protected abstract boolean isExit(boolean isFirst);
	
	protected abstract IPrivateMessage getIPrivateMessage();
}
