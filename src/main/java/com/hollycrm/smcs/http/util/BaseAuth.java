package com.hollycrm.smcs.http.util;

import org.apache.http.client.methods.HttpUriRequest;

import weibo4j.http.BASE64Encoder;

import com.hollycrm.smcs.config.AppConfig;

public class BaseAuth {
	
	public static void auth(HttpUriRequest request){
		request.setHeader("Authorization", "Basic "+BASE64Encoder.encode(
				(AppConfig.get("sina.app.username")+":"+AppConfig.get("sina.app.password")).getBytes()));
	}

}
