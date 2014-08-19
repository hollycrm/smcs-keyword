package com.hollycrm.smcs.http.httpstatus;

import org.apache.http.HttpResponse;

public interface IHttpStatusFilter {

	void filter(HttpResponse response) throws Exception;
}
