package com.hollycrm.smcs.http.httpclient.impl;

import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.httpclient.IManageHttpClient;

public abstract class SendHttpClientContainer {
	private static IManageHttpClient impl = new SendHttpClientImpl();
 
	public synchronized static IHttpClient obtainHttpClient(Long bloggerId){
		return impl.obtainHttpClient(bloggerId);
	}
	
	public synchronized static void removeHttpClient(Long bloggerId){
		impl.addReLogin(bloggerId);
	}
	
	public synchronized static IHttpClient removeAndObtainHttpClient(Long bloggerId) {
		return impl.removeAndObtainHttpCient(bloggerId);
	}
	
	public  static IHttpClient getCommonHttpclient(){
		return impl.getCommonHttpClient();
	}
	
	public static void addReLogin(Long bloggerId){
		impl.addReLogin(bloggerId);
	}
	
	public static void keepHttpClientSession(){
		impl.keepHttpClientSession();
	}
	
	public static void init(){
		
	}
}
