package com.hollycrm.smcs.http.impl;

import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.SignatureType;
import org.scribe.oauth.OAuthService;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.entity.base.App;
import com.hollycrm.smcs.entity.base.IdOauth;
import com.hollycrm.smcs.entity.cookie.BaseCookie;
import com.hollycrm.smcs.http.AbstractHttpClient;
import com.hollycrm.smcs.http.AuthToken;
import com.hollycrm.smcs.http.InvalidPasswordException;
import com.hollycrm.smcs.http.StoreCookie;
import com.hollycrm.smcs.http.util.HttpClientLoginConfig;
import com.hollycrm.smcs.http.util.SinaConstant;
import com.hollycrm.smcs.http.util.SinaWeibo2Api;
import com.hollycrm.smcs.http31.HttpClient;
import com.hollycrm.smcs.security.impl.AuthSecurityCode;
import com.hollycrm.smcs.service.cookie.BaseCookieService;
import com.hollycrm.smcs.util.JsonUtil;
import com.hollycrm.smcs.util.OauthUtil;

public class AuthClient extends AbstractHttpClient{
	
	private static final long serialVersionUID = 6663962130959091920L;
	
	private final App app;
	
	/**授权用户名**/
	private final String username;
	
	/**授权用密码**/
	private final String password;	
	
	/**授权用组织**/
	private final Long groupId;
	
	
	public AuthClient(App app, String username,
			String password,Long groupId) {		
		super(new AuthSecurityCode(), new HttpClient());
		this.app = app;
		this.username=username;
		this.password=password;		  
	  this.groupId=groupId;
	  
	}
	
	/**
	 * 授权
	 * 
	 * @return
	 * @throws Exception
	 */
	public synchronized AuthToken authorizeSina() throws Exception{	
		
			OAuthService service = new ServiceBuilder().provider(SinaWeibo2Api.class).apiKey(app.getAppKey()).apiSecret(
					app.getAppSecret()).callback(app.getRedirectUri())
					.signatureType(SignatureType.QueryString).build();
			// 获取授权引导界面，引导用户至授权界面
			final String authUrl = service.getAuthorizationUrl(null);
			String ticket = login(false, authUrl);
			if(ticket == null){
				throw new Exception("授权失败");
			}
			String authorizeUrl = "https://api.weibo.com/2/oauth2/authorize";
			Map<String, String> map = new HashMap<String, String>();
			map.put("action", "submit");
			map.put("client_id", app.getAppKey());
			map.put("redirect_uri", app.getRedirectUri());
			map.put("response_type", "token");
			map.put("ticket", ticket);
			map.put("userId", username);
			map.put("withOfficalFlag", "0");
			Map<String, String> headers = new HashMap<String, String>(1);
			headers.put("Referer", authUrl);
			String tokenUrl = client.postResponseHeader("Location", authorizeUrl, map, headers);
			logger.info("授权成功token url ="+ tokenUrl);
			return parseTokenUrl(tokenUrl);
		
		
	}
	
	
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
	private AuthToken parseTokenUrl(String url) throws Exception{
		url = url.substring(url.indexOf("access_token"));
		String [] urlArray=url.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for(int i=0;i<urlArray.length;i++){
			String [] keys = urlArray[i].split("=");
			if(keys.length != 2) {
				continue;
			}
			map.put(keys[0].trim(), keys[1].trim());
		}
		AuthToken token =  new AuthToken();
		token.setAccessToken(map.get("access_token"));
		token.setExpiresIn((map.get("expires_in")));
		token.setUid(Long.parseLong(map.get("uid")));
		return token;
	}
	
