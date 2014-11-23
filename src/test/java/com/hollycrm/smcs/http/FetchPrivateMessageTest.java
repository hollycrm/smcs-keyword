package com.hollycrm.smcs.http;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.http.impl.LoginClient;
import com.hollycrm.smcs.http42.HttpClient;
import com.hollycrm.smcs.util.JsonUtil;


public class FetchPrivateMessageTest {
	
	private ICommonHttpClient client ;
	
	@Before
	public void init() throws Exception{
		ApplicationContextHolder.init();
		AppConfig.init("smcs-keyword.properties");
		//client = new CommonHttpClient();
		client = new LoginClient("sghakww8512@163.com", "mn19851229", 1L, 5075829468L);
	}
	
	@Test
	public void testPrivateMessage() throws InvalidHttpClientException, Exception{
		String entity = client.simpleHttpGet("http://weibo.com/aj/message/getbyid?_wv=5&ajwvr=6&mid=3622183341740591&uid=1642909335&count=10&_t=0&__rnd=1416739682578");
		System.out.println(entity);
		Map map = JsonUtil.getMap4Json(entity);
		Map map2 = JsonUtil.getMap4Json(map.get("data").toString());
		Document doc = Jsoup.parse("<div class='messageDetail'>"+map2.get("html")+"</div");
		
		Elements elements = doc.getElementsByClass("messageDetail").first().children(); 
		for(Element element: elements){
			System.out.println(getMsgText(element));
			
		}
		
	}
	
	public String getMsgText(Element element) {
		Element elementT = element.select("p.page").first();
		if(elementT == null){
			return "";
		}
		return face(elementT.toString());
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
		return Jsoup.parseBodyFragment(bodyHtml).select("p.page").first()
				.text().trim();
		
	}

	
}
