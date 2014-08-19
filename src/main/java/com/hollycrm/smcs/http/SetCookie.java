package com.hollycrm.smcs.http;

import org.apache.http.impl.client.DefaultHttpClient;

public interface SetCookie {

	void setCookies( DefaultHttpClient client);
}
