package com.hollycrm.smcs.monitor.bean;

import org.apache.http.client.methods.HttpGet;

/**
 * 监控类
 * @author dingqj
 *
 */
public class MonitorBean {
	
	private Long uid;

	/**http请求**/
	private HttpGet httpGet;
	
	/**抓取时间**/
	private long fetchTime;
	
	/**可供抓取时长**/
	private long fetchDurationTime;

	public MonitorBean(){
		
	}
	
	public MonitorBean(Long uid, HttpGet httpGet, long fetchTime, long fetchDurationTime){
		this.uid = uid;
		this.httpGet = httpGet;
		this.fetchTime = fetchTime;
		this.fetchDurationTime = fetchDurationTime;
	}	
	
	
	/**
	 * 检查请求是否应该关闭
	 */
	public boolean isShouldColse(){
		if((System.currentTimeMillis() - fetchTime) >= fetchDurationTime){
			return true;
		}
		return false;
	}
	
	/**
	 * 关闭
	 */
	public void abort(){
		httpGet.abort();
	}

	public Long getUid() {
		return uid;
	}
	
	
}
