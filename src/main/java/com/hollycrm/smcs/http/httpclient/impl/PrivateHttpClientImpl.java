package com.hollycrm.smcs.http.httpclient.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.entity.base.OfficalBlog;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.httpclient.AbstractInitHttpClient;
import com.hollycrm.smcs.http.impl.LoginClient;

public class PrivateHttpClientImpl extends AbstractInitHttpClient {

	private final Map<Long, IHttpClient> map = new ConcurrentHashMap<Long, IHttpClient>();
	
	@Override
	public IHttpClient obtainHttpClient(Long bloggerId) {
		if(!contains(bloggerId)){
			initHttpClient(bloggerId);
		}
		return map.get(bloggerId);	
		
	}



	@Override
	protected void addHttpClient(IHttpClient client) {
		map.put(client.getBloggerId(), client);
	}

	@Override
	public IHttpClient removeAndObtainHttpCient(Long bloggerId) {
		map.remove(bloggerId);
		initHttpClient(bloggerId);
		return obtainHttpClient(bloggerId);
	}

	@Override
	protected boolean contains(Long bloggerId) {
		return map.containsKey(bloggerId);
	}

	@Override
	public IHttpClient obtainHttpClient() {
		Set<Long> set = map.keySet();
		for(Long bloggerId:set){
			return map.get(bloggerId);
		}
		return null;
		
	}
	
	@Override
	public void addReLogin(Long bloggerId) {
		map.remove(bloggerId);
		addIntoReLogin(bloggerId);
	}

	@Override
	public void keepHttpClientSession() {
		Set<Long> set = map.keySet();
		String entity = null;
		for(Long bloggerId:set){
			try{
			entity = map.get(bloggerId).simpleHttpGet("http://weibo.com/fav");
			if(!LoginClient.isContainLogin(entity)){
				/*EarlyWarning ew = new EarlyWarning();
				ew.setTitle("httpclient session is expired");
				ew.setContent("bloggerId{"+bloggerId+"},的httpclient session已失效，系统将重新登录，请关注验证码");
				ew.setDetail("");
				ew.setCreatedAt(new Date());
				ew.setWarning(EarlyWarning.WAIT_WARN);
				ew.setWarnLevel(WarnLevel.WARN.getInfo());
				ew.setWarnStyle(0);
				ew.setWarnTarget(AppConfig.get(EmailUtils.MULTIPLE_EMAIL_ADDRESS));
				ApplicationContextHolder.getBean(EarlyWarningService.class).save(ew);*/
				logger.warn("bloggerId{"+bloggerId+"},的httpclient session已失效");
				addReLogin(bloggerId);
			}
			Thread.sleep(1000l);
			}catch(Exception e){
				logger.error(e.getLocalizedMessage(),e);
				addReLogin(bloggerId);
			}
			
		}
	}

	@Override
	protected List<OfficalBlog> findWaittingLoginOfficalBlog() {
		return officalBlogService.findPrivateOfficaBlog(Integer.parseInt(AppConfig.get("taskServer")), true);
	}



	@Override
	protected long getSleepTime() {
		return 100L;
	}
	
	

	

}
