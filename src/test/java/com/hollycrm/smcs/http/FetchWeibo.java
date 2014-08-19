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
import com.hollycrm.smcs.util.JsonUtil;

public class FetchWeibo {
private IHttpClient client;
	
	@Before
	public void init() throws Exception{
		ApplicationContextHolder.init();
		AppConfig.init("smcs-keyword.properties");
		client = new LoginClient("sghakww@126.com", "flymotor123", null, null);
	}
	
	@Test
	public void fetch() throws Exception{
		String url ="http://weibo.com/leijun";
		String entity = client.simpleHttpGet(url);
		
		Document doc = Jsoup.parse(entity);
		boolean isFirst = true;
		String maxId = null;
		String currentId = null;
		
		Elements scripts = doc.getElementsByTag("script");
		for(Element element : scripts){
		String	privateList = element.html();
			if (privateList.contains("\"pid\":\"pl_content_hisFeed\"")) {
				Element	searchFeed = doc(privateList).select("div.WB_feed").first();
				Elements	links = searchFeed.select("div.WB_feed_type");
				int i=0;
				for(Element element1:links){
					String mid = element1.attr("mid");
					System.out.println(mid);
					if(isFirst){
						maxId = mid;
						isFirst = false;
					}
					currentId = mid;
					System.out.println(++i);
				}
			}
		}
		String url1 ="http://weibo.com/aj/mblog/mbloglist?_wv=5&page=1&count=15&max_id=" +currentId+
				"&pre_page=1&end_id=" +maxId+
				"&pagebar=0&_k=" +System.currentTimeMillis()+
				"&uid=" +1749127163+
				"&_t=0&__rnd="+System.currentTimeMillis();
		String entity1 = client.simpleHttpGet(url1);
		Map map = JsonUtil.getMap4Json(entity1);
		String data = map.get("data").toString();
		Elements	links =Jsoup.parse(data).select("div.WB_feed_type");
		int i=0;
		for(Element element:links){
			String mid = element.attr("mid");
			System.out.println(mid);
			System.out.println(++i);
		}
		
	}
	
	
	protected Elements script(IHttpClient client, String url) throws Exception {
		String entity = client.simpleHttpGet(url);
		
		Document doc = Jsoup.parse(entity);
		return doc.getElementsByTag("script");
	}

	protected Document doc(String html) {
		String jsonList = html.substring(41, html.length() - 1);
		Map map = JsonUtil.getMap4Json(jsonList);
		return Jsoup.parse((String) map.get("html"));
	}

}
