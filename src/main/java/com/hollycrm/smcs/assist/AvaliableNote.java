package com.hollycrm.smcs.assist;

/**
 * 留言
 * @author fly
 *
 */
public class AvaliableNote extends AvaliablePm{

	public AvaliableNote( Long bloggerId, Long groupId, String mediaType, int type, 
			long interval){
		super( bloggerId, groupId, mediaType, type, interval);
	}
}
