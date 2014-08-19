package com.hollycrm.smcs.http;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.entity.base.ExtraFile;
import com.hollycrm.smcs.http.impl.LoginClient;
import com.hollycrm.smcs.util.JsonUtil;

public class GrabNoteMessage {
	private final Logger logger = LoggerFactory.getLogger(GrabNoteMessage.class);
	
	private IHttpClient client ;
	
	@Before
	public void before() throws Exception{
		ApplicationContextHolder.init();
			AppConfig.init("smcs-keyword.properties");
			client = new LoginClient("weibo@hollycrm.com", "a12345678Az", null, null);
	}
	
	@Test
	public void grab() throws Exception{
		Elements notePage = null;
		String eachElementHtml = null;
		Elements msgBoxs = null;
		Long mid = null;
		Long uid = null;
		String msgBoxText = null;
		Date msgBoxDate = null;
		lablePage:for(int i =0;i < 5; i++){
			notePage = script(client,String.format("http://weibo.com/notesboard?page=?", i));
			if(notePage.isEmpty()){
				break;
			}
			for(Element element : notePage){
				eachElementHtml = element.html();
				if(eachElementHtml.contains("\"pid\":\"pl_content_notebox\"")) {
					msgBoxs = doc(eachElementHtml).getElementsByAttributeValue("node-type", "messageUnit");
					if(msgBoxs.isEmpty()){
						break lablePage;
					}
					for(Element msgBox:msgBoxs){
						mid = Long.parseLong(msgBox.attr("mid"));
						uid = Long.parseLong(msgBox.attr("uid"));
						msgBoxText = msgBox.select("p.detail").first().toString();
						msgBoxDate = date(msgBox.select("em.WB_time").text().trim());
						
						List<ExtraFile> attachFile = readAttachFile(msgBox.select("div.msg_attachment"), client, mid);
						logger.info(String.format("mid=%s,uid=%s,msgBoxText=%s,msgBoxDate=%s", mid,uid,face(msgBoxText),msgBoxDate));
						//saveDirectMessage(uid, mid, msgBoxDate, msgBoxText, false, attachFile);
					}
				}
				
			}
		}
	}
	
	public List<ExtraFile> readAttachFile(Elements elements, IHttpClient client, Long mid){
		if(elements.isEmpty()){
			return Collections.EMPTY_LIST;
		}
		List<ExtraFile> list = new ArrayList<ExtraFile>(elements.size());
		ExtraFile extraFile = null;
		String fileName = null;
		for(Element element:elements){
			extraFile = new ExtraFile();
			
			fileName = parseFileName(element.select("em.file_name").first().text());
			element.select("a").first().attr("href");
			extraFile.setFileOldName(fileName);
			extraFile.setFileSize(getFileSize(element.select("em.file_size").first().text()));
			extraFile.setUploadUrl(element.select("a").first().attr("href"));
			extraFile.setSid(mid);
			list.add(extraFile);
		}
		return list;
		
	}
	private  String getFileSize(String fileSize){
		return fileSize.substring(1,fileSize.length()-1);
	}
	private  String getSuffix(String fileName){
		return fileName.substring(fileName.lastIndexOf("."));
	}
	
	private String parseFileName(String fileName)
	{
		fileName = fileName.replaceAll(":|：", ":");
		return fileName.substring(fileName.indexOf(":")+1);
	}	
	public  String face(String talkText) {
		while (true) {
			Document body = Jsoup.parseBodyFragment(talkText);
			Element txt = body.select("p.detail").first();
			String title = txt.select("img").attr("title");
			Element imgPart = txt.select("img").first();
			if (imgPart == null) {
				break;
			}
			talkText = talkText.replace(imgPart.toString(), title);
		}
		Document body1 = Jsoup.parseBodyFragment(talkText);
		Element txt1 = body1.select("p.detail").first();
		String face = txt1.text().trim();
		face = face.replaceAll(":|：", ":");
		face = face.substring(face.indexOf(":") + 1);
		return face;
	}
	
	protected Elements script(IHttpClient client, String url) throws Exception {
		logger.info("抓取页面url:"+url);
		String entity = client.simpleHttpGet(url);		
		if(logger.isDebugEnabled()){
			logger.info(entity);
		}
		//filterEntiry(client.getBloggerId(), entity);
		Document doc = Jsoup.parse(entity);
		
		return doc.getElementsByTag("script");
	}

	protected Document doc(String html) {
		String jsonList = html.substring(41, html.length() - 1);
		Map map = JsonUtil.getMap4Json(jsonList);
		return Jsoup.parse((String) map.get("html"));
	}
	
	protected Date date(String date)  {
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
