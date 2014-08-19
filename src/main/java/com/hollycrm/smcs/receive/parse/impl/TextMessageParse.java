package com.hollycrm.smcs.receive.parse.impl;

import java.util.Map;

import com.hollycrm.smcs.entity.receive.ReceiveMessage;
import com.hollycrm.smcs.entity.receive.TextMessage;
import com.hollycrm.smcs.receive.parse.AbsReceiveMessageParse;

/**
 * 
 * @author dingqj 
 *
 * 2013-10-11 下午6:00:36
 */
public class TextMessageParse extends AbsReceiveMessageParse{

	@Override
	protected ReceiveMessage parsePrivate(Map map) {
		return new TextMessage();
	}

}
