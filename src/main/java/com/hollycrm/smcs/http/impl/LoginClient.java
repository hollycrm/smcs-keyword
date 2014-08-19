package com.hollycrm.smcs.http.impl;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.script.ScriptEngine;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.entity.cookie.BaseCookie;
import com.hollycrm.smcs.http.AbstractHttpClient;
import com.hollycrm.smcs.http.InvalidPasswordException;
import com.hollycrm.smcs.http.StoreCookie;
import com.hollycrm.smcs.http.util.HttpClientLoginConfig;
import com.hollycrm.smcs.http.util.SinaConstant;
import com.hollycrm.smcs.http42.HttpClient;
import com.hollycrm.smcs.security.impl.LoginSecurityCode;
import com.hollycrm.smcs.service.cookie.BaseCookieService;
import com.hollycrm.smcs.util.JsonUtil;
import com.hollycrm.smcs.util.OauthUtil;

public class LoginClient extends AbstractHttpClient{

	private static final long serialVersionUID = -2808593651193420683L;
	
	
	/**登录新浪账号**/
	private final String username;
	
	/**登录新浪密码**/
	private final String password;
	
	/**组织id**/
	private final Long groupId;
	
	/**博主ID**/
	private final Long bloggerId;
	
	
	private volatile boolean isHandleCookie = false;
	
	private final  int type;
	
	public LoginClient(String username,String password,Long groupId,Long bloggerId) throws Exception{
		this(username, password, groupId, bloggerId ,0);//0 展示登录账号微博类型
	}
	
	
	public LoginClient(String username,String password,Long groupId,Long bloggerId,int type) throws Exception{
		super(new LoginSecurityCode(),new HttpClient());
		this.username=username;
		this.password=password;
		this.groupId=groupId;	
		this.bloggerId = bloggerId;
		this.type = type;
		init();
	}
	
	private void init() throws Exception{	
		//用cookie进行登录
		if(loginByCookie()){
			logger.info("账号{"+username+"},通过cookie登录成功！");
			return;
		}
		isHandleCookie = true;
		String entity=login(false);		
		if((entity.indexOf("location.replace") >= 0) && (entity.indexOf("retcode=4049") >= 0)){
			entity = login(true);
		}else if((entity.indexOf("retcode=101") >= 0)){
			throw new InvalidPasswordException("登录名或密码错误");
		}
		loginSuccess(entity, HttpClientLoginConfig.get(SinaConstant.SINA_AJAX_LOGIN));
	}
	
	/**
	 * 用cookie进行登录
	 * @return
	 */
	private boolean loginByCookie(){
		try {
			List<BaseCookie> list = ApplicationContextHolder.getBean(BaseCookieService.class).findExpireBaseCookie(username, "w");
			if((list == null) || list.isEmpty()){
				return false;
			}		
			return isLogin(this.simpleHttpGet(HttpClientLoginConfig.get(SinaConstant.SINA_PROTAL_URL),null,new DefaultSetCookie(list),null));
		} catch (Exception e) {
			logger.error(e.toString(),e);
			return false;			
		}
		
	}
	
	
	
	public static  String getElementFromHtml(String html, String key){
		final String p = "$CONFIG['"+key+"'] = ";
		int start = html.indexOf(p);
		if(start == -1){
			throw new NoSuchElementException("not element"+p);
		}
		int end = html.indexOf(";", start);
		return html.substring(start + p.length()+1, end - 1);
	}
	
	public static  boolean isContainLogin(String html){
		if(html.indexOf("$CONFIG['islogin'] = '1'")!=-1){
			return true;
		}
		return false;
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
		if(isHandleCookie){
			storeCookie.storeCookie(username, "w");
		}
	}
	
	/**
	 * 登录 新浪
	 * 
	 * @param flag true 有验证码 ,false 没有验证码
	 */
	
	@SuppressWarnings("rawtypes")
	private String login(boolean flag) throws Exception{
		String su=flag?OauthUtil.encodeAccount(username):"";
		String entity=simpleHttpGet(HttpClientLoginConfig.get(SinaConstant.SINA_PERLOGIN_URL)+"&su="+su+"&_="+System.currentTimeMillis());
		String jsonStr = entity.substring(35, entity.length() - 1);			
		Map map3 = JsonUtil.getMap4Json(jsonStr);
		String servertime = (map3.get("servertime")).toString();
		String nonce=(String) map3.get("nonce");
		String rsakv=(String) map3.get("rsakv");
		String pubkey=(String) map3.get("pubkey");
		String pcid = (String) map3.get("pcid");
		Map<String,String> loginMap=new HashMap<String,String>();	
		loginMap.put("entry", HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_ENTRY));
		loginMap.put("gateway", HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_GATEWAY));
		loginMap.put("from", HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_FROM));
		loginMap.put("savestate", HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_SAVESTATE));
		loginMap.put("useticket", HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_USETICKET));
		loginMap.put("ssosimplelogin", HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_SSOSIMPLELOGIN));
		loginMap.put("su", OauthUtil.encodeAccount(username));
		loginMap.put("service", HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_SERVICE));
		loginMap.put("pwencode", HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_PWENCODE));
		loginMap.put("returntype", HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_RETURNTYPE));
		loginMap.put("encoding", HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_ENCODING));
		loginMap.put("vsnf", HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_VSNF));
		loginMap.put("url",HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_PARAMURL));
		loginMap.put("vsnval", HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_VSNVAL));
		loginMap.put("prelt", HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_PRELT));
		if(flag){           //取验证码			
			loginMap.put("door", iSecurityCode.getDoor(dealPinCode(pcid,username,groupId,"login")));			
		}
		String realServerTime=getRealServerTime(servertime);
		ScriptEngine engine=getScriptEngine();
		loginMap.put("rsakv", rsakv);
		loginMap.put("servertime", realServerTime);
		loginMap.put("nonce", nonce);			
		loginMap.put("sp", getEncryptPassword(engine,realServerTime,nonce,pubkey,password));	
		entity=this.post(HttpClientLoginConfig.get(SinaConstant.SINA_LOGIN_URL), loginMap, null);
		logger.info("登录返回的html=\n"+entity);
		return entity;
	}
	
	
	
	@Override
	public String getUsername() {
		return username;
	}
	

	@Override
	public Long getBloggerId() {
		return bloggerId;
	}

	@Override
	public int getType() {
		return 0;
	}

}
