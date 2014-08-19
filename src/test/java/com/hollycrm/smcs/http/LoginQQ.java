package com.hollycrm.smcs.http;

import org.junit.Test;

import com.hollycrm.smcs.http42.HttpClient;

public class LoginQQ {
	
	@Test
	public void login() throws InvalidHttpClientException, Exception{
	//	String url = "https://ui.ptlogin2.qq.com/cgi-bin/login?daid=164&target=self&style=16&mibao_css=m_webqq&appid=501004106&enable_qlogin=0&no_verifyimg=1&s_url=http%3A%2F%2Fw.qq.com/proxy.html&f_url=loginerroralert&strong_login=1&login_state=10&t=20130723001";
		String url = "http://w.qq.com/login.html";
		ICommonHttpClient client = new HttpClient();
		System.out.println(client.simpleHttpGet(url));
	
	}

}
