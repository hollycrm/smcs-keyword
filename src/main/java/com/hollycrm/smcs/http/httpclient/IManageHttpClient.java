package com.hollycrm.smcs.http.httpclient;

import com.hollycrm.smcs.http.IHttpClient;



public interface IManageHttpClient {
	
	/**
	 * 获取公用的httpclient
	 * @return httpclient
	 * @throws Exception
	 */
	IHttpClient obtainHttpClient();
	
	/**
	 * 获取指定bloggerId的httpClient
	 * @param bloggerId
	 * @return
	 */
	IHttpClient obtainHttpClient(Long bloggerId);
	
	
	
	/**
	 * 移除bloggerId登录的httpclient
	 * 并且重新获取
	 * @param bloggerId
	 * @return
	 */
	IHttpClient removeAndObtainHttpCient(Long bloggerId);
	

	/**
	 * 把bloggerId加到待重登录 的队列中
	 * @param bloggerId
	 */
	void addReLogin(Long bloggerId);
	
	/**
	 * 获取无需登录的httpclient
	 * @return
	 */
	IHttpClient getCommonHttpClient();
	
	/**
	 * 保持httpclient session
	 * 
	 */
	void keepHttpClientSession();

}
