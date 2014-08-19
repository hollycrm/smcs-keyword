package com.hollycrm.smcs.http.httpclient.impl;

import java.util.List;

import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.entity.base.OfficalBlog;


/**
 * 发送http管理容器
 *
 * @author dingqj 
 * @since 
 * 2013-11-21 下午10:42:56
 */
public class SendHttpClientImpl extends PrivateHttpClientImpl{

	@Override
	protected List<OfficalBlog> findWaittingLoginOfficalBlog() {
		return this.officalBlogService.findSendPrivateOfficalBlog(Integer.parseInt(AppConfig.get("taskServer")));
	}

	

}
