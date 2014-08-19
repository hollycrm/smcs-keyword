package com.hollycrm.smcs.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.SignatureType;
import org.scribe.oauth.OAuthService;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.http.impl.CommonHttpClient;
import com.hollycrm.smcs.http.impl.LoginClient;
import com.hollycrm.smcs.http.util.SinaWeibo2Api;
import com.hollycrm.smcs.http42.HttpClient;




public class HttpClientTest {
	private ICommonHttpClient client;
	
	private String appKey = "3474291478";
	private String appSecret = "6fc43b27d2f969c51824ef05c104ac9d";
	
	@Before
	public void init() throws Exception{
		ApplicationContextHolder.init();
		AppConfig.init("smcs-keyword.properties");
		//client = new CommonHttpClient();
		client = new LoginClient("sghakww1@sina.com", "12345678Az", 1L, 5075829468L);
		//client = new LoginClient("shecook@sina.com", "wenyi84784001", null, 2608705907L);
		//AuthClient client = new AuthClient("3474291478","6fc43b27d2f969c51824ef05c104ac9d","sghakww8512@163.com","flymotor1231",1L);
		//client.authorizeSina();
		//client = new HttpClient();
	}
	
	@Test
	public void auth() throws Exception {
		OAuthService service = new ServiceBuilder().provider(SinaWeibo2Api.class).apiKey(appKey).apiSecret(
				appSecret).callback("http://wbkf.hollycrm.com/oauth/oauth!callback.do")
				.signatureType(SignatureType.QueryString).build();
		// 获取授权引导界面，引导用户至授权界面
		final String authUrl = "https://api.weibo.com/2/oauth2/authorize?client_id=3474291478&redirect_uri=http://wbkf.hollycrm.com/oauth/oauth!callback.do&response_type=token&forcelogin=false";//service.getAuthorizationUrl(null);
		System.out.println(authUrl);
		Map<String, String> header =  new HashMap<String, String> ();
		header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
		header.put("Referer", authUrl);
		header.put("Connection", "keep-alive");	
		
		String html = client.simpleHttpGet(authUrl, header);
		
		
		System.out.println(html);
	}
	
	@Test
	public void focus() throws InvalidHttpClientException, Exception{
		Map<String, String> map = new HashMap<String, String>();
		map.put("_t", "0");
		map.put("extra", "");
		map.put("f", "1");
		map.put("location", "page_100606_home");
		map.put("nogroup", "false");
		map.put("oid", "1732050982");
		map.put("refer_flag", "");
		map.put("refer_sort", "");
		map.put("uid", "1732050982");
		map.put("wforce", "1");
		String entity  = client.post("http://weibo.com/aj/f/followed?_wv=5&__rnd=1391494755751", map, null);
		System.out.println(entity);
	}
	
	
	@Test
	public void testsession() throws InvalidHttpClientException, Exception{
		
		String entity = client.simpleHttpGet("http://weibo.com/fav");
		if(!LoginClient.getElementFromHtml(entity,"islogin").equals("1")){
			System.out.println(entity);
		}
		
	}
	
	@Test
	public void authTudou() throws Exception{
		String url = "https://api.tudou.com/oauth2/access_token";
		Map<String ,String> map = new HashMap<String, String>();
		map.put("code", "c952cd826cbb5249d120c1a3a8305425");
		map.put("client_id", "974a8b73c983b4fe");
		map.put("client_secret", "d2381221ac83706ce1da15565e807420");
		System.out.println(client.post(url, map, null));
		
	}
	
	@Test
	public void postFile() throws Exception{
		String url="http://218.60.27.20/?token=114617761_183106319_653970437&appKey=974a8b73c983b4fe&sn=1387173319572";
		
		
		AttachItem item = new AttachItem("file", client.httpGetWithByte("http://127.0.0.1/tmp/1387264837216.wmv"));
		List<AttachItem> items = new ArrayList<AttachItem>(1);
		items.add(item);
		
		String map = client.httpPostWithFile(url, null, items);
		System.out.println(map);
	}
	
	@Test
	public void downPic() throws Exception {
		client = new HttpClient();
		String a = client.downloadPic("http://ww2.sinaimg.cn/bmiddle/85128273tw1e4j3d42kx1j20ku25cwme.jpg",
				"upload", ".jpg", "private");
		System.out.println(a);
	}
	
	@Test
	public void showMention() throws Exception{
		//https://api.weibo.com/2/statuses/show.json?id=3481475946781445&access_token=2.00BIactB0xnc971e9676c58f0e45_b
	try{
		IHttpClient client = new CommonHttpClient();
		client.post("http://127.0.0.1/Test?com=3", null, null);
	
	}catch(Exception e){
		e.printStackTrace();
	}
		
	}
	
	@Test
	public void postAuth() throws Exception{
		IHttpClient client = new LoginClient("bjct10000@sina.com", "bjkfywzczx10000", null, null);
		String url = "http://open.weibo.com/appconsole/ajax_app_basic_info_save.php";
		Map<String, String> map = new HashMap<String, String>();
		map.put("_t", "0");
		map.put("admin_url", "http://219.141.163.5:8080");
		map.put("app_desc", "为北京电信客户提供微博自助服务，包括业务查询、业务办理、充值缴费等");
		map.put("app_logo", "779f23f5jw1e6gmvu06t3j20280283ya");
		map.put("app_name", "电信自助服务");
		map.put("app_pics", "779f23f5gw1e6puum6c2oj20l40qo40b,779f23f5gw1e6puv0u72hj20l40qo0vr,779f23f5jw1e6pux745ctj20l40qodhl");
		map.put("appkey", "3880551156");
		map.put("bind_domain", "0");
		map.put("entapp_type","1");
		map.put("enterpriseSort", "1207");
		map.put("enterpriseTrade", "1100");
		map.put("is_enterprise_app", "1");
		map.put("is_inapp", "0");
		map.put("pic1", "");
		map.put("service_number", "10000");
		map.put("short_desc", "北京电信用户自助服务");
		map.put("source_url","http://219.141.163.5:8080");
		map.put("tiny_logo", "779f23f5jw1e6gmvr0yw6j200g00g0m0");
		map.put("uploadImageInput", "779f23f5gw1e6puum6c2oj20l40qo40b");
		map.put("wb_self", "1");
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Host", "open.weibo.com");
		headers.put("Referer", "http://open.weibo.com/apps/3880551156/info/basic");
		System.out.println(client.post(url, map, headers));
		
	}
	
	@Test
	public void loginByCookie() throws Exception{
		
		
	}

}
