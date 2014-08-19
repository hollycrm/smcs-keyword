package com.hollycrm.smcs.http;

import java.io.FileReader;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.security.ISecurityCode;
import com.hollycrm.smcs.util.OauthUtil;

public abstract class AbstractHttpClient implements Serializable,IHttpClient{
	
	
	


	private static final long serialVersionUID = -8550439711635256411L;

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**验证码处理类**/
	protected final ISecurityCode iSecurityCode;
	
	/**普通httpclient**/
	protected ICommonHttpClient client;
	
	public static final String INVALID_PASSWORD_CODE = "101";
	
	public static final String SECURITY_CODE = "4049";
	
	
	public AbstractHttpClient(ISecurityCode iSecurityCode, ICommonHttpClient httpClient){
		this.iSecurityCode = iSecurityCode;
		this.client = httpClient;
	}

	/**
	 * 根据服务器时间计算真实时间
	 * 
	 * @param servertime
	 * @return 真实时间串
	 */
	public String getRealServerTime(String servertime) {
		long preloginTimeEnd = new Date().getTime();
		long t1 = OauthUtil.getServerTimeLong();
		
		long tmp = t1 - (preloginTimeEnd / 1000);		
		return  "" + (Long.parseLong(servertime) + (2 * (tmp / 2)));		
	}
	
	/**
	 * 获取脚本引擎
	 * 
	 * @return ScriptEngine 脚本引擎
	 * @throws Exception
	 */
	public ScriptEngine getScriptEngine() throws Exception{
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		ScriptEngine engine = scriptEngineManager.getEngineByName("JavaScript");
		if (engine == null) {
			System.err.println("No script engine found for javascript");
			return null;
		}
	String path=AbstractHttpClient.class.getClassLoader().getResource("").getPath();
		String jsFileName = path+"/sinasso.js";
		logger.info(jsFileName);
		FileReader reader = new FileReader(jsFileName);
		engine.eval(reader);	
		reader.close();
		return engine;
	}
	
	/**
	 * 对密码进行加密
	 * 
	 * @param engine js脚本引擎
	 * @param servertime 时间
	 * @param nonce 新浪返回nonce
	 * @param pubkey 新浪返回pubkey 
	 * @param password 密码
	 * @return 加密密码
	 * @throws Exception
	 */
	public String getEncryptPassword(ScriptEngine engine, String servertime,String nonce,String pubkey, String password) throws Exception{			
		String text = "['" + servertime + "','" + nonce + "'].join('\t') + '\\n' + '"+password+"'";
		String scriptSource = "var RSAKey = new sinaSSOEncoder.RSAKey();RSAKey.setPublic('" + pubkey
				+ "', '10001');var text = " + text + ";RSAKey.encrypt(text);";
		return (String) engine.eval(scriptSource);
	}
	
	/**
	 * 处理验证码
	 * 
	 * @param pcid 图片pid
	 * @param username 用户名
	 * @param groupId 
	 * @param suffix 图片后缀 默认.jpg
	 * @return 图片名称
	 * @throws Exception
	 */
	protected String dealPinCode(String pcid, String username, Long groupId, String suffix) throws Exception{
		String pinCodeURL = "http://login.sina.com.cn/cgi/pin.php?r="
				+ new String(new Double(Math.floor(Math.random() * 100000000)).toString()) + "&s=0&p=" + pcid;
		if(logger.isDebugEnabled()){
			logger.debug(pinCodeURL);
		}
		String fileName = downloadPic(pinCodeURL,"pincode", ".jpg", suffix);
		return	iSecurityCode.save(username, groupId, fileName);
	}
	
	/**
	 * 
	 * @see ICommonHttpClient
	 */	
	@Override
	public String simpleHttpGet(String url) throws InvalidHttpClientException, Exception {
		return client.simpleHttpGet(url);
	}

	/**
	 * 
	 * 
	 * @see ICommonHttpClient
	 */	
	@Override
	public String simpleHttpGet(String url, Map<String, String> headers) throws InvalidHttpClientException, Exception {
		return client.simpleHttpGet(url, headers);
	}

	@Override
	public String simpleHttpGet(String url, Map<String, String> headers, SetCookie setCookie, StoreCookie storeCookie)
			throws InvalidHttpClientException, Exception {
		return client.simpleHttpGet(url, headers, setCookie, storeCookie);
	}
	
	
	/**
	 * @see ICommonHttpClient
	 */	
	@Override
	public byte[] httpGetWithByte(String url) throws InvalidHttpClientException, Exception {
		return client.httpGetWithByte(url);
	}

	/**
	 * @see ICommonHttpClient
	 */	
	@Override
	public String post(String url, Map<String, String> map, Map<String, String> headers) throws InvalidHttpClientException, Exception {
		return client.post(url, map, headers);
	}

	/**
	 * @see ICommonHttpClient
	 */	
	@Override
	public String postBody(String url, String body) throws InvalidHttpClientException, Exception {
		return client.postBody(url, body);
	}

	/**
	 * @see ICommonHttpClient
	 */
	@Override
	public String httpPostWithFile(String url, Map<String, String> params, List<AttachItem> items) throws InvalidHttpClientException, Exception {
		
		return client.httpPostWithFile(url, params, items);
		//return null;
	}

	/**
	 * @see ICommonHttpClient
	 */
	@Override
	public String downloadPic(String url, String parentFile, String suffix, String prefix) throws InvalidHttpClientException, Exception {
		return client.downloadPic(url, parentFile, suffix, prefix);
	}

	/**
	 * @see ICommonHttpClient
	 */
	@Override
	public String postResponseHeader(String header, String url, Map<String, String> params, Map<String, String> headers)
			throws InvalidHttpClientException, Exception {
		//return client.postResponseHeader(header, url, params, headers);
		return null;
	}

	/**
	 * @see ICommonHttpClient
	 */
	@Override
	public Map<String, String> postResponseHeader(String url, Map<String, String> params, Map<String, String> headers)
			throws InvalidHttpClientException, Exception {
		//return client.postResponseHeader(url, params, headers);
		return null;
	}

	/**
	 * @see ICommonHttpClient
	 */
	@Override
	public Map<String, String> postFile(String url, Map<String, String> params, List<AttachItem> items)
			throws InvalidHttpClientException, Exception {
		return client.postFile(url, params, items);
	}
	
	@Override
	public HttpEntity execute(HttpUriRequest request) throws Exception {
		return client.execute(request);
	}

	@Override
	public HttpClient getOriginalClient() {
		return client.getOriginalClient();
	}

	
	
}
