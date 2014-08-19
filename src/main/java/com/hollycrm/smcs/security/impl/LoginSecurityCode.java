package com.hollycrm.smcs.security.impl;

import com.hollycrm.smcs.security.AbsSecurityCode;
import com.hollycrm.smcs.security.SecurityCode;

public class LoginSecurityCode extends AbsSecurityCode{

	@Override
	public int getType() {
		return SecurityCode.LOGIN_SECURITY_CODE;
	}

	@Override
	protected String getSecurityTitle() {
		return "登录";
	}

	

}
