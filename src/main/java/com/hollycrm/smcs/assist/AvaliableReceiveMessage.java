package com.hollycrm.smcs.assist;

public class AvaliableReceiveMessage extends Avaliable{

	private Long groupId;
	private String mediaType;
	private Long bloggerId;

	/**消息接口时长**/
	private Long receiveMessageTime;
	
	public AvaliableReceiveMessage(){
		super(TWO_MINUTE);
	}
	
	public AvaliableReceiveMessage( Long bloggerId, Long groupId, String mediaType, 
			long interval, Long receiveMessageTime){
		super(interval);
		this.bloggerId = bloggerId;
		this.groupId = groupId;
		this.mediaType = mediaType;		
		this.receiveMessageTime = receiveMessageTime;
	}

	
	@Override
	protected int leaseCount() {
		return 2;
	}

	

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public Long getBloggerId() {
		return bloggerId;
	}

	public void setBloggerId(Long bloggerId) {
		this.bloggerId = bloggerId;
	}
	

	public void compare(AvaliableReceiveMessage temp){		
			receiveMessageTime = temp.getReceiveMessageTime();
			interval = temp.getInterval();		
	}

	public Long getReceiveMessageTime() {
		return receiveMessageTime;
	}

	public void setReceiveMessageTime(Long receiveMessageTime) {
		this.receiveMessageTime = receiveMessageTime;
	}


}
