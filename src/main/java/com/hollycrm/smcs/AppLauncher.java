package com.hollycrm.smcs;

import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.http.httpclient.impl.PublicHttpClientContainer;
import com.hollycrm.smcs.remote.IKeywordService;
import com.hollycrm.smcs.remote.impl.KeywordServiceImpl;
import com.hollycrm.smcs.task.GrabFragmentDispatcher;
import com.hollycrm.smcs.task.board.impl.GrabBoardFragmentDispatcher;
import com.hollycrm.smcs.task.board.impl.GrabBoardMessageDispatcher;
import com.hollycrm.smcs.task.board.impl.PollingBoardScheduler;
import com.hollycrm.smcs.task.board.impl.TransformBoardScheduler;
import com.hollycrm.smcs.task.keyword.impl.GrabKeywordDispatcher;
import com.hollycrm.smcs.task.keyword.impl.GrabKeywordFragmentDispatcher;
import com.hollycrm.smcs.task.keyword.impl.PollingKeywordScheduler;
import com.hollycrm.smcs.task.keyword.impl.TransformKeywordDispatcher;
import com.hollycrm.smcs.task.keyword.impl.TransformKeywordScheduler;

public class AppLauncher {
	private static final Logger logger = LoggerFactory.getLogger(AppLauncher.class);

	private GrabKeywordDispatcher dispatcher;

	private PollingKeywordScheduler keywordScheduler;
	
	private GrabFragmentDispatcher grabFragmentDispatcher;
	
	private TransformKeywordScheduler transformKeywordScheduler;
	
	private TransformKeywordDispatcher transformKeywordDispatcher;
	
	private GrabBoardMessageDispatcher boardDispatcher;

	private PollingBoardScheduler boardScheduler;

	private GrabBoardFragmentDispatcher boardFragmentDispatcher;
	
	private TransformBoardScheduler boardTransformScheduler;
	
	private static final String CONFIG_NAME = "smcs-keyword.properties";
	
	private IKeywordService impl;
	


	public static void main(String[] args) throws Exception {
		AppLauncher app = new AppLauncher();
		app.init();
		/*app.startJmxService();
		app.startRmiService();*/
		
		app.startRmi();
		app.keywordScheduler.polling();
		app.dispatcher.polling();
		//app.grabFragmentDispatcher.polling();		
		app.transformKeywordScheduler.polling();
		app.transformKeywordDispatcher.polling();
		
		//app.boardScheduler.polling();
		//app.boardDispatcher.polling();			
		//app.boardFragmentDispatcher.polling();
		//app.boardTransformScheduler.polling();
		logger.info("抓取关键字服务启动成功");
		
		
	}

	public void init() throws Exception {
		AppConfig.init(CONFIG_NAME);
		ApplicationContextHolder.init();
		impl = new KeywordServiceImpl();
		PublicHttpClientContainer.init();
		logger.info("等待初始化一部分httpclient，30秒后开始启动服务");
		Thread.sleep(30000l);
		dispatcher = new GrabKeywordDispatcher();
		grabFragmentDispatcher = new GrabKeywordFragmentDispatcher();
		keywordScheduler = new PollingKeywordScheduler(dispatcher);
		transformKeywordDispatcher = new TransformKeywordDispatcher();
		transformKeywordScheduler = new TransformKeywordScheduler(transformKeywordDispatcher);
		
		boardDispatcher = new GrabBoardMessageDispatcher();
		boardScheduler = new PollingBoardScheduler(boardDispatcher);
		boardFragmentDispatcher = new GrabBoardFragmentDispatcher();
		boardTransformScheduler = new TransformBoardScheduler();

	}
	
	 public void startRmi() throws Exception{
   	 try{
   		LocateRegistry.getRegistry(1099);
   		bindRmi();
   	 }catch(ConnectException ce){
   		LocateRegistry.createRegistry(1099);
   		bindRmi();
   	 }catch(Exception e){
   		 logger.error("smcs-keyword rmi启动失败");
   		 throw e;
   	 }
   	 
    }
	 
	 public void bindRmi() throws Exception {
		 String srvRmiUrl =AppConfig.get("SrvRmiUrl") + AppConfig.get("taskServer");
  	 Naming.rebind(srvRmiUrl, impl);
  	 logger.info("smcs-keyword rmi启动成功");
	 }
	
	
	
	 public void startJmxService() throws Exception {
 		try {
 			java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry
 					.getRegistry(AppConfig.get("RmiIP"));
 			MBeanServer ms = MBeanServerFactory.createMBeanServer();
 			ObjectName agentName = new ObjectName(
 					"Agent:name=KeywordServiceImpl");
 			ms.registerMBean(impl, agentName);

 			String jmxRmiUrl =AppConfig.get("JmxRmiUrl")+AppConfig.get("taskServer");

 			try {

 				registry.unbind(jmxRmiUrl);
 				logger.debug("JMX服务RMI绑定缓存清空成功，JmxRmiUrl：" + jmxRmiUrl);
 			} catch (Exception ex) {
 				logger.debug("JMX服务RMI还未绑定：" + jmxRmiUrl);
 			}

 			JMXServiceURL url = new JMXServiceURL(
 					"service:jmx:rmi:///jndi/rmi://"+AppConfig.get("RmiIP")+":1099/" + jmxRmiUrl);
 			JMXConnectorServer cs = JMXConnectorServerFactory
 					.newJMXConnectorServer(url, null, ms);

 			cs.start();

 			logger.info("JMX启动成功！Url：" + url);
 		} catch (Exception ex) {
 			String errorMsg = "JMX启动失败，详细原因：" + ex.getLocalizedMessage();
 			logger.error(errorMsg, ex);
 			throw new Exception(ex.getLocalizedMessage(), ex);
 		}
 	}
 	
 	
 	public void startRmiService() throws Exception {
 		try {
 		java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry
 		.getRegistry(AppConfig.get("RmiIP"));
 		String srvRmiUrl =AppConfig.get("SrvRmiUrl")+AppConfig.get("taskServer");
 		IKeywordService stub = (IKeywordService) UnicastRemoteObject
 				.exportObject(impl, 0);
 		registry.rebind(srvRmiUrl, stub); // 绑定RMI名称 进行发布
 		logger.info("RMI启动成功！SrvRmiUrl：" + srvRmiUrl);
 		}
 		catch (Exception ex) {
 			String errorMsg = "RMI启动失败，详细原因：" + ex.getLocalizedMessage();
 			logger.error(errorMsg, ex);
 			throw new Exception(ex.getLocalizedMessage(), ex);
 		}		
 	}

	
}
