package com.hollycrm.smcs.task;

import com.hollycrm.smcs.http.IHttpClient;

public interface IGrabHtml {
	
	/**
	 * 判断抓取的mid在不在所要抓取的范围内
	 * @param mid
	 * @return
	 */
	 boolean isInRange(Long mid);
	 
	 /**
	  * 
	  * @param mid
	  * @return
	  */
	 boolean isInValidRange(Long mid);
	
	 /**
	  * 初始化sinceId
	  * @return
	  */
	 void initSinceId();
	 
	 /**
	  * 判断是否是第一次
	  * @return
	  */
	 boolean isFirst();
	
	 /**
	  * 结束抓取 保存最大的mid
	  */
	 void endGrab(Long firstMid);
	
	 /**
	  * 抓取出错处理,保存段，保存最大的mid
	  */
	 void errorDeal(Long currentId , Long firstMid, Long uid);
	
	 
	 
	 /**
	  * 退出程序
	  */
	 void exit();
	 
	 /**
	  * 计算运行时间
	  * @param count
	  */
	 void countRuntime(int count);
	 
	 /**
	  * 获取httpclient
	  * 
	  * @return
	  */
	 IHttpClient obtainHttpClient();
	 
	 
}
