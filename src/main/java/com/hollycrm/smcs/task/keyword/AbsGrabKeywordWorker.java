package com.hollycrm.smcs.task.keyword;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.assist.AvaliableKeyword;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.entity.fetch.Condition;
import com.hollycrm.smcs.entity.log.GrabLog;
import com.hollycrm.smcs.filter.IMessageFilter;
import com.hollycrm.smcs.filter.factory.MessageFilterFactory;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.httpclient.impl.PublicHttpClientContainer;
import com.hollycrm.smcs.log.impl.GrabLogger;
import com.hollycrm.smcs.security.ISecurityCode;
import com.hollycrm.smcs.security.impl.KeywordSecurityCode;
import com.hollycrm.smcs.task.AbsGrabHtml;
import com.hollycrm.smcs.task.AbsGrabMessageWorker;
import com.hollycrm.smcs.task.keyword.util.InputKeywordPCodeUtil;
import com.hollycrm.smcs.task.keyword.util.KeywordConstant;
import com.hollycrm.smcs.util.JsonUtil;

public abstract class AbsGrabKeywordWorker extends AbsGrabMessageWorker {

	private static String LOGGER_MESSAGE = "groupId{%s},conditionId{%s},关键字{%s},排它关键字{%s},抓取结束，数量{%s}";

	public static final String GRAB_PAGE_TIME = "grab.page.time";

	public static final String ALL_AREA = "1000";
	
	
	protected  AvaliableKeyword avaliableKeyword;
	private static final Long pageTime = Long.parseLong(AppConfig.get(GRAB_PAGE_TIME));

	private final ISecurityCode iSecurityCode;
	
	private final IMessageFilter messageFilter;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh");
	
	

	public AbsGrabKeywordWorker(AvaliableKeyword avaliableKeyword, AbsGrabHtml grabHtml) {
	
		this.avaliableKeyword = avaliableKeyword;
		this.grabHtml = grabHtml;
		
		iSecurityCode = new KeywordSecurityCode();
		messageFilter = MessageFilterFactory.getMessageFilter();

	}

	@Override
	public void run() {
		
		try {
			grabHtml.initSinceId();
			
			int pageNo = NORMAL_GRAB_PAGE;
			/** 如果第一次登录默认只取两页 **/
			if (grabHtml.isFirst()) {
				pageNo = FIRST_GRAB_PAGE;
			}
			
			String encodeKey;
			try {
				encodeKey = URLEncoder.encode(avaliableKeyword.getKey(), "utf8");
			} catch (UnsupportedEncodingException e) {
				encodeKey = avaliableKeyword.getKey();
			}
		
			IHttpClient client = grabHtml.obtainHttpClient();
			if(client == null){
				return;
			}
			/*if(!haveNewKeyword(client, encodeKey)){
				return;
			}*/
			String privateList = null;
			Element searchFeed = null;
			Elements links = null;
			Long mid = null;
			String url = buildUrl(encodeKey);
			addSourceTime();
			lableA: for (int i = 1; i <= pageNo; i++) {				
				Elements scripts = script(client, url + i);
				if(scripts.isEmpty()){
					break;
				}
				for (Element elementPage : scripts) {
					privateList = elementPage.html();					
					if (privateList.contains("\"pid\":\"pl_wb_feedlist\"")) {
						searchFeed = doc(privateList).select("div.search_feed").first();
						if(searchFeed == null ){
							break lableA;
						}
						
						links = searchFeed.select("dl.feed_list");
						if(links.isEmpty()){
							break lableA;
						}
						for (Element link : links) {
							mid = Long.parseLong(link.attr("mid"));
							if (grabHtml.isInRange(mid)) {
								if(!matchContent(link)){
									continue;
								}
								save(mid, avaliableKeyword.getKey(), null, avaliableKeyword.getGroupId(), SEARCH,avaliableKeyword.getConditionId()
										, avaliableKeyword.getExclusiveKey(), avaliableKeyword.getProvince(), avaliableKeyword.getCity());
								dealCurrentMid(mid);
								sum++;
							}
							
							if(!grabHtml.isInValidRange(mid)){
								break lableA;
							}
							
						}
					} else if (privateList.contains("\"pid\":\"pl_common_sassfilter\"")) {
						/** 出现验证码 **/
						Element yzmDoc = doc(privateList).getElementsByAttributeValue("node-type", "yzm_img").first();
						String fileName = client.downloadPic("http://s.weibo.com" + yzmDoc.attr("src"), "pincode", ".jpg", "cond");						
						InputKeywordPCodeUtil.inputKeywordPCode(avaliableKeyword.getKey(), 
								iSecurityCode.getDoor(iSecurityCode.save(avaliableKeyword.getKey(), avaliableKeyword.getGroupId(), fileName)), client);
						i--;
					}	
				}
				
				Thread.sleep(pageTime);
			}

			grabHtml.endGrab(firstMid);
			logger.info(String.format(LOGGER_MESSAGE, avaliableKeyword.getGroupId(), avaliableKeyword.getConditionId(), 
					avaliableKeyword.getKey(), avaliableKeyword.getExclusiveKey(), sum));
		} catch (Exception e) {
			logger.info(e.toString(), e);
			grabHtml.errorDeal(currentId, firstMid, null);
		} finally {
			
			GrabLogger.logger(new GrabLog(avaliableKeyword.getGroupId(), getLogType(), new Date(), sum, avaliableKeyword.getConditionId(), true));
			grabHtml.countRuntime(sum);
			grabHtml.exit();
		}

	}
	
	
	
