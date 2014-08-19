package com.hollycrm.smcs.receive.parse.impl;

import java.util.Map;

import com.hollycrm.smcs.entity.receive.PositionMessage;
import com.hollycrm.smcs.entity.receive.ReceiveMessage;
import com.hollycrm.smcs.receive.parse.AbsReceiveMessageParse;

/**
 * 
 * @author dingqj 
 *
 * 2013-10-11 下午6:06:30
 */
public class PositionMessageParse extends AbsReceiveMessageParse {

	@Override
	protected ReceiveMessage parsePrivate(Map map) {
		PositionMessage positionMessage = new PositionMessage();
		Map data = getDataMap(map);
		positionMessage.setLatitude(Double.parseDouble(data.get("latitude").toString()));
		positionMessage.setLongitude(Double.parseDouble(data.get("longitude").toString()));		
		return positionMessage;
	}

}
