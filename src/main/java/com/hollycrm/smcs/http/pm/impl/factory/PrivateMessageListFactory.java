package com.hollycrm.smcs.http.pm.impl.factory;

import com.hollycrm.smcs.entity.base.OfficalBlog;
import com.hollycrm.smcs.http.pm.IPrivateMessageList;
import com.hollycrm.smcs.http.pm.impl.NoVPrivateMessageList;
import com.hollycrm.smcs.http.pm.impl.VPrivateMessageList;

public abstract class PrivateMessageListFactory {
	
	public static IPrivateMessageList getPrivateMessageList(Long bloggerId, Long groupId, int type){
		IPrivateMessageList privateMessage = null;
		if(type == OfficalBlog.HAVE_V){
			privateMessage = new VPrivateMessageList(bloggerId, groupId);
		}else if(type == OfficalBlog.NO_V){
			privateMessage = new NoVPrivateMessageList(bloggerId, groupId);
		}
		return privateMessage;
	}

}