	/**
	 * 用cookie进行登录
	 * @return
	 */
	private void loginByCookie(){
		try {
			List<BaseCookie> list = ApplicationContextHolder.getBean(BaseCookieService.class).findExpireBaseCookie(username, "w");
			if((list == null) || list.isEmpty()){
				return;
			}		
			isLogin(this.simpleHttpGet(HttpClientLoginConfig.get(SinaConstant.SINA_PROTAL_URL),null,new DefaultSetCookie(list),null));
		} catch (Exception e) {
			logger.error(e.toString(),e);
			return;			
		}
		
	}
	
	
	/**
	 * 授权登录
	 * 
	 * @param flag 是否是验证码
	 * @param authUrl 授权地址
	 * @return 返回
	 * @throws Exception
	 */
	private String login(boolean flag, String authUrl) throws Exception{
		String su = OauthUtil.encodeAccount(username);
		String url ="https://login.sina.com.cn/sso/prelogin.php?entry=openapi&callback=sinaSSOController.preloginCallBack&su="
						+ su + "&rsakt=mod&client=ssologin.js(v1.3.22)&_=" + new Date().getTime();
		
		String entity = client.simpleHttpGet(url);
		String jsonStr = entity.substring(35, entity.length() - 1);		
		Map map = JsonUtil.getMap4Json(jsonStr);
		String servertime = (map.get("servertime")).toString();		
		String pcid = (String) map.get("pcid");
		String nonce=(String) map.get("nonce");
		String rsakv=(String) map.get("rsakv");
		String pubkey=(String) map.get("pubkey");
		String realServerTime=getRealServerTime(servertime);
		StringBuilder sb = new StringBuilder(500);
		sb.append("https://login.sina.com.cn/sso/login.php?entry=openapi&gateway=1&from=&savestate=0&useticket=1&ct=1800&s=1&vsnf=1&vsnval=&door=");
		if(flag){						
			sb.append(iSecurityCode.getDoor(dealPinCode(pcid,username,groupId,"auth")));
			sb.append("&pcid=");
			sb.append(pcid);
		}
		sb.append("&su=");
		sb.append(su);
		sb.append("&service=miniblog&servertime=");
		sb.append(realServerTime);
		sb.append("&nonce=");
		sb.append(nonce);
		sb.append("&pwencode=rsa2&rsakv=");
		sb.append(rsakv);
		sb.append("&sp=");
		sb.append(getEncryptPassword(getScriptEngine(),realServerTime,nonce,pubkey,password));
		sb.append("&encoding=UTF-8&callback=sinaSSOController.loginCallBack&cdult=2&domain=weibo.com&prelt=42&returntype=TEXT&client=ssologin.js(v1.3.22)&_=");		
		sb.append(new Date().getTime());	
		Map<String, String> headers =new HashMap<String, String>();
		headers.put("Referer", authUrl);
		headers.put("Host", "login.sina.com.cn");
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:9.0.1) Gecko/20100101 Firefox/9.0.1");
		headers.put("Connection", "keep-alive");			 
		entity =client.simpleHttpGet(sb.toString(), headers);		
		jsonStr = entity.substring(32, entity.length() - 2);	
		map = JsonUtil.getMap4Json(jsonStr);
		String retcode=(String) map.get("retcode");//{retcode=101, reason=登录名或密码错误}
		logger.info("授权map信息"+map);
		if(SECURITY_CODE.equals(retcode) && !flag){
			return login(true, authUrl);
		}else if(INVALID_PASSWORD_CODE.equals(retcode)){
			throw new InvalidPasswordException("登录名或密码错误");
		}else if("0".equals(retcode)){
			return (String) map.get("ticket");
		}else{
			throw new Exception(map.get("reason").toString());
		}
		
	}
	
	private boolean isLogin(String html) throws Exception {
		//http://weibo.com/sso/login.php?ssosavestate=1390957700&url=http%3A%2F%2Fweibo.com%2F&ticket=ST-MjI3NDQ5NTY0NA==-1388365700-yf-B8FC6696C44481B5759731109193E261&retcode=0
		//http://weibo.com/ajaxlogin.php?
		//http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack&ssosavestate=1390957814&ticket=ST-MjI3NDQ5NTY0NA==-1388365814-yf-9A0B71B175E95C5D37C3772899B9F8DE&retcode=0
		try{			
			if(isContainLogin(html)){							
				return true;
			}else if(html.indexOf("http://weibo.com/sso/login.php") != -1){
				loginSuccess(html,"http://weibo.com/sso/login.php");
				return true;
			}
			return false;
		}catch(Exception e){
			logger.info(e.getLocalizedMessage(), e);
			if(html.indexOf("$CONFIG['islogin'] = '1'")!=-1){
				return true;
			}
			return false;
		}
		
	}
	public static  boolean isContainLogin(String html){
		if(html.indexOf("$CONFIG['islogin'] = '1'")!=-1){
			return true;
		}
		return false;
	}
	
	private void loginSuccess(String entity, String key) throws Exception{
		String url = null;
		try{
			url = entity.substring(entity.indexOf(key),
					entity.indexOf("code=0") + 6);
		}catch(Exception e){
			throw new Exception(URLDecoder.decode(entity.substring(entity.indexOf("retcode"),entity.indexOf("</script>")-2), "GBK"));
		}
			
		StoreCookie storeCookie = new DefaultStoreCookie();
		this.simpleHttpGet(url,null,null,storeCookie);
		
	}
	
	// non-javadoc
	@Override
	public String getUsername() {
		return username;
	}
	
	//non-javadoc
	@Override
	public Long getBloggerId() {
		return null;
	}
	
	//non-javadoc
	@Override
	public int getType() {
		return 0;
	}

	public App getApp() {
		return app;
	}
	
}
