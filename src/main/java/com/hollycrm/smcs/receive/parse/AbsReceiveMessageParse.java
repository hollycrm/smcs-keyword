package com.hollycrm.smcs.receive.parse;

import java.util.Date;
import java.util.Map;

import com.hollycrm.smcs.entity.receive.ReceiveMessage;
import com.hollycrm.smcs.util.DateUtil;
import com.hollycrm.smcs.util.JsonUtil;

/**
 * 
 * @author dingqj 
 *
 * 2013-10-11 下午5:13:05
 */
public abstract class AbsReceiveMessageParse implements IReceiveMessageParse{
	
	
	

	@Override
	public ReceiveMessage parse(Map map) {
		ReceiveMessage receiveMessage = parsePrivate(map);
		parseNormal(map, receiveMessage);
		return receiveMessage;
	}
	
	/**
	 * 读取map里的私有字段
	 * @param map
	 * @return
	 */
	protected abstract ReceiveMessage parsePrivate(Map map);
	
	/**
	 * 读取map里的共有字段
	 * @param map
	 * @param receiveMessage
	 */
	private void parseNormal(Map map, ReceiveMessage receiveMessage){
		receiveMessage.setRecipientId(Long.parseLong(map.get("receiver_id").toString()));
		receiveMessage.setReceiveId(Long.parseLong(map.get("id").toString()));
		receiveMessage.setSenderId(Long.parseLong(map.get("sender_id").toString()));
		receiveMessage.setCreatedAt(DateUtil.formatDate(map.get("created_at").toString()));
		receiveMessage.setReceiveTime(new Date());
		receiveMessage.setText(map.get("text").toString());
		receiveMessage.setData(map.get("data").toString());
	}
	
	/**
	 * 把map 里的data字段转化为map
	 * @param map
	 * @return
	 */
	protected Map getDataMap(Map map){
		return JsonUtil.getMap4Json(map.get("data").toString());
	}
	


}
