package com.hollycrm.smcs.assist;


public class AvaliableKeyword extends Avaliable{
	
	private final Long conditionId;
	
	/**搜索关键字**/
	private String key;
	
	/**排它关键字**/
	private String exclusiveKey;
	
	/**省或直辖市**/
	private String province;
	
	/**地市**/
	private String city;
	
	private final Long groupId;	
	
	private long beginTime;	
	
	public synchronized long getBeginTime() {
		return beginTime;
	}

	public synchronized void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}	
	

	public AvaliableKeyword(String key, String exclusiveKey, Long conditionId, 
			Long groupId,long interval, String province, String city){
		super(interval);
		this.conditionId = conditionId;
		this.groupId = groupId;
		this.key = key;
		this.exclusiveKey = exclusiveKey;
		this.province = province;
		this.city = city;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
		
	}

	public Long getGroupId() {
		return groupId;
	}

	public Long getConditionId() {
		return conditionId;
	}


	

	@Override
	protected int leaseCount() {
		return LEAST_COUNT;
	}

	public String getExclusiveKey() {
		return exclusiveKey;
	}

	public void setExclusiveKey(String exclusiveKey) {
		this.exclusiveKey = exclusiveKey;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}


}
