package com.hollycrm.smcs.assist;


public class AvaliablePm extends Avaliable{
	
	
	private Long groupId;
	private String mediaType;
	private Long bloggerId;
	private int type;

	
	
	public AvaliablePm(){
		super(TWO_MINUTE);
	}
	
	public AvaliablePm( Long bloggerId, Long groupId, String mediaType, int type, 
			 long interval){
		super(interval);
		this.bloggerId = bloggerId;
		this.groupId = groupId;
		this.mediaType = mediaType;
		this.type = type;

		
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
	

	public void compare(AvaliablePm temp){			
			type = temp.getType();			
			interval = temp.getInterval();		
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
