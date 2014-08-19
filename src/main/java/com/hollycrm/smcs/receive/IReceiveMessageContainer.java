package com.hollycrm.smcs.receive;

import com.hollycrm.smcs.entity.receive.ReceiveMessage;

public interface IReceiveMessageContainer {

	/**
	 * 向容器中放一个receiveMessage;
	 * @param receiveMessage
	 */
	void push(ReceiveMessage receiveMessage) throws Exception;
	
	/**
	 * 从容器中取receiveMessage
	 * @return
	 */
	ReceiveMessage pull() throws Exception;
}
