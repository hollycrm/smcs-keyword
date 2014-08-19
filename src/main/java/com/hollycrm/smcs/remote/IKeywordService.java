package com.hollycrm.smcs.remote;

import java.rmi.Remote;

public interface IKeywordService extends Remote{
	
	/**
	 * 重新登录
	 * @param bloggerId
	 * @throws Exception
	 */
	void addReLogin(Long bloggerId) throws Exception;

}
