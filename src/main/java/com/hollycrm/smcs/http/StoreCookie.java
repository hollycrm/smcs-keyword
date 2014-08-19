package com.hollycrm.smcs.http;

import org.apache.http.impl.client.DefaultHttpClient;

public interface StoreCookie {
	
	void store(DefaultHttpClient client);
	
	void storeCookie(String username, String mediaType);

}