	private String buildUrl(String encodeKey){
		String url = AppConfig.get(KeywordConstant.SINA_SOSO_URL) + encodeKey ;
		if(StringUtils.isNotBlank(avaliableKeyword.getProvince())){
			url +="&region=custom:"+avaliableKeyword.getProvince();
			url += ":";
			if(StringUtils.isNotBlank(avaliableKeyword.getCity())){
				url += avaliableKeyword.getCity();
			}else {
				url += ALL_AREA;
			}
		}
		Date now = new Date();
		Date halfOfHourBefore = DateUtils.addMinutes(now, -30);
		if (!grabHtml.isFirst()) {
			url += "&timescope=custom:" + sdf.format(halfOfHourBefore) + ":" + sdf.format(now);
		}
		return url + KeywordConstant.SINA_SOSO_URL_PARAMS;
		
	}
	
	
	
	/**
	 * 判断关键字内容能否匹配
	 * @param content
	 * @return
	 */
	private boolean matchContent(Element link){
		try{
			return messageFilter.filter(link.getElementsByAttributeValue("node-type", "feed_list_content")
					.first().select("em").first().text(), avaliableKeyword.getKey().split(Condition.SPLIT_KEYWORD), 
					avaliableKeyword.getExclusiveKey().split(Condition.SPLIT_KEYWORD));
		}catch(Exception e){
			logger.error(e.toString(),e);
			return true;
		}
	}
	

	@Override
	protected void filterEntiry(Long bloggerId, String entity) throws Exception {
		if (entity.indexOf(KeywordConstant.REPLACE_SINA_LOGIN) != -1) {
			logger.info("抓取关键字" + avaliableKeyword.getKey() + ", client session丢失，重新登录！");
			PublicHttpClientContainer.addReLogin(bloggerId);
			throw new Exception("session丢失");
		}
	}
	
	protected long processSourceTime(){
		String time=new Date().getTime()+"";
		return Long.parseLong(time.substring(0,time.length()-3));
	}
	
	protected abstract void addSourceTime();
	
	protected abstract boolean haveNewKeyword(IHttpClient client, String encodeKey);
	
	protected  int getNewKeywordCount(IHttpClient client, String encodeKey, long beginTime) {
		try {
			String url="http://s.weibo.com/ajax/mblog/newtips?search="+encodeKey+"&t="+beginTime+
					"&_t=0&__rnd="+new Date().getTime();
			Map map = JsonUtil.getMap4Json(client.simpleHttpGet(url));
			if("100000".equals(map.get("code"))){
				String data=map.get("data").toString();
				JSONObject dataJson=JSONObject.fromObject(data);
				return Integer.parseInt(dataJson.get("num").toString());
			}else{
			 return 0;
			}
		} catch (Exception e) {
			return 0;
		}	
	}
	
}
