package com.hollycrm.smcs.http;

import org.junit.Before;
import org.junit.Test;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.remote.impl.SecurityCodeProxy;

public class SecurityCodeTest {
	
	@Before
	public void init() throws Exception{
		AppConfig.init("smcs-keyword.properties");
		ApplicationContextHolder.init();
	}
	
	@Test
	public void inputSecurityCode() throws Exception{
		ApplicationContextHolder.getBean(SecurityCodeProxy.class).putSecurityCode("134", "abc", 1);
		System.out.println(ApplicationContextHolder.getBean(SecurityCodeProxy.class).getSecurityCode("134", 1));
	}

}
