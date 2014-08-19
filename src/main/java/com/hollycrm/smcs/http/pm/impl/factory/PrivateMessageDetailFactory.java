package com.hollycrm.smcs.http.pm.impl.factory;

import com.hollycrm.smcs.entity.base.OfficalBlog;
import com.hollycrm.smcs.http.pm.IPrivateMessageDetail;
import com.hollycrm.smcs.http.pm.impl.NoVPrivateMessageDetail;
import com.hollycrm.smcs.http.pm.impl.VPrivateMessageDetail;

public abstract class PrivateMessageDetailFactory {
	
	public static IPrivateMessageDetail getprivateMessageDetail(Long bloggerId, Long groupId, int type){
		IPrivateMessageDetail privateMessage = null;
		if(type == OfficalBlog.HAVE_V){
			privateMessage = new VPrivateMessageDetail(bloggerId, groupId);
		}else if(type == OfficalBlog.NO_V){
			privateMessage = new NoVPrivateMessageDetail(bloggerId, groupId);
		}
		return privateMessage;
	}

}
