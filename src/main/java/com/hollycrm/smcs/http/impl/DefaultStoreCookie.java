package com.hollycrm.smcs.http.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.entity.cookie.BaseCookie;
import com.hollycrm.smcs.http.StoreCookie;
import com.hollycrm.smcs.service.cookie.BaseCookieService;

public class DefaultStoreCookie implements StoreCookie{
	
	private List<Cookie> list;

	@Override
	public void store(DefaultHttpClient client) {
		list = client.getCookieStore().getCookies();
	}

	
	
	
	private List<BaseCookie> transform(String username, String mediaType){
		List<BaseCookie> cookies = new ArrayList<BaseCookie>(list.size());
		BaseCookie baseCookie = null;
		Date expireDate = getMinExpireDate();
		for(Cookie cookie:list){
			baseCookie = new BaseCookie();
			baseCookie.setCookieComment(cookie.getComment());
			baseCookie.setCookieDomain(cookie.getDomain());
			baseCookie.setCookieExpiryDate(cookie.getExpiryDate());
			baseCookie.setCookiePath(cookie.getPath());
			baseCookie.setCookieVersion(cookie.getVersion());
			baseCookie.setMediaType(mediaType);
			baseCookie.setName(cookie.getName());
			baseCookie.setSecure(cookie.isSecure());
			baseCookie.setValue(cookie.getValue());
			baseCookie.setExpireDate(expireDate);
			baseCookie.setUsername(username);
			cookies.add(baseCookie);
		}
		return cookies;
	}
	
	private Date getMinExpireDate(){
		Date expireDate = null;
		for(Cookie cookie:list){
			if((expireDate == null) && (cookie.getExpiryDate()!= null)){
				expireDate = cookie.getExpiryDate();
				continue;
			}
			
			if((cookie.getExpiryDate()!= null) && expireDate.after(cookie.getExpiryDate())){
				expireDate = cookie.getExpiryDate();
			}
		}
		return expireDate;
	}

	@Override
	public void storeCookie(String username, String mediaType) {
		List<BaseCookie> cookies = transform(username, mediaType);
		BaseCookieService service = ApplicationContextHolder.getBean(BaseCookieService.class);
		service.deleteBeforeCookie(username, mediaType);
		service.save(cookies);
		
	}
	

}
