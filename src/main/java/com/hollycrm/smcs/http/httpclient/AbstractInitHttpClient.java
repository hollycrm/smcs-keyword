package com.hollycrm.smcs.http.httpclient;

import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.email.util.EmailUtils;
import com.hollycrm.smcs.entity.base.OfficalBlog;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.InvalidPasswordException;
import com.hollycrm.smcs.http.impl.CommonHttpClient;
import com.hollycrm.smcs.http.impl.LoginClient;
import com.hollycrm.smcs.service.base.OfficalBlogService;


public abstract class AbstractInitHttpClient implements Runnable,IManageHttpClient {
	protected static final String EMAIL_TITLE = "登录新浪失败";
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected static final long SLEEP_TIME = 1800000L;
	protected final OfficalBlogService officalBlogService;
	
	private final  IHttpClient commonHttpClient = new CommonHttpClient();
	
	
	protected final Queue<Long> reLogin = new ConcurrentLinkedQueue<Long>();


	public AbstractInitHttpClient() {
		officalBlogService = ApplicationContextHolder.getBean(OfficalBlogService.class);
		Thread thread = new Thread(this);
		thread.setName("httpclient容器");
		thread.setDaemon(true);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	
	protected void init(){
		
			List<OfficalBlog> list = findWaittingLoginOfficalBlog();
			for(OfficalBlog officalBlog:list){
				initHttpClient(officalBlog);
				try {
					Thread.sleep(getSleepTime());
				} catch (InterruptedException e) {
					logger.error(e.toString(),e);
				}
			}
		
		
	}
	
	protected abstract long getSleepTime();
	
	protected abstract List<OfficalBlog> findWaittingLoginOfficalBlog();
	
	protected void initHttpClient(Long  bloggerId){
		OfficalBlog officalBlog = officalBlogService.findByBloggerIdAndMediaType(bloggerId, "w");
		if(officalBlog == null){
			logger.info(String.format("没有找到blogger{%s}账号信息",bloggerId));
			return;
		}
		initHttpClient(officalBlog);
	}
	
	protected void initHttpClient(OfficalBlog officalBlog){
		try{
			if(contains(officalBlog.getBloggerId())){
				return;
			}
			addHttpClient(new LoginClient(officalBlog.getUsername(),OfficalBlog.decode(officalBlog.getPassword()), 
					officalBlog.getGroupId(), officalBlog.getBloggerId(), officalBlog.getType()));
			
			officalBlog.setLogin(AppConfig.get("http.login.type"),true);
			logger.info("初始化用户{"+officalBlog.getUsername()+"} 的 httpclient成功");
		}catch(InvalidPasswordException e){
			logger.error("初始化用户{"+officalBlog.getUsername()+"} 的 httpclient失败,原因："+e.getLocalizedMessage(),e);
			officalBlogService.updateStatus(officalBlog);
			sendEmail(EMAIL_TITLE,"初始化用户{"+officalBlog.getUsername()+"} 的 httpclient失败,原因："+e.getLocalizedMessage());
			return;
		}catch(Exception e){
			logger.error("初始化用户{"+officalBlog.getUsername()+"} 的 httpclient失败,原因:"+e.getLocalizedMessage(),e);
			officalBlog.setLogin(AppConfig.get("http.login.type"),false);
			sendEmail(EMAIL_TITLE,"初始化用户{"+officalBlog.getUsername()+"} 的 httpclient失败,原因："+e.getLocalizedMessage());
			
		}
		officalBlogService.updateLogin(officalBlog);
	}
	
	public void sendEmail(String title, String content){
		try {
			EmailUtils.sendEmail(title,content);
		} catch (Exception e) {
			logger.error("发送邮件失败"+e.getLocalizedMessage());
		}
	}
	
	
	protected abstract void addHttpClient(IHttpClient client);
	
	protected abstract boolean contains(Long bloggerId);
	
	

	@Override
	public void run() {
		boolean isFirst = true;
		while(true){
			int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
			try{
				//第一次或都每天8-22运行
				if(((hour>=8)&&(hour<=21)) || isFirst){
					init();
					isFirst = false;
				}				
				reLogin();				
			}catch(Exception e){
				logger.error(e.toString(),e);
			}
		}
	}
	
	private void reLogin(){
		Long bloggerId = null;
		for(int i=0;i<60;i++) {
			while((bloggerId = reLogin.poll()) != null){
				initHttpClient(bloggerId);
			}
			try {
				Thread.sleep(10000l);
			} catch (InterruptedException e) {
				logger.error(e.toString(),e);
			}
		}
	}
	
	
	@Override
	public IHttpClient getCommonHttpClient(){
		return commonHttpClient;
	}
	
	/**
	 * 把bloggerId加入待初始化队列
	 * @param bloggerId
	 */
	protected void addIntoReLogin(Long bloggerId){
		if(!reLogin.contains(bloggerId)){
			reLogin.add(bloggerId);
			logger.info("把bloggerId{"+bloggerId+"}，加入到待初始化队列");
		}
		
	}
	


	
}
