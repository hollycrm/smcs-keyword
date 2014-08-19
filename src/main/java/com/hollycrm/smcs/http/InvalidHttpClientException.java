package com.hollycrm.smcs.http;

/**
 * 当httpclient无效时 抛出该错误
 * 
 * @author fly
 *
 */
public class InvalidHttpClientException extends Exception{

	private static final long serialVersionUID = 7099808719849775347L;
	
	private int statusCode;
	
	public InvalidHttpClientException(String errorMsg){
		this(errorMsg,-1);
	}
	
	public InvalidHttpClientException(String errorMsg,int statusCode){
		super(errorMsg);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}
