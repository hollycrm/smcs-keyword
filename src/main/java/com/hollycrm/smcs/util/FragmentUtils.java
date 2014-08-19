package com.hollycrm.smcs.util;

import com.hollycrm.smcs.entity.fetch.Fragment;

public abstract class FragmentUtils {

	public static Fragment createdKeywordFragment(Long maxId, Long sinceId, String key, String type) {
		
		
		return createdBaseFragment(maxId, sinceId, key, type);
	}
	
	private static Fragment createdBaseFragment(Long maxId, Long sinceId, String key, String type){
		Fragment fragment = new Fragment();		
		fragment.setKey(key);
		fragment.setType(type);
		fragment.setSinceId(sinceId);
		fragment.setMaxId(maxId);	
		return fragment;
	}

}
