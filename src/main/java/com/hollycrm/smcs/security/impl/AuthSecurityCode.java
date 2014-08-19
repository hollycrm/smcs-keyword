package com.hollycrm.smcs.security.impl;

import com.hollycrm.smcs.security.AbsSecurityCode;
import com.hollycrm.smcs.security.SecurityCode;

public class AuthSecurityCode extends AbsSecurityCode{

	@Override
	public int getType() {
		return SecurityCode.AUTH_SECURITY_CODE;
	}

	@Override
	protected String getSecurityTitle() {
		return "授权";
	}

	

}
