package com.hollycrm.smcs.assist;

import com.hollycrm.smcs.task.keyword.impl.TransformKeywordWorker;

public class AvaliableKeywordGroup extends Avaliable{

	private Long groupId;
	
	public AvaliableKeywordGroup(Long groupId){
		super(ONE_SECOND*10);
		this.groupId = groupId;
	}
	
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	


	@Override
	protected int leaseCount() {
		return TransformKeywordWorker.FETCH_COUNT;
	}
	
}
