package com.hollycrm.smcs.http.auth.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hollycrm.smcs.atomic.impl.ShowUserByIdAtomic;
import com.hollycrm.smcs.atomic.strategy.AbstractSinaStrategy;
import com.hollycrm.smcs.email.util.EmailUtils;
import com.hollycrm.smcs.entity.base.App;
import com.hollycrm.smcs.entity.base.IdGroup;
import com.hollycrm.smcs.entity.base.IdOauth;
import com.hollycrm.smcs.entity.base.OfficalBlog;
import com.hollycrm.smcs.http.AuthToken;
import com.hollycrm.smcs.http.InvalidPasswordException;
import com.hollycrm.smcs.http.auth.IAutoAuth;
import com.hollycrm.smcs.http.impl.AuthClient;
import com.hollycrm.smcs.remote.exception.RmiException;
import com.hollycrm.smcs.service.base.AppService;
import com.hollycrm.smcs.service.base.IdOauthService;
import com.hollycrm.smcs.service.base.OfficalBlogService;

@Component
public class AutoAuth implements IAutoAuth{
	
private final Logger logger=LoggerFactory.getLogger(AutoAuth.class);
	
	@Resource
	private IdOauthService idOauthService;
	
	@Resource
	private OfficalBlogService officalBlogService;
	
	@Resource
	private AppService appService;


	@Override
	public String autoOauth(IdOauth oauth, boolean flag) {
		try{
			OfficalBlog officalBlog=this.officalBlogService.getOfficalBlogByBloggerPK(oauth.getSid(),oauth.getMediaType());
			if(officalBlog == null){
				return null;
			}
			if(flag && !officalBlog.isStatus()){
				logger.info(oauth.getScreenName()+"授权失败，登录名密码错误");
				oauth.setExpired(true);
				officalBlogService.loginIsLost(officalBlog);
				return null;
			}
			App app=appService.get(oauth.getApp().getId());
			AuthClient autoOauth=new AuthClient(app,officalBlog.getUsername(),
					OfficalBlog.decode(officalBlog.getPassword()),oauth.getGroupId());
			AuthToken token = null;
			try{
				token = autoOauth.authorizeSina(); 
			}catch(InvalidPasswordException e){
				logger.error(oauth.getScreenName()+"授权应用"+app.getAppName()+"失败，原因："+e.getLocalizedMessage());
				officalBlogService.updateStatus(officalBlog);
				EmailUtils.sendEmail("授权失败", oauth.getScreenName()+"授权应用"
						+app.getAppName()+"失败，原因："+e.getLocalizedMessage());
				oauth.setExpired(true);				
				return null;
			}catch(Exception e){
				logger.info(e.getLocalizedMessage(),e);
				oauth.setExpired(true);			
				EmailUtils.sendEmail("授权失败", oauth.getScreenName()+"授权应用"
						+app.getAppName()+"失败，原因："+e.getLocalizedMessage());
				return null;
			}			
			logger.info(oauth.getScreenName()+"授权应用"+app.getAppName()+"成功，授权信息=" + token.toString());				
			parseToken(oauth, token);
		}catch(Exception e){
			logger.error(e.getLocalizedMessage(),e);
			return null;
		}finally{
			idOauthService.save(oauth);
		}
		
		return oauth.getAccessToken();
	}

	@Override
	public String autoOauth(Long id) {
		return autoOauth(idOauthService.get(id), true);
	}

	@Override
	public void authApp(String username, String password, Long groupId, App app) throws Exception {		
		IdOauth oauth = new IdOauth();
		IdGroup idGroup = new IdGroup();
		idGroup.setId(groupId);
		oauth.setMediaType(AbstractSinaStrategy.FLAG);
		oauth.setIdGroup(idGroup);
		oauth.setApp(app);
		try{
			parseToken(oauth,new AuthClient(app, username,password,groupId).authorizeSina());
		}catch(Exception e){
			throw new RmiException("授权失败",e);
		}
				
		try{
			oauth.transform(new ShowUserByIdAtomic(oauth.getSid(), oauth.getMediaType()).call());
		}catch(Exception e){
			logger.error(e.getLocalizedMessage(), e);
			throw new RmiException("获取博主信息出错",e);
		}
		
		idOauthService.save(oauth);
		
	}
	
	/*
	 * 
	 * 
	 *
	 * */
	
	/**
	 * url中包含的内容
	 *  http://wbkf.hollycrm.com/oauth/oauth!callback.do#
	 *  access_token=2.00TkMXeC1vlHnDc7017f710a0i3I5g&remind_in=260314
	 *  &expires_in=260314&uid=2431232937
	 *0 accessToke ,
	 *1 remind ,
	 *2 expires,
	 *3 uid
	 *
	 * 从url中解析出token 
	 * 
	 * @param oauth
	 * @param url
	 * @throws Exception
	 */
	private void parseToken(IdOauth oauth, AuthToken token) throws Exception{
		oauth.setAccessToken(token.getAccessToken());
		oauth.setExpireIn(token.getExpiresIn());
		oauth.setSid(token.getUid());
		oauth.setExpired(false);
		
	}

}
