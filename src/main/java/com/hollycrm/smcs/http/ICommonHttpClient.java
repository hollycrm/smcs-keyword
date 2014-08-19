package com.hollycrm.smcs.http;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * 定义http默认的几种请求方法
 * 
 * @author fly
 *
 */
public interface ICommonHttpClient {
	
	/**
	 *  /**
   * Get方法传送消息 ，无请求头
   * 
   * @param url  请求url 
   * @return 服务器返回的信息
	 * @throws InvalidHttpClientException
	 * @throws Exception
	 */
	String simpleHttpGet(String url) throws InvalidHttpClientException, Exception;
	
	/**
	 * Get方法传送消息 ，带请求头
	 * @param url 请求url
	 * @param headers 请求头
	 * @return 服务器返回的信息
	 * @throws InvalidHttpClientException
	 * @throws Exception
	 */
	String simpleHttpGet(String url, Map<String, String> headers) throws InvalidHttpClientException, Exception;
	
	
	String simpleHttpGet(String url, Map<String, String> headers, SetCookie setCookie, StoreCookie storeCookie) throws InvalidHttpClientException, Exception;
	
	/**
	 * Get方法传送消息，返回byte[]
	 * 
	 * @param url 请求url
	 * @return 返回byte[]
	 * @throws InvalidHttpClientException
	 * @throws Exception
	 */
	byte[] httpGetWithByte(String url) throws InvalidHttpClientException, Exception;
	
	/**
   * 服务器返回的信息
   * 
   * @param url
   * @param map
   * @param headers
   * @return 服务器返回的信息
   * @throws Exception
   */
  String post(String url,Map<String,String> map,Map<String,String> headers) throws InvalidHttpClientException,  Exception;
  
  /**
   * Post方法传送消息
   * 
   * @param url  连接的URL
   * @param body 请求内容
   * @return 服务器返回的信息
   * @throws Exception
   */
  String postBody(String url, String body) throws InvalidHttpClientException, Exception;
  
  /**
   * Post方法传送消息上传附件
   * 
   * @param url  连接的URL
   * @param queryString 请求参数串
   * @return 服务器返回的信息
   * @throws Exception
   */
  String httpPostWithFile(String url, Map<String,String> params, List<AttachItem> items) throws InvalidHttpClientException, Exception;

  /**
   *  下载图片，并上传到图片服务器
   * @param url 下载图片地址
   * @param parentFile 上传到图片服务器文件夹
   * @param suffix 图片后缀名
   * @param prefix 图片前缀名
   * @return 图片名称
   * @throws InvalidHttpClientException
   * @throws Exception
   */
  String downloadPic(String url, String parentFile, String suffix, String prefix) throws InvalidHttpClientException, Exception;
  
  /**
   * post 请求，返回某个header
   * 
   * @param header header key
   * @param url 请求url
   * @param params 请求参数
   * @param headers 请求头
   * @return header
   * @throws InvalidHttpClientException
   * @throws Exception
   */
  String postResponseHeader(String header, String url, Map<String, String> params, Map<String, String> headers) throws InvalidHttpClientException, Exception;
  
  /**
   * post 请求 ，返回所有header
   * @param url
   * @param params
   * @param headers
   * @return
   * @throws InvalidHttpClientException
   * @throws Exception
   */
  Map<String, String> postResponseHeader(String url, Map<String, String> params, Map<String, String> headers ) throws InvalidHttpClientException, Exception;
  
  /**
   * post请求，带附件
   * 
   * @param url 请求url
   * @param params 请求参数
   * @param items 附件
   * @return
   * @throws InvalidHttpClientException
   * @throws Exception
   */
  Map<String,String> postFile(String url, Map<String, String> params, List<AttachItem> items) throws InvalidHttpClientException, Exception;
  
  
  /**
   * 执行一个http请求
   * @param request
   * @return HttpEntity
   * @throws Exception
   */
  HttpEntity execute(HttpUriRequest request) throws Exception;
  
  HttpClient getOriginalClient();
  
  
}
