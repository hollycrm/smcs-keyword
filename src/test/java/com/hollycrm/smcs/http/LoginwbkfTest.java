package com.hollycrm.smcs.http;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.hollycrm.smcs.http42.HttpClient;

/**
 * 先登录wbkf.hollycrm.com
 * 再读取下待处理列表
 * 
 * @author fly
 *
 */
public class LoginwbkfTest {
	
	@Test
	public void login() throws InvalidHttpClientException, Exception{
		ICommonHttpClient client = new HttpClient();
		Map<String, String> map = new HashMap<String, String>();
		map.put("password_", "xiaoshengfa");
		map.put("userName", "admin");
		String entity = client.post("http://wbkf.hollycrm.com/oauth/login.do", map, null);
		System.out.println(entity);
		
		System.out.println(client.simpleHttpGet("http://wbkf.hollycrm.com/weibo-message!gmail2.do?doType=pend&clazz=pend"));
		
		
	}

}
