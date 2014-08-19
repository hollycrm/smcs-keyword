package com.hollycrm.smcs.http42;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.http.AttachItem;
import com.hollycrm.smcs.http.ICommonHttpClient;
import com.hollycrm.smcs.http.InvalidHttpClientException;
import com.hollycrm.smcs.http.SetCookie;
import com.hollycrm.smcs.http.StoreCookie;
import com.hollycrm.smcs.http.client.HHeader;
import com.hollycrm.smcs.util.JsonUtil;



/**
 * 
 * @author fly
 *
 */
public  class HttpClient implements Serializable,ICommonHttpClient{
	protected final Logger logger=LoggerFactory.getLogger(getClass());

	/**
	 * 
	 */
	private static final long serialVersionUID = -1694571776706889183L;
	
	protected final org.apache.http.client.HttpClient client;
	
	public static final int CON_TIME_OUT_MS = 100000;
  public static final int SO_TIME_OUT_MS = 10000;
  public static final int MAX_CONNECTIONS_PER_HOST = 20;
  public static final int MAX_TOTAL_CONNECTIONS = 200;

  private  int conTimeOutMs;
  private  int soTimeOutMs;

	
	public HttpClient(){
		this(MAX_CONNECTIONS_PER_HOST,MAX_TOTAL_CONNECTIONS,CON_TIME_OUT_MS,SO_TIME_OUT_MS,null);
	}
	
