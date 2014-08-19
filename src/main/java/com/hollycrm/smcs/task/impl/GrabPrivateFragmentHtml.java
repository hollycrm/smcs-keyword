package com.hollycrm.smcs.task.impl;

import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.httpclient.impl.PrivateHttpClientContainer;
import com.hollycrm.smcs.task.IDispatcher;

public class GrabPrivateFragmentHtml extends GrabFragmentHtml{

	

	public GrabPrivateFragmentHtml(Fragment fragment, IDispatcher dispatcher) {
		super(fragment, dispatcher);
	}

	@Override
	public IHttpClient obtainHttpClient() {
		return PrivateHttpClientContainer.obtainHttpClient(fragment.getBloggerId());
	}

}
