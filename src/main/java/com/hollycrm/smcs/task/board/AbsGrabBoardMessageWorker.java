package com.hollycrm.smcs.task.board;

import java.util.Date;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.entity.log.GrabLog;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.log.impl.GrabLogger;
import com.hollycrm.smcs.task.AbsGrabMessageWorker;
import com.hollycrm.smcs.task.IGrabHtml;


public abstract class AbsGrabBoardMessageWorker extends AbsGrabMessageWorker{
	
	public static final String BOARD_URL = "http://e.weibo.com/";
	
	public static final String BOARD_URL_PARAM = "/messboard?status=0&is_all=1&page=";
	
	public static final String BOARD_MESSAGE_CONTAIN = "\"pid\":\"pl_content_hisMess\"";	
	
	protected Long uid;
	protected Long groupId;
	
	public AbsGrabBoardMessageWorker(IGrabHtml grabHtml){
		this.grabHtml  = grabHtml;
		
	}
	
	@Override
	public void run() {
		try{
		
			grabHtml.initSinceId();
			
			IHttpClient client = grabHtml.obtainHttpClient();
			if(client == null){
				return;
			}
			int page = NORMAL_GRAB_PAGE;
			if(grabHtml.isFirst()){
				page = FIRST_GRAB_PAGE;
			}
			String url=BOARD_URL+uid+BOARD_URL_PARAM;
			Elements elements = null;
			String html = null;
			Element divFeedList =null;
			Elements dlFeedList =null;
			Long mid = null;			
			Label:for(int i=1;i<=page;i++){			
				elements=script(client,url+i);
				for(Element element:elements){
					html=element.html();
					if(html.contains(BOARD_MESSAGE_CONTAIN)){					
						divFeedList=doc(html).getElementsByAttributeValue("node-type", "feed_list").first();
						dlFeedList=divFeedList.select("dl.feed_list");
						if(dlFeedList.isEmpty()){
							break Label;
						}
						for(Element dlElement:dlFeedList){
							mid=Long.parseLong(dlElement.attr("mid"));
							if(grabHtml.isInRange(mid)){							
								save(mid, null, uid, groupId, BOARD,null,null);
								dealCurrentMid(mid);
								sum++;
							}
							if(!grabHtml.isInValidRange(mid)){
								break Label;
							}
							
						}
						
					}
				}
			}
			grabHtml.endGrab(firstMid);
			grabHtml.countRuntime(sum);
		}catch(Exception e){			
			grabHtml.errorDeal(currentId, firstMid, null);
		}finally{
			GrabLogger.logger(new GrabLog(groupId, getLogType(), new Date(), sum, true));
			grabHtml.exit();
		}
		
	
	}
	
	
	@Override
	protected void filterEntiry(Long bloggerId, String entity) throws Exception{
		
	}
	
}
