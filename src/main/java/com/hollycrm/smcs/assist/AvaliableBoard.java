package com.hollycrm.smcs.assist;

public class AvaliableBoard extends Avaliable{
	private final Long bloggerId;
	private Long groupId;
	private String mediaType;
	
	public AvaliableBoard(Long bloggerId, Long groupId, String mediaType,long interval){
		super(interval);
		this.bloggerId = bloggerId;
		this.groupId = groupId;
		this.mediaType = mediaType;
	}
	
	public Long getBloggerId() {
		return bloggerId;
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

	


	@Override
	protected int leaseCount() {
		return LEAST_COUNT;
	}
}
