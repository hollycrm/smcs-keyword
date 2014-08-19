package com.hollycrm.smcs.http.httpclient.impl;

import com.hollycrm.smcs.http.IHttpClient;

public abstract class PublicHttpClientContainer {
	private static PublicHttpClientImpl impl = new PublicHttpClientImpl();
	
	public synchronized static IHttpClient obtainHttpClient(){
		return impl.obtainHttpClient();
	}
	
	public synchronized static void removeHttpClient(Long bloggerId){
		impl.addReLogin(bloggerId);
	}
	
	public synchronized static IHttpClient removeAndObtainHttpClient(Long bloggerId){
		return impl.removeAndObtainHttpCient(bloggerId);
	}
	
	public  static IHttpClient getCommonHttpclient(){
		return impl.getCommonHttpClient();
	}
	
	public static void addReLogin(Long bloggerId){
		impl.addReLogin(bloggerId);
	}
	
	public static void init(){
		
	}
}
