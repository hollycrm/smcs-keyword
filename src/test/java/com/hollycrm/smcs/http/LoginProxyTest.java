package com.hollycrm.smcs.http;

import org.junit.Test;

import com.hollycrm.smcs.remote.ILoginProxy;
import com.hollycrm.smcs.remote.impl.KeywordLoginProxy;

public class LoginProxyTest {
	
	@Test
	public void testProxyTest() throws Exception{
		ILoginProxy proxy = new KeywordLoginProxy("rmi://127.0.0.1:1099/Rmi_Smcs_Core_Url1");
		proxy.addReLogin(1l);
	}
}
