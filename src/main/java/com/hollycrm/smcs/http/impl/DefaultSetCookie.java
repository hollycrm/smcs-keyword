package com.hollycrm.smcs.http.impl;

import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.hollycrm.smcs.entity.cookie.BaseCookie;
import com.hollycrm.smcs.http.SetCookie;

public class DefaultSetCookie implements SetCookie{
	private final List<BaseCookie> list;
	
	
	public DefaultSetCookie(List<BaseCookie> list){
		this.list = list;
	}
	

	@Override
	public void setCookies( DefaultHttpClient client) {
		CookieStore cookieStore = new BasicCookieStore();
		for(BaseCookie baseCookie:list){
			cookieStore.addCookie(buildCookie(baseCookie));
		}		
		client.setCookieStore(cookieStore);
	}
	
	private Cookie buildCookie(BaseCookie baseCookie){
		BasicClientCookie cookie = new BasicClientCookie(baseCookie.getName(), baseCookie.getValue());
		cookie.setComment(baseCookie.getCookieComment());
		cookie.setDomain(baseCookie.getCookieDomain());
		cookie.setExpiryDate(baseCookie.getCookieExpiryDate());
		cookie.setPath(baseCookie.getCookiePath());
		cookie.setSecure(baseCookie.isSecure());
		cookie.setVersion(baseCookie.getCookieVersion());
		
		return cookie;
	}

}
