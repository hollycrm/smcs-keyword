package com.hollycrm.smcs.http.auth;

import com.hollycrm.smcs.entity.base.App;
import com.hollycrm.smcs.entity.base.IdOauth;

/**
 * 定义授权接口
 * 
 * @author fly
 *
 */

public interface IAutoAuth {
	
	/**
	 * 根据idOauth进行授权
	 * 
	 * @param oauth
	 * @param flag 表示是否需要考虑status状态
	 * @return 返回token 
	 */
	public String  autoOauth(IdOauth oauth, boolean flag);
	
	/**
	 * 根据idOauth id进行授权
	 * 
	 * @param id
	 * @return 返回token
	 */
	public String autoOauth(Long id);
	
	/**
	 * 根据账号对某个应该进行授权
	 * 
	 * @param username 账号名
	 * @param password 账号密码
	 * @param groupId
	 * @param app 应用
	 * @throws Exception
	 */
	void authApp(String username,String password,Long groupId,App app) throws Exception;

}
