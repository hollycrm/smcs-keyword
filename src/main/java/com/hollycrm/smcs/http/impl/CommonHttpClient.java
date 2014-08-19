package com.hollycrm.smcs.http.impl;

import com.hollycrm.smcs.http.AbstractHttpClient;
import com.hollycrm.smcs.http42.HttpClient;

public class CommonHttpClient extends AbstractHttpClient{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7408229714559090960L;

	public CommonHttpClient() {
		super(null, new HttpClient());
	}

	@Override
	public String getUsername() {
		return null;
	}

	@Override
	public Long getBloggerId() {
		return null;
	}

	@Override
	public int getType() {
		return 0;
	}

}
