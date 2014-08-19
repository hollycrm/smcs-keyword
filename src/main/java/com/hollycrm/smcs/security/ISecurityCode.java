package com.hollycrm.smcs.security;

import com.hollycrm.smcs.security.Exception.NoInputSecurityCodeException;


public interface ISecurityCode {
	
	String getDoor(String key) throws NoInputSecurityCodeException;
	
	/**
	 * 验证码类型
	 * @return
	 */
	int getType();
	
	/**
	 * 返回唯一的key
	 * @param key
	 * @param groupId
	 * @param fileName
	 * @return
	 */
	String save(String key, Long groupId, String fileName);
	
	

}
