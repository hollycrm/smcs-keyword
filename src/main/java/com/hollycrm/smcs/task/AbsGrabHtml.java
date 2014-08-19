package com.hollycrm.smcs.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.http.httpclient.impl.PublicHttpClientContainer;
import com.hollycrm.smcs.service.fetch.FragmentService;

public abstract class AbsGrabHtml implements IGrabHtml {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected final FragmentService fragmentService;
	protected final IDispatcher dispatcher;
	
	public AbsGrabHtml(IDispatcher dispatcher){
		this.dispatcher = dispatcher;
		fragmentService =  ApplicationContextHolder.getBean(FragmentService.class);
	}

	/**
	 * 创建 fragment
	 * @param groupId
	 * @param key
	 * @param type
	 * @param sinceId
	 * @param maxId
	 * @param bloggerId
	 * @param uid
	 * @param officalBlogId
	 * @param conditionId
	 * @param exclusiveKey
	 */
	public void createAndSaveFragment(Long groupId, String key, String type, Long sinceId, Long maxId, Long bloggerId,
			Long uid, Long officalBlogId, Long conditionId,String exclusiveKey) {
		createAndSaveFragment(new Fragment(groupId,key,type,sinceId,maxId,bloggerId,uid,officalBlogId, conditionId, exclusiveKey,0, null,null));
	}
	
	public void createAndSaveFragment(Fragment fragment){
		this.fragmentService.save(fragment);
	}
	
	@Override
	public IHttpClient obtainHttpClient() {
		return PublicHttpClientContainer.obtainHttpClient();
	}
	
}
