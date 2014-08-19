package com.hollycrm.smcs.http.httpstatus;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;

import com.hollycrm.smcs.http.InvalidHttpClientException;

public abstract class AbstractHttpStatusFilter implements IHttpStatusFilter{

	@Override
	public void filter(HttpResponse response) throws Exception {
		if((response.getStatusLine().getStatusCode() >= HttpStatus.SC_BAD_REQUEST)){
  		throw new InvalidHttpClientException("错误http请求状态",response.getStatusLine().getStatusCode());
  	}
	}

}
