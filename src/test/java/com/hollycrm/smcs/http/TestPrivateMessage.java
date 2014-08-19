package com.hollycrm.smcs.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

import weibo4j.http.BASE64Encoder;

import com.hollycrm.smcs.util.JsonUtil;


/**
 * 测试接收消息
 * @author fly
 *
 */
public class TestPrivateMessage {
	HttpClient client;
	
	
	@Before
	public void init(){
		 // 使用默认的 socket factories 注册 "http" & "https" protocol scheme
    SchemeRegistry supportedSchemes = new SchemeRegistry();
    supportedSchemes.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
    supportedSchemes.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
    PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager(supportedSchemes);    
 // 参数设置
    HttpParams httpParams = new SyncBasicHttpParams();
    HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);    
    HttpProtocolParams.setUseExpectContinue(httpParams, false);
    HttpClientParams.setCookiePolicy(httpParams, CookiePolicy.BROWSER_COMPATIBILITY);
     client = new DefaultHttpClient(connectionManager, httpParams);
    
	}
	
	/**
	 * 接收消息
	 * @throws InvalidHttpClientException
	 * @throws Exception
	 */
	@Test
	public void receiveMessage() throws InvalidHttpClientException, Exception{		
		String url = "https://m.api.weibo.com/2/messages/receive.json?source=3474291478&uid=3165121060";
		final HttpGet httpGet = new HttpGet(url);   
		httpGet.setHeader("Authorization", "Basic "+BASE64Encoder.encode(("weibo@hollycrm.com"+":"+"mn12345678Az").getBytes()));
		HttpResponse response = null;
		
  	try{
  		response = client.execute(httpGet);
  	}catch(SocketTimeoutException ste){
  		throw new InvalidHttpClientException(ste.getLocalizedMessage(),-1);
  	}catch(ConnectionPoolTimeoutException cpte){
  		throw new InvalidHttpClientException(cpte.getLocalizedMessage(),-1);
  	}
  	 Thread thread = new Thread(new Runnable(){

				@Override
				public void run() {
					
					try {
						Thread.sleep(1000L);
						httpGet.abort();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
     	
     });
     thread.start();
  	
  	String responseData =fetchResult(response.getEntity(), (Charset)null);
  	if((response.getStatusLine().getStatusCode() >= HttpStatus.SC_BAD_REQUEST)){
  		System.out.println(response.getStatusLine().getStatusCode());
  		throw new InvalidHttpClientException("错误http请求状态",response.getStatusLine().getStatusCode());
  	}
    
    System.out.println(responseData);
	}
	
	private  String fetchResult(
      final HttpEntity entity, final Charset defaultCharset) throws IOException, ParseException {
  if (entity == null) {
      throw new IllegalArgumentException("HTTP entity may not be null");
  }
  final InputStream instream = entity.getContent();
  if (instream == null) {
      return null;
  }
  try {
      if (entity.getContentLength() > Integer.MAX_VALUE) {
          throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
      }
      int i = (int)entity.getContentLength();
      if (i < 0) {
          i = 4096;
      }
      ContentType contentType = ContentType.getOrDefault(entity);
      Charset charset = contentType.getCharset();
      if (charset == null) {
          charset = defaultCharset;
      }
      if (charset == null) {
          charset = HTTP.DEF_CONTENT_CHARSET;
      }
      final Reader reader = new InputStreamReader(instream, charset);
      CharArrayBuffer buffer = new CharArrayBuffer(i);
      char[] tmp = new char[1024];
      int l;
     
      try{
      	while((l = reader.read(tmp)) != -1) {
      		if((tmp[l-2] != '\r') || (tmp[l-1] != '\n')) {
        		buffer.append(tmp, 0, l);
        	} else {
        		buffer.append(tmp, 0, l);
        		System.out.println(buffer.toString());
        		Map map = JsonUtil.getMap4Json(buffer.toString());
        		System.out.println(map.get("created_at"));
        		System.out.println(map.get("ssss") == null);
        		buffer.clear();
        	} 
      	}
      	}catch(Exception e){
      		e.printStackTrace();
      	}
      
      
      return buffer.toString();
  } finally {
      instream.close();
  }
}
	
	
	@Test
	public void downloadPid() throws ClientProtocolException, IOException{
		String url = "https://upload.api.weibo.com/2/mss/msget?access_token=2.00jZrXqC1vlHnD26d8fd4337tF9juC&fid=993617228";
	//	String url = "http://ww1.sinaimg.cn/thumbnail/4ffbcf0ajw1e9kosgr00fj20bz0addh4.jpg";
		HttpGet get = new HttpGet(url);
		
		HttpResponse response = client.execute(get);
		Header[] header =response.getAllHeaders();
		HttpEntity httpentity = response.getEntity();
		
	}
	
	
	/**
	 * 发送消息
	 * @throws Exception
	 */
	@Test
	public void replyMessage() throws Exception{
		String url = "https://m.api.weibo.com/2/messages/reply.json";
		HttpPost post = new HttpPost(url);
		post.setHeader("Authorization", "Basic "+ BASE64Encoder.encode(("weibo@hollycrm.com"+":"+"mn123145678Az").getBytes()));			
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("source","3474291478"));
		pairs.add(new BasicNameValuePair("id","1310250000121896"));
		pairs.add(new BasicNameValuePair("type","text"));
		String data = "{\"text\": \"纯文本回复\"}";
		/*
		String data = "{\"articles\": [ " +
				"{\"display_name\": \"两个故事\"," +
				"\"summary\": \"今天讲两个故事，分享给你。谁是公司？谁又是中国人？\","+
            "\"image\": \"http://storage.mcp.weibo.cn/0JlIv.jpg\","+
            "\"url\": \"http://e.weibo.com/mediaprofile/article/detail?uid=1722052204&aid=983319\"  }" +
            ","+
            "{\"display_name\": \"333两个故事\"," +
            "\"summary\": \"3333今天讲两个故事，分享给你。谁是公司？谁又是中国人？\","+
            "\"image\": \"http://img.t.sinajs.cn/t4/appstyle/V5_message/images/pic/desk170.jpg\","+
            "\"url\": \"http://e.weibo.com/mediaprofile/article/detail?uid=1722052204&aid=983319\"  }" +
            "  ]}";*/
		
		pairs.add(new BasicNameValuePair("data",data));
		
		post.setEntity(new UrlEncodedFormEntity(pairs, Charset.forName("UTF-8")));
  
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
		HttpResponse response = null;
  	try{
  		response = client.execute(post);
  	}catch(SocketTimeoutException ste){
  		throw new InvalidHttpClientException(ste.getLocalizedMessage(),-1);
  	}catch(ConnectionPoolTimeoutException cpte){
  		throw new InvalidHttpClientException(cpte.getLocalizedMessage(),-1);
  	}
  	System.out.println((response.getStatusLine().getStatusCode()));
  	String responseData = EntityUtils.toString(response.getEntity());
  	System.out.println(responseData);
	}
	
	/**
	 * 发送提醒
	 * @throws Exception
	 */
	@Test
	public void sendMessage() throws Exception{
		String url = "https://m.api.weibo.com/2/messages/send.json";
		HttpPost post = new HttpPost(url);
		post.setHeader("Authorization", "Basic "+BASE64Encoder.encode(("weibo@hollycrm.com"+":"+"wbkf12345678Az").getBytes()));			
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("source","3474291478"));
		pairs.add(new BasicNameValuePair("sender_id","2608705907"));
		pairs.add(new BasicNameValuePair("recipient_id","1738026657"));
		pairs.add(new BasicNameValuePair("type","text"));
		String data = "{\"text\": \"消息提醒\"}";
		
		/*String data = "{\"articles\": [ {\"display_name\": \"两个故事\"," +
				"\"summary\": \"今天讲两个故事，分享给你。谁是公司？谁又是中国人？\","+
            "\"image\": \"http://storage.mcp.weibo.cn/0JlIv.jpg\","+
            "\"url\": \"http://e.weibo.com/mediaprofile/article/detail?uid=1722052204&aid=983319\"  }  ]}";*/
		
		pairs.add(new BasicNameValuePair("data",URLEncoder.encode(data)));
		
		post.setEntity(new UrlEncodedFormEntity(pairs, Charset.forName("UTF-8")));
  
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
		HttpResponse response = null;
  	try{
  		response = client.execute(post);
  	}catch(SocketTimeoutException ste){
  		throw new InvalidHttpClientException(ste.getLocalizedMessage(),-1);
  	}catch(ConnectionPoolTimeoutException cpte){
  		throw new InvalidHttpClientException(cpte.getLocalizedMessage(),-1);
  	}
  	System.out.println(response.getStatusLine().getStatusCode());
  	
  	String responseData = EntityUtils.toString(response.getEntity());
  	System.out.println(responseData);
	}
	
	
	
	@Test
	public void test34(){
		String value ="Content-Disposition: attachment;filename=\"20130814124334-121104320.png\"";
		System.out.println(value.substring(value.indexOf("filename")+10, value.length()-1));
	}
	

}
