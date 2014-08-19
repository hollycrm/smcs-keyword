package com.hollycrm.smcs.http.httpclient.impl;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.entity.base.OfficalBlog;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.httpclient.AbstractInitHttpClient;

public class PublicHttpClientImpl extends AbstractInitHttpClient{
	
	private final List<Long> inited = new CopyOnWriteArrayList<Long>();
	
	/**
	 * 可用httpclient
	 */
	private final Queue<Long> avaliable = new ConcurrentLinkedQueue<Long>();
	
	/**
	 * 可用的httpclient
	 */
	private final Map<Long, IHttpClient> map = new ConcurrentHashMap<Long, IHttpClient>();
	
	/**
	 * 有验证码的httpclient
	 */
	private final Queue<Long> invalid = new ConcurrentLinkedQueue<Long>();
	
	
	
	private volatile  boolean isObtainInvalidClient = false;
	
	private void invalidToAvaliable(){
		Long bloggerId = null;
		while((bloggerId =invalid.poll()) != null){
			avaliable.add(bloggerId);
		}
	}
	
	@Override
	public IHttpClient obtainHttpClient() {
		/**使用无效的Httpclient**/
		if(isObtainInvalidClient){
			invalidToAvaliable();
		}
		
		if(avaliable.isEmpty()){
			logger.info("public httpclient queue is empty,init is run ");
			invalidToAvaliable();
		}
		Long bloggerId = avaliable.poll();	
		if(bloggerId == null){
			logger.info("there is no avaliable httpclient");
			return null;
		
		}
		avaliable.add(bloggerId);
		return map.get(bloggerId);
	}



	@Override
	protected void addHttpClient(IHttpClient client) {
		inited.add(client.getBloggerId());
		putHttpClient(client.getBloggerId(),client);
	}
	
	private void putHttpClient(Long bloggerId,IHttpClient client){
		avaliable.add(bloggerId);
		map.put(bloggerId, client);
	}

	@Override
	protected boolean contains(Long bloggerId) {
		return isInit(bloggerId);
	}

	
	private  void remove(Long bloggerId){
		inited.remove(bloggerId);
		map.remove(bloggerId);
		avaliable.remove(bloggerId);
		invalid.remove(bloggerId);
	}
	
	private synchronized boolean isInit(Long bloggerId){
		return inited.contains(bloggerId);
	}

	@Override
	public IHttpClient obtainHttpClient(Long bloggerId) {
		return null;
	}

	@Override
	public IHttpClient removeAndObtainHttpCient(Long bloggerId) {
		addReLogin(bloggerId);
		return obtainHttpClient();
	}

	

	@Override
	public void addReLogin(Long bloggerId) {
		remove(bloggerId);				
		addIntoReLogin(bloggerId);
	}

	@Override
	public void keepHttpClientSession() {		
	}

	@Override
	protected List<OfficalBlog> findWaittingLoginOfficalBlog() {		
		return officalBlogService.findPublicOfficalBlog(Integer.parseInt(AppConfig.get("taskServer")));
	}

	@Override
	protected long getSleepTime() {
		return 5000L;
	}
}
