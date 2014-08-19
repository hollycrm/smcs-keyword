package com.hollycrm.smcs.monitor;

import com.hollycrm.smcs.monitor.bean.MonitorBean;

/**
 * 监控接收消息连接时长
 * 
 * @author dingqj
 *
 */
public interface IReceiveMessageMonitor {

	/**
	 * 向监控程序注册待监控请求
	 * @param bean 监控bean 包含
	 * @see MonitorBean
	 */
	void register(MonitorBean bean);
	
	/**
	 * 停止连接
	 * @param uid
	 */
	void stop(Long uid);
	
	/**
	 * 开始监控
	 */
	void monitor();
	
}
