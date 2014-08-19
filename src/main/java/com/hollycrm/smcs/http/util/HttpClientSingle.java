package com.hollycrm.smcs.http.util;

import com.hollycrm.smcs.http.ICommonHttpClient;
import com.hollycrm.smcs.http42.HttpClient;

/**
 * 
 * @author dingqj 
 *
 * 2013-10-12 上午9:13:37
 */
public class HttpClientSingle {
	
	public static ICommonHttpClient client ;
	
	private HttpClientSingle(){
		
	}
	
	public static ICommonHttpClient getHttpClient(){
		if(client == null){
			client = new HttpClient();
		}
		return client;
	}

}
