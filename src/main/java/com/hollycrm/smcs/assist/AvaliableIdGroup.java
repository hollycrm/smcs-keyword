package com.hollycrm.smcs.assist;

/**
 * 
 * 有效的IdGroup
 * @author dingqj 
 * @since 
 * 2013-11-11 下午2:47:49
 */
public class AvaliableIdGroup extends Avaliable{
	

	private Long groupId;
	
	private Long maxId;
	
	
	public AvaliableIdGroup(Long groupId, Long maxId) {
		super(0);
		this.groupId = groupId;
		this.maxId = maxId;
	}

	@Override
	protected int leaseCount() {
		return 0;
	}

	public Long getGroupId() {
		return groupId;
	}
	
	
	public Long getRealGroupId(){
		if(groupId == 0L){
			return null;
		}
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getMaxId() {
		return maxId;
	}

	public void setMaxId(Long maxId) {
		this.maxId = maxId;
	}
}