	public HttpClient(int maxConnectionsPerHost, int maxTotalConnections, int conTimeOutMs, int soTimeOutMs, HttpHost proxy){
		this.conTimeOutMs=conTimeOutMs;
		this.soTimeOutMs=soTimeOutMs;
		 // 使用默认的 socket factories 注册 "http" & "https" protocol scheme
    SchemeRegistry supportedSchemes = new SchemeRegistry();
    supportedSchemes.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
    supportedSchemes.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
    PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager(supportedSchemes);
    
 // 参数设置
    HttpParams httpParams = new SyncBasicHttpParams();
    HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

    httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, conTimeOutMs);
    httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeOutMs);
    
    HttpProtocolParams.setUseExpectContinue(httpParams, false);

    connectionManager.setDefaultMaxPerRoute(maxConnectionsPerHost);
    connectionManager.setMaxTotal(maxTotalConnections);

    HttpClientParams.setCookiePolicy(httpParams, CookiePolicy.BROWSER_COMPATIBILITY);
    client = new DefaultHttpClient(connectionManager, httpParams);
    
    
    //设置代理
    if(null!=proxy){
    	client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    }
	}

	
	 /**
   * Get方法传送消息（无压缩）
   * 
   * @param url  连接的URL  
   * @return 服务器返回的信息
   * @throws Exception
   */
  @Override
	public String simpleHttpGet(String url) throws InvalidHttpClientException, Exception {
  	return simpleHttpGet(url, null);
  }
  
  @Override
	public String simpleHttpGet(String url, Map<String, String> headers) throws InvalidHttpClientException, Exception {
  	return simpleHttpGet(url,headers,null,null);
    
  }
  
	@Override
	public String simpleHttpGet(String url, Map<String, String> headers, SetCookie setCookie, StoreCookie storeCookie)
			throws InvalidHttpClientException, Exception {
		logger.info("HttpClient simpleHttpGet [1] url = " + url);
	    String responseData = null;
	    HttpGet httpGet = new HttpGet(url);   
	    setHeader(headers,httpGet);
	    if(setCookie != null){
	    	setCookie.setCookies((DefaultHttpClient) client);
	    }
	    HttpResponse response = execute(httpGet, "simpleHttpGet");
	    responseData = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
	    Header [] header2 =httpGet.getAllHeaders();
	    for(Header header:header2) {
	    	System.out.println(header.getName() +"||"+header.getValue());
	    }
	    if(storeCookie != null){
	    	storeCookie.store((DefaultHttpClient) client);
	    }
	    httpGet.abort();
	    return responseData;
	}
  
  @Override
	public byte[] httpGetWithByte(String url) throws InvalidHttpClientException, Exception{
  	
  	logger.info("HttpClient httpGetWithByte [1] url = " + url);
  	 HttpGet httpGet = new HttpGet(url); 
  	 HttpResponse response = execute(httpGet, "httpGetWithByte");
     byte[] results= EntityUtils.toByteArray(response.getEntity());     
     httpGet.abort();
    
     return results;
  	
  }
  
  @Override
	public String downloadPic(String url, String parentFile, String suffix, String prefix) throws InvalidHttpClientException, Exception{
  	
  	Map<String, String> map = new HashMap<String, String>();
  	map.put("parentFile", parentFile);
  	map.put("suffix", suffix);
  	map.put("prefix", prefix);
  	List<AttachItem> items = new ArrayList<AttachItem>(1);
  	items.add(new AttachItem("file", httpGetWithByte(url)));
  	String name = httpPostWithFile(AppConfig.get("upload.pic.url"), map, items);
  	Map<String, String> result = JsonUtil.getMap4Json(name);
  	if(result.get("success").endsWith("1")) {
			return parentFile+"/"+result.get("fileName");
		}
  	throw new Exception("下载图片出错");
		
  }
  
  /**
   * Get方法传送消息
   * 
   * @param url  连接的URL  
   * @return 服务器返回的信息
   * @throws Exception
   */
  public String httpGet(String url) throws InvalidHttpClientException, Exception {

      StringBuilder responseData = new StringBuilder();
      
      logger.info("HttpClient httpGet [1] url = " + url);

      HttpGet httpGet = new HttpGet(url);
      httpGet.addHeader("Accept-Encoding", "gzip,deflate,sdch");    

      HttpResponse  response = execute(httpGet, "httpGet");
      logger.info("HttpClient httpGet [2] StatusLine : " + response.getStatusLine());

      try {
          byte[] b=new byte[2048];
          GZIPInputStream gzin = new GZIPInputStream(response.getEntity().getContent());
          int length=0;
          while((length=gzin.read(b))!=-1){
              responseData.append(new String(b,0,length));
          }
          gzin.close();
      } catch (Exception e) {
          e.printStackTrace();
      } finally {
          httpGet.abort();
      }
    

      return responseData.toString();
  }
  
  /**
   * 服务器返回的信息
   * 
   * @param url
   * @param map
   * @param headers
   * @return 服务器返回的信息
   * @throws Exception
   */
  @Override
	public String post(String url,Map<String,String> map,Map<String,String> headers) throws InvalidHttpClientException, Exception{
     HttpPost httpPost = buildHttpPost(url, map, headers);
     HttpResponse response = execute(httpPost, "post");
     String responseData=EntityUtils.toString(response.getEntity());
     
     httpPost.abort();
     return responseData;
  }
  
  public HttpResponse postAndResponse(String url,Map<String,String> map,Map<String,String> headers)  throws InvalidHttpClientException, Exception{
    
    return execute(buildHttpPost(url,map,headers), "postAndResponse");
    
 }
  
 private HttpPost buildHttpPost(String url,Map<String,String> map,Map<String,String> headers){
	 logger.info("HttpClient httpPost [1] url = " + url);
   HttpPost httpPost = new HttpPost(url);    
   setHeader(headers,httpPost);
   if(map!=null){
  	 httpPost.setEntity(new UrlEncodedFormEntity(buildParamsList(map), Charset.forName("UTF-8")));
   }
   httpPost.getParams().setParameter("http.socket.timeout", conTimeOutMs);
   httpPost.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
   return httpPost;
 }

  /**
   * Post方法传送消息
   * 
   * @param url  连接的URL
   * @param body 请求内容
   * @return 服务器返回的信息
   * @throws Exception
   */
  @Override
	public String postBody(String url, String body) throws InvalidHttpClientException, Exception {      
      logger.info("HttpClient httpPost [1] url = " + url);
      HttpPost httpPost = new HttpPost(url);
      httpPost.addHeader("Accept-Encoding", "gzip,deflate,sdch");     
      if ((body != null) && !body.equals("")) {
          StringEntity reqEntity = new StringEntity(body,Charset.forName("UTF-8"));
         
          // 设置请求的数据
          httpPost.setEntity(reqEntity);
      }
      HttpResponse response = execute(httpPost, "postBody");
    
      String responseDate=EntityUtils.toString(response.getEntity());
      httpPost.abort();
      return responseDate;
  }

  /**
   * Post方法传送消息上传附件
   * 
   * @param url  连接的URL
   * @param queryString 请求参数串
   * @return 服务器返回的信息
   * @throws Exception
   */
  //@Override
	@Override
	public String httpPostWithFile(String url, Map<String,String> params, List<AttachItem> items) throws InvalidHttpClientException, Exception {
  	HttpPost httpPost = buildFileHttpPost(url,params,items);
		HttpResponse response = execute(httpPost, "httpPostWithFile");
    String responseData=EntityUtils.toString(response.getEntity());
    httpPost.abort();
    return responseData;
  	
  }
  
  @Override
	public Map<String,String> postFile(String url, Map<String,String> params, List<AttachItem> items) throws InvalidHttpClientException, Exception{
  	HttpPost httpPost = buildFileHttpPost(url,params,items);
  	Header [] header = execute(httpPost, "postFile").getAllHeaders();
  	httpPost.abort();
  	return revertHeaderToMap(header);

  }
  
  
  private HttpPost buildFileHttpPost(String url, Map<String,String> params, List<AttachItem> items) throws InvalidHttpClientException, Exception{
  	logger.info("HttpClient httpPostWithFile [1]  uri = " + url);
    MultipartEntity mpEntity = new MultipartEntity();
    HttpPost httpPost = new HttpPost(url);    
    StringBody stringBody;     
    FormBodyPart fbp;
    ByteArrayBody bab;   
    if(params!=null){
    	List<NameValuePair> queryParamList =buildParamsList(params);
      for (NameValuePair queryParam : queryParamList) {
          stringBody = new StringBody(queryParam.getValue(), Charset.forName("UTF-8"));
          fbp = new FormBodyPart(queryParam.getName(), stringBody);
          mpEntity.addPart(fbp);
      }
    }    
    for(AttachItem item :items){
    	bab = new ByteArrayBody(item.getContent(), item.getContentType(), item.getName());
      fbp = new FormBodyPart(item.getName(), bab);
      mpEntity.addPart(fbp);
    }
    httpPost.setEntity(mpEntity);
    return httpPost;
  }
  
  private HttpResponse execute(HttpUriRequest request, String method) throws InvalidHttpClientException, Exception{
  	HttpResponse response = null;
  	try{
  		response = client.execute(request);
  	}catch(SocketTimeoutException ste){
  		throw new InvalidHttpClientException(ste.getLocalizedMessage(),-1);
  	}catch(ConnectionPoolTimeoutException cpte){
  		throw new InvalidHttpClientException(cpte.getLocalizedMessage(),-1);
  	}
  	logger.info("HttpClient "+method+"  StatusLine = " + response.getStatusLine());
  	if((response.getStatusLine().getStatusCode() >= HttpStatus.SC_BAD_REQUEST)){
  		throw new InvalidHttpClientException("错误http请求状态",response.getStatusLine().getStatusCode());
  	}
  	return response;
  }
  
  private void setHeader(Map<String,String> headers,HttpUriRequest request){
  	if(headers!=null){
   	 Iterator<Entry<String,String>> it=headers.entrySet().iterator();
   	 while(it.hasNext()){
   		Map.Entry<String, String> entry = it.next();
   		request.addHeader(entry.getKey(), entry.getValue());
   	 }
    }
  }
  
  private void setHeader(List<HHeader> headers,HttpUriRequest request){
  	if(headers!=null){
   	  for(HHeader header:headers) {
				request.addHeader(header.getName(), header.getValue());
			}
   	 }    
  }
  
  private  List<NameValuePair> buildParamsList(Map<String,String> map){
  	List<NameValuePair> nvps=new ArrayList<NameValuePair>();
  		Iterator<Entry<String,String>> it=map.entrySet().iterator();
  		while(it.hasNext()){
  			Map.Entry<String, String> entry=it.next();
  			nvps.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
  		}
		return nvps;
  }
  

  
	
	 /**
   * 断开HttpClient的连接
   */
  public void shutdownConnection() {
      try {
          client.getConnectionManager().shutdown();
      } catch (Exception e) {
         logger.error(e.getLocalizedMessage(),e);
      }
  }
  
	public void setConTimeOutMs(int conTimeOutMs) {
		this.conTimeOutMs = conTimeOutMs;
	}

	public void setSoTimeOutMs(int soTimeOutMs) {
		this.soTimeOutMs = soTimeOutMs;
	}


	@Override
	public String postResponseHeader(String header, String url, Map<String, String> params, Map<String, String> headers)
			throws InvalidHttpClientException, Exception {
		Map<String, String> map = postResponseHeader(url,params,headers);
		return map.get(header);
	}

	@Override
	@SuppressWarnings("unchecked")	
	public Map<String, String> postResponseHeader(String url, Map<String, String> params, Map<String, String> headers)
			throws InvalidHttpClientException, Exception {
		HttpPost httpPost = buildHttpPost(url, params, headers);
		Header [] header = execute(httpPost, "postResponseHeader").getAllHeaders();
		httpPost.abort();
		return revertHeaderToMap(header);
	}
	
	private Map<String, String> revertHeaderToMap(Header [] headers){
		if((headers == null) || (headers.length<=0)){
			return Collections.EMPTY_MAP;
		}
		Map<String, String> map = new HashMap<String, String>(headers.length);
		for(Header header:headers){
			map.put(header.getName(), header.getValue());
		}
		return map;
	}

	@Override
	public HttpEntity execute(HttpUriRequest request) throws Exception {
		return execute(request, "execute").getEntity();
	}

	@Override
	public org.apache.http.client.HttpClient getOriginalClient() {
		return client;
	}


	
}
