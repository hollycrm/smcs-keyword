package com.hollycrm.smcs.receive.parse;

import java.util.Map;

import com.hollycrm.smcs.entity.receive.ReceiveMessage;

/**
 * 
 * @author dingqj 
 *
 * 2013-10-11 下午5:12:58
 */
public interface IReceiveMessageParse {
	
	/**
	 * 接收消息解析
	 * @param map
	 * @return receiveMessage
	 */
	ReceiveMessage parse(Map map);

}
