package com.hollycrm.smcs.http;

import org.junit.Test;

public class TestException {
	
	@Test
	public void getExceptionStackTrace(){
		
		Exception e = new Exception("wewew");
		System.out.println(e.toString());
		System.out.println(e);
		StackTraceElement[] trace = e.getStackTrace();
		for(StackTraceElement ste:trace){
			System.out.println(ste.toString());
		}
		
		
	}
	

}
