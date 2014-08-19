package com.hollycrm.smcs.http31;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.HttpConnectionParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.http.AttachItem;
import com.hollycrm.smcs.http.ICommonHttpClient;
import com.hollycrm.smcs.http.InvalidHttpClientException;
import com.hollycrm.smcs.http.SetCookie;
import com.hollycrm.smcs.http.StoreCookie;
import com.hollycrm.smcs.http.util.ByteArrayPart;
import com.hollycrm.smcs.util.JsonUtil;

public class HttpClient implements ICommonHttpClient,Serializable{
	private final Logger logger = LoggerFactory.getLogger(HttpClient.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -4329236921453073849L;
	
	
	private final  org.apache.commons.httpclient.HttpClient client;	
	public static final Integer MAX_TIME_OUT = 10000;  
	public static final Integer MAX_IDLE_TIME_OUT = 100000;  
	public static final Integer MAX_CONN = 200;

	
	public HttpClient(){
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.closeIdleConnections(MAX_IDLE_TIME_OUT);
		connectionManager.getParams().setParameter("http.connection-manager.max-total", MAX_CONN); 
		client = new org.apache.commons.httpclient.HttpClient(connectionManager);
		DefaultHttpParams.getDefaultParams().setBooleanParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);
		client.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
		client.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, MAX_TIME_OUT);
	}
	
	@Override
	public String simpleHttpGet(String url) throws Exception {
		return simpleHttpGet(url,null);
	}
	
	

	@Override
	public String simpleHttpGet(String url, Map<String, String> headers) throws Exception {
		logger.info("HttpClient simpleHttpGet  url = " + url);
		GetMethod getMethod = new GetMethod(url);
		setHeaders(headers, getMethod);
		return executeMethod(getMethod);
	}

	@Override
	public byte[] httpGetWithByte(String url) throws Exception {
		logger.info("HttpClient httpGetWithByte  url = " + url);
		GetMethod getMethod = new GetMethod(url);
		return executeAndResponseByte(getMethod);
	}

	@Override
	public String post(String url, Map<String, String> map, Map<String, String> headers) throws Exception {
		logger.info("HttpClient post  url = " + url);
		PostMethod postMethod = new PostMethod(url);
		setHeaders(headers, postMethod);
		setParameters(map, postMethod);		
		return executeMethod(postMethod);
	}

	@Override
	public String postBody(String url, String body) throws Exception {
		return null;
	}

	@Override
	public String httpPostWithFile(String url, Map<String, String> params, List<AttachItem> items) throws Exception {
		return executeMethod(buildPostFile(url,params,items));
		
	}
	
	private void setHeaders(Map<String, String> headers, HttpMethod method){
		if(headers != null){
			 Iterator<Entry<String,String>> it=headers.entrySet().iterator();
	   	 while(it.hasNext()){
	   		Map.Entry<String, String> entry = it.next();
	   		method.setRequestHeader(entry.getKey(), entry.getValue());	   		
	   	 }
		}
	}
	
	private String executeMethod(HttpMethod method) throws Exception{	
		 try{
			 client.executeMethod(method);	
			 logger.info("HttpClient execute StatusLine = " + method.getStatusLine());
				if (method.getStatusCode() == HttpStatus.SC_OK) {					
					return method.getResponseBodyAsString();
				} else {
					method.abort();//马上断开连接  	
					throw new Exception("请求异常,错误的sttautcode");
	      } 	
		 }finally { 
				method.releaseConnection();  
		 }
		 
	}
	
	private byte[] executeAndResponseByte(HttpMethod method) throws Exception{
		try{
			 client.executeMethod(method);	
			 logger.info("HttpClient execute StatusLine = " + method.getStatusLine());
				if (method.getStatusCode() == HttpStatus.SC_OK) {					
					return method.getResponseBody();
				} else {
					method.abort();//马上断开连接  	
					throw new Exception("请求异常,错误的sttautcode");
	      } 	
		 }finally { 
				method.releaseConnection();  
		 }
	}
 	
	private Header[] executeAndResponseHeader(HttpMethod method) throws Exception{
		try{
			client.executeMethod(method);	
			 logger.info("HttpClient execute StatusLine = " + method.getStatusLine());
			 return method.getResponseHeaders();
		}finally{
			method.releaseConnection();  
		}
	}
	
	private void setParameters(Map<String, String> map, PostMethod method){
		if(map == null){
			return;
		}
		Iterator<Entry<String,String>> it=map.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, String> entry=it.next();
			method.addParameter(entry.getKey(), entry.getValue());
		}
		
	}

	@Override
	public String downloadPic(String url, String parentFile, String suffix, String prefix) throws Exception {
		
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

	@Override
	public String postResponseHeader(String header, String url, Map<String, String> params, Map<String, String> headers)
			throws Exception {
		Map<String, String> map =postResponseHeader(url, params, headers);
		return map.get(header);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> postResponseHeader(String url, Map<String, String> params, Map<String, String> headers)
			throws Exception {
		logger.info("HttpClient postResponseHeader  url = " + url);
		PostMethod postMethod = new PostMethod(url);
		setHeaders(headers, postMethod);
		setParameters(params, postMethod);
		Header[] responseHeaders = executeAndResponseHeader(postMethod);
		
		return revertHeaderToMap(responseHeaders);
	}

	@Override
	public Map<String, String> postFile(String url, Map<String, String> params, List<AttachItem> items)
			throws Exception {
		
		return revertHeaderToMap(executeAndResponseHeader(buildPostFile(url,params,items)));
	}
	
	private PostMethod buildPostFile(String url, Map<String, String> params, List<AttachItem> items) throws Exception{
		logger.info("HttpClient postFile  url = " + url);
	
		PostMethod  filePost = new PostMethod (url);	
		//setParameters(params, filePost);		
		ByteArrayPart filePart = null;
		int i =0;
		Part [] parts = new Part[items.size()+params.size()];
		for(AttachItem item:items){
			filePart = new ByteArrayPart(item.getContent(), item.getName(),
					item.getContentType());	
			parts[i++] = filePart;
		}
		Iterator<Entry<String,String>> it=params.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, String> entry=it.next();
			parts[i++] = new StringPart(entry.getKey(),entry.getValue());
		}
		
		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));					
	  filePost.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,  
    new DefaultHttpMethodRetryHandler()); 	 
	  return filePost;
	}

	
	private String buildUrl(String url ,Map<String,String> params){
		StringBuilder sb = new StringBuilder(200);
		if((params == null)||params.isEmpty()){
			return url;
		}
		Iterator<Entry<String,String>> it=params.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, String> entry=it.next();
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			sb.append("&");
		}
		String entry = sb.toString();
		if(url.indexOf("?")!=-1){
			return url+"&"+entry.substring(0,entry.length()-1);
		}else{
			return url +"?"+entry.substring(0,entry.length()-1);
		}
		
		
	}
	
	private Map<String, String> revertHeaderToMap(Header[] headers){
		if(headers == null){
			return Collections.EMPTY_MAP;
		}
		Map<String, String> map = new HashMap<String,String>(headers.length);
		for(Header header:headers){
			map.put(header.getName(), header.getValue());
		}
		return map;
	}

	@Override
	public HttpEntity execute(HttpUriRequest request) throws Exception {
		return null;
	}

	@Override
	public org.apache.http.client.HttpClient getOriginalClient() {
		return null;
	}

	@Override
	public String simpleHttpGet(String url, Map<String, String> headers, SetCookie setCookie, StoreCookie storeCookie)
			throws InvalidHttpClientException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
