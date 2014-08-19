package com.hollycrm.smcs.security;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SecurityCode {
	private static final Logger logger = LoggerFactory.getLogger(SecurityCode.class);

	/** 登录验证码类型 **/
	public static final int LOGIN_SECURITY_CODE = 1;

	/** 抓取关键字验证码类型 **/
	public static final int KEYWORD_SECURITY_CODE = 2;

	/** 授权验证码类型 **/
	public static final int AUTH_SECURITY_CODE = 3;

	private static ConcurrentHashMap<String, String> loginCodeMap = new ConcurrentHashMap<String, String>();
	private static ConcurrentHashMap<String, String> authCodeMap = new ConcurrentHashMap<String, String>();
	private static ConcurrentHashMap<String, String> condCodeMap = new ConcurrentHashMap<String, String>();


	public static void put(String key, String value, int type) {
		logger.info("输入"+getCodeName(type)+"验证码key:"+key+",value="+value);
		switch (type) {
		case 1:
			loginCodeMap.put(key, value);
			break;
		case 2:
			condCodeMap.put(key, value);
			break;
		case 3:
			authCodeMap.put(key, value);
			break;
		
		default:
			;
			break;
		}
	}

	public static String get(String key, int type) {
		
		String door = "";
		switch (type) {
		case 1:
			door = loginCodeMap.get(key);
			break;
		case 2:
			door = condCodeMap.get(key);
			break;
		case 3:
			door = authCodeMap.get(key);
			break;
		
		default:
			;
			break;
		}
		logger.info("获取"+getCodeName(type)+"验证码key:"+key+",value="+door);
		return door;
	}

	public static void remove(String key, int type) {
		logger.info("删除"+getCodeName(type)+"验证码key:"+key);
		switch (type) {
		case 1:
			loginCodeMap.remove(key);
			break;
		case 2:
			condCodeMap.remove(key);
			break;
		case 3:
			authCodeMap.remove(key);
			break;
		
		default:
			;
			break;
		}
	}
	
	public static String getCodeName(int type){
		switch (type) {
		case 1:
			return "登录";
			
		case 2:
			return "关键字";
			
		case 3:
			return "授权";
			
		
		default:
			return "";
		}
	}

}
