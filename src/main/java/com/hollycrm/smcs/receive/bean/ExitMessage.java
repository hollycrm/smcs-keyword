package com.hollycrm.smcs.receive.bean;

import com.hollycrm.smcs.entity.message.Message;
import com.hollycrm.smcs.entity.receive.ReceiveMessage;

/**
 * 用于ReceiveMessageBlockArray退出标志
 *
 * @author dingqj 
 * @since 
 * 2013-11-7 下午2:40:19
 */
public class ExitMessage extends ReceiveMessage{

	private static final long serialVersionUID = 8072273997830667096L;

	@Override
	public String getItemType() {
		return null;
	}

	@Override
	protected Message newMessage() {
		return null;
	}

}
