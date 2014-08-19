package com.hollycrm.smcs.receive.parse.impl;

import java.util.Map;

import com.hollycrm.smcs.entity.receive.EventMessage;
import com.hollycrm.smcs.entity.receive.ReceiveMessage;
import com.hollycrm.smcs.receive.parse.AbsReceiveMessageParse;

/**
 * 
 * @author dingqj 
 *
 * 2013-10-11 下午5:47:30
 */
public class EventMessageParse extends AbsReceiveMessageParse{

	@Override
	protected ReceiveMessage parsePrivate(Map map) {
		EventMessage eventMessage = new EventMessage();
		Map data = getDataMap(map);
		eventMessage.setKey((String) data.get("key"));
		eventMessage.setSubType(data.get("subtype").toString());
		return eventMessage;
	}

}
