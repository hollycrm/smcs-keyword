package com.hollycrm.smcs.monitor;

import com.hollycrm.smcs.monitor.bean.MonitorBean;
import com.hollycrm.smcs.monitor.impl.ReceiveMessageMonitor;

public class ReceiveMessageMonitorContainer {
	private static IReceiveMessageMonitor receiveMessageMonitor = new ReceiveMessageMonitor();
	
	public static void monitor(){
		receiveMessageMonitor.monitor();
	}
	
	public static void register(MonitorBean bean) {
		receiveMessageMonitor.register(bean);
	}

	

	public static void stop(Long uid) {
		receiveMessageMonitor.stop(uid);
	}
}
