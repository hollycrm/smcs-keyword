package com.hollycrm.smcs.http;

/**
 * 无效的http 连接
 * 
 * @author fly
 *
 */
public class InvalidHttpConnectionException extends Exception{

	private static final long serialVersionUID = 7129419742485791299L;
	
	public InvalidHttpConnectionException(String errorMsg){
		this(errorMsg, null);
	}

	public InvalidHttpConnectionException(String errorMsg, Throwable cause){
		super(errorMsg, cause);
	}
}
