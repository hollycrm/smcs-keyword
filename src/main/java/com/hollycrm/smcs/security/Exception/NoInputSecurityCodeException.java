package com.hollycrm.smcs.security.Exception;

public class NoInputSecurityCodeException extends Exception{

	private static final long serialVersionUID = -8332156775505875314L;
	
	public NoInputSecurityCodeException(String msg){
		super(msg);
	}

}
