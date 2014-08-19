package com.hollycrm.smcs.http;

/**
 * 当登录时，密码错误，抛出该错误
 * 
 * @author fly
 *
 */
public class InvalidPasswordException extends Exception{

	private static final long serialVersionUID = 5135234782643002495L;
	
	public InvalidPasswordException(String errorMsg){
		super(errorMsg);
	}
}
