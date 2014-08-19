package com.hollycrm.smcs.receive.parse.impl;

import java.util.Map;

import com.hollycrm.smcs.entity.receive.MentionMessage;
import com.hollycrm.smcs.entity.receive.ReceiveMessage;
import com.hollycrm.smcs.receive.parse.AbsReceiveMessageParse;

/**
 * 
 * @author dingqj 
 *
 * 2013-10-11 下午6:13:04
 */
public class MentionMessageParse extends AbsReceiveMessageParse {

	@Override
	protected ReceiveMessage parsePrivate(Map map) {
		MentionMessage mentionMessage = new MentionMessage();
		Map data = getDataMap(map);
		mentionMessage.setSubType(data.get("subtype").toString());
		mentionMessage.setKey(data.get("key").toString());
		return mentionMessage;
	}

}
