package com.hollycrm.smcs.monitor.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.hollycrm.smcs.monitor.IReceiveMessageMonitor;
import com.hollycrm.smcs.monitor.bean.MonitorBean;

/**
 * 
 * @author dingqj 
 *
 * 2013-10-11 上午10:34:17
 */
public class ReceiveMessageMonitor implements IReceiveMessageMonitor, Runnable{

	private final  Map<Long, MonitorBean> map = new ConcurrentHashMap<Long, MonitorBean>();
	
	/**
	 * 启动监控线程
	 */
	
	@Override
	public void monitor(){
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.setName("receiveMessageMonitor");
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	
	@Override
	public void run() {
		while(true){
			Set<Long> it = map.keySet();
			for(Long uid:it){
				if(map.get(uid).isShouldColse()){
					map.get(uid).abort();
					map.remove(uid);
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void register(MonitorBean bean) {
		map.put(bean.getUid(), bean);
	}

	

	@Override
	public void stop(Long uid) {
		MonitorBean bean = map.get(uid);
		if(bean != null){			
			bean.abort();
			map.remove(uid);
		}
	}

}
