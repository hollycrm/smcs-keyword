package com.hollycrm.smcs.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class KeywordPageTest {
	
	@Test
	public void text(){
		String em = "<em>"+
				"毛毛：我用亚莉的"+
				"<span style=\"color: red;\">手机</span>"+
				"发的。"+
				"<a href=\"http://weibo.com/n/%E6%9D%A5%E5%90%AC%E5%90%AC%E6%88%91%E5%94%B1%E7%9A%84\""+ 
					"usercard=\"name=来听听我唱的\">@来听听我唱的</a>"+
				"《他不爱我》,击败了全国39%的人. 试听地址>>> (通过"+
				"<a class=\"a_topic\" href=\"http://huati.weibo.com/k/%E5%94%B1%E5%90%A7?from=526\""+ 
					"target=\"_blank\">#唱吧#</a>"+
				"录制)"+
				"<img src=\"http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/6a/laugh.gif\" " +
				"title=\"[哈哈]\" alt=\"[哈哈]\" type=\"face\"/>"+
				"<a href=\"http://weibo.com/n/%E5%8A%A8%E6%84%9FTalk\" usercard=\"name=动感Talk\">@动感Talk</a>"+
				"<a class=\"W_btn_h\" href=\"http://t.cn/zQ8P3qx\" target=\"_blank\">"+
				"<i class=\"W_ico20 icon_fl_place\"/>"+
				"<span title=\"留下枢纽\">留下枢纽</span>"+
				"</a>"+
				"</em>";
		
		Document doc = Jsoup.parse(em);
		System.out.println(doc.select("em").first().text());
		
	}

}
