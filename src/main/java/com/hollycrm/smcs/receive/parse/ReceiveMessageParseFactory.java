package com.hollycrm.smcs.receive.parse;

import com.hollycrm.smcs.entity.receive.ReceiveMessage;
import com.hollycrm.smcs.receive.parse.impl.EventMessageParse;
import com.hollycrm.smcs.receive.parse.impl.ImageMessageParse;
import com.hollycrm.smcs.receive.parse.impl.MentionMessageParse;
import com.hollycrm.smcs.receive.parse.impl.PositionMessageParse;
import com.hollycrm.smcs.receive.parse.impl.TextMessageParse;

/**
 * 
 * @author dingqj 
 *
 * 2013-10-12 上午10:47:49
 */
public abstract class ReceiveMessageParseFactory {

	public static IReceiveMessageParse getReceiveMessageParse(String type){
		IReceiveMessageParse receiveMessageParse = null;
		if(type.equals(ReceiveMessage.EVENT)){
			receiveMessageParse = new EventMessageParse();
		} else if(type.equals(ReceiveMessage.TEXT)){
			receiveMessageParse = new TextMessageParse();
		} else if(type.equals(ReceiveMessage.IMAGE)){
			receiveMessageParse = new ImageMessageParse();
		} else if(type.equals(ReceiveMessage.POSITION)) {
			receiveMessageParse = new PositionMessageParse();
		} else if(type.equals(ReceiveMessage.MENTION)){
			receiveMessageParse = new MentionMessageParse();
		} else {
			throw new IllegalArgumentException("没有相应的处理器type="+type);
		}
		return receiveMessageParse;
	}
}
