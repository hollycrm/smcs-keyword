package com.hollycrm.smcs.task.impl;

import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.task.AbsGrabHtml;
import com.hollycrm.smcs.task.IDispatcher;

public  class GrabFragmentHtml extends AbsGrabHtml{

	protected final Fragment fragment;
	
	
	public GrabFragmentHtml(Fragment fragment , IDispatcher dispatcher){
		super(dispatcher);
		this.fragment = fragment;
		
	}
	
	@Override
	public boolean isInRange(Long mid) {
		return (fragment.getMaxId() > mid) && (fragment.getSinceId() < mid);
		
	}

	@Override
	public void initSinceId() {
		
	}

	@Override
	public void endGrab(Long firstMid) {
		fragment.setValid(false);
		fragmentService.save(fragment);
	}

	@Override
	public void errorDeal(Long currentId, Long firstMid, Long uid) {
		this.endGrab(firstMid);
		createAndSaveFragment(new Fragment(fragment.getGroupId(), fragment.getType(), currentId, fragment.getMaxId(), fragment.getBloggerId(),
				uid, fragment.getOfficalBlogId(),fragment.getBlogType()));		
	}
	
	

	@Override
	public void exit() {
		dispatcher.release(1);
	}

	@Override
	public boolean isFirst() {
		return false;
	}

	@Override
	public void countRuntime(int count) {
		return;
	}

	@Override
	public boolean isInValidRange(Long mid) {
		if(mid > fragment.getSinceId()){
			return true;
		}
		return false;
	}

	

	
}
