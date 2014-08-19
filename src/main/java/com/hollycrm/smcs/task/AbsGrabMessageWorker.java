package com.hollycrm.smcs.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.entity.base.IdGroup;
import com.hollycrm.smcs.entity.base.Trend;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.InvalidHttpClientException;
import com.hollycrm.smcs.http.httpclient.impl.PublicHttpClientContainer;
import com.hollycrm.smcs.service.fetch.TrendService;
import com.hollycrm.smcs.util.JsonUtil;

public abstract class AbsGrabMessageWorker implements Runnable {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**第一次抓取頁數**/
	public static final int FIRST_GRAB_PAGE = Integer.parseInt(AppConfig.get("first.grab.page"));

	/**默認最大的抓取頁數**/
	public static final int NORMAL_GRAB_PAGE = Integer.parseInt(AppConfig.get("max.grab.page"));
	
	/**script**/
	protected static final String SCRIPPT_TAG = "script";
	
	/**html**/
	protected static final String HTML = "html";

	/****/
	protected static final int SUBSTRING_BEGIN = 41;
	
	protected static final String SINA_MEDIA_TYPE = "w";

	protected IGrabHtml grabHtml;

	protected Long currentId;

	protected Long sinceId;

	protected Long firstMid;

	protected boolean isFirst = true;
	
	protected  TrendService trendService;
	
	protected static final int SEARCH = 2;
	
	protected static final int BOARD = 3;
	
	protected static final int PRIVATE = 1;
	
	protected int sum;
	
	
	public AbsGrabMessageWorker(){
		trendService = ApplicationContextHolder.getBean(TrendService.class);
		
	}
	
	protected abstract String getLogType();
	
	
	
	/**
	 * 处理当前读取的mid 如果是第一个mid 把mid->firstMid 把mid ->currentId
	 * 
	 * @param mid
	 */
	protected void dealCurrentMid(Long mid) {
		if (isFirst) {
			firstMid = mid;
			isFirst = false;
		}
		currentId = mid;
	}

	/**
	 * 获取url的html
	 * 
	 * @param client
	 * @param url
	 * @return
	 * @throws Exception
	 */
	protected Elements script(IHttpClient client, String url) throws Exception {
		logger.info("抓取页面url:"+url);
		String entity = null;
		try{
			entity = client.simpleHttpGet(url);
		}catch(InvalidHttpClientException e){
			logger.error(e.toString(), e);
			logger.info(String.format("账号{%s}的httpclient无效，重新登录", client.getUsername()));
			reloginHttpClient(client.getBloggerId());
			throw e;
		}
		
		if(logger.isDebugEnabled()){
			logger.info(entity);
		}
		filterEntiry(client.getBloggerId(), entity);
		Document doc = Jsoup.parse(entity);
		return doc.getElementsByTag(SCRIPPT_TAG);
	}

	/**
	 * 把html 转换成 Document
	 * @param html
	 * @return
	 */
	protected Document doc(String html) {
		String jsonList = html.substring(SUBSTRING_BEGIN, html.length() - 1);
		Map map = JsonUtil.getMap4Json(jsonList);
		return Jsoup.parse((String) map.get(HTML));
	}
	
	/**
	 * 重新登录 
	 * @param bloggerId
	 */
	protected  void reloginHttpClient(Long bloggerId){
		PublicHttpClientContainer.addReLogin(bloggerId);
	}

	/**
	 * 过滤entity
	 * 
	 * @param entity
	 */
	protected abstract void filterEntiry(Long bloggerId,String entity) throws Exception;
	
	protected void save(Long mid,String key,Long bloggerId,Long groupId,int clazz,Long conditionId, String exclusiveKey){
		save(mid, key, bloggerId, groupId, clazz, conditionId,  exclusiveKey, null, null);
	}
	
	/**
	 * 保存trend
	 * @param mid
	 * @param key
	 * @param bloggerId
	 * @param groupId
	 * @param clazz
	 * @param conditionId
	 * @param exclusiveKey
	 * @param province
	 * @param city
	 */
	protected void save(Long mid,String key,Long bloggerId,Long groupId, 
			int clazz,Long conditionId, String exclusiveKey, String province, String city){
		Trend trend = new Trend();
		trend.setMid(mid);
		trend.setDeal(0);
		trend.setMediaType("w");
		trend.setClazz(clazz);
		trend.setCreatedAt(new Date());
		trend.setKey(key);
		trend.setRead(true);
		trend.setBloggerId(bloggerId);
		IdGroup idGroup = new IdGroup();
		idGroup.setId(groupId);
		trend.setIdGroup(idGroup);
		trend.setConditionId(conditionId);
		trend.setExclusiveKey(exclusiveKey);
		trend.setProvince(province);
		trend.setCity(city);
		trendService.save(trend);
	}
	
	
	
	
	
	/**
	 * 转换时间
	 * @param date
	 * @return
	 */
	public Date date(String date)  {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (date.indexOf("今天") != -1) {			
			Calendar ca = Calendar.getInstance();
			int month = ca.get(Calendar.MONTH) + 1;// 获取月份
			int day = ca.get(Calendar.DATE);// 获取日
			int year = ca.get(Calendar.YEAR);
			String now = date.substring(date.indexOf(" ") + 1);
			date = year + "-" + month + "-" + day + " " + now + ":00";
		} else if (date.indexOf("分钟") != -1) {
			String minutes = date.substring(0, date.indexOf("分钟前"));
			Date now = new Date();
			int i = Integer.parseInt(minutes);
			date = df.format(DateUtils.addMinutes(now, -i));
		} else if (date.indexOf("秒") != -1) {
			String second = date.substring(0, date.indexOf("秒前"));
			Date now = new Date();
			int i = Integer.parseInt(second);
			date = df.format(DateUtils.addSeconds(now, -i));
		} else if (date.indexOf("月") != -1) {
			Calendar ca = Calendar.getInstance();
			String month = date.substring(0, date.indexOf("月"));
			String day = date.substring(date.indexOf("月") + 1, date.indexOf("日"));
			String now = date.substring(date.indexOf(" ") + 1);
			int year = ca.get(Calendar.YEAR);
			date = year + "-" + month + "-" + day + " " + now + ":00";
		} else {
			date = date + ":00";
		}		
		Date dateTime = null;
		try {
			dateTime = df.parse(date);
		} catch (ParseException e) {
			dateTime = new Date();
		}
		return dateTime;
	}
	
	
	
}
