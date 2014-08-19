package com.hollycrm.smcs.security.impl;

import com.hollycrm.smcs.security.AbsSecurityCode;
import com.hollycrm.smcs.security.SecurityCode;

public class KeywordSecurityCode extends AbsSecurityCode{



	@Override
	public int getType() {
		return SecurityCode.KEYWORD_SECURITY_CODE;
	}

	@Override
	protected String getSecurityTitle() {
		return "关键字";
	}

}
