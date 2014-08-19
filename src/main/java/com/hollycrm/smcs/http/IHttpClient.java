package com.hollycrm.smcs.http;

/**
 * 
 * 
 * @author fly
 *
 */

public interface IHttpClient extends ICommonHttpClient{

	/**
	 * 返回登录账号
	 * @return
	 */
	String getUsername();
	
	/**
	 * 返回博主
	 * @return
	 */
	Long getBloggerId();
	
	/**
	 * 登录账号的类型
	 * @return
	 */
	int getType();
	
	
	

}
