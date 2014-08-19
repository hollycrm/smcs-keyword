package com.hollycrm.smcs.task;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.assist.Avaliable;
import com.hollycrm.smcs.entity.fetch.HtmlMaxId;
import com.hollycrm.smcs.service.fetch.MaxIdService;

public abstract class AbsGrabNormalHtml extends AbsGrabHtml{
	


	/**关键字**/
	protected static final String MENTION_SEARCH = "search";
	
	/**私信**/
	protected static final String MENTION_PRIVATE = "private";
	
	/**留言板 **/
	protected static final String MENTION_BOARD = "board";
	
	protected static final String MENTION_NOTE = "note";
	
	protected static final String mediaType = "w";
	
	
	protected  Long sinceId;
	protected HtmlMaxId htmlMaxId;
	protected final MaxIdService maxIdService;
	
	
	
	public AbsGrabNormalHtml(IDispatcher dispatcher){
		super(dispatcher);
		maxIdService = ApplicationContextHolder.getBean(MaxIdService.class);
	}
	
	@Override
	public boolean isInRange(Long mid) {
		if(sinceId ==null ) {
			return true;
		} else{
			if(mid > sinceId) {
				return true;
			}			
		}
		return false;
	}

	@Override
	public boolean isInValidRange(Long mid) {
		
		return isInRange(mid);
	}

	@Override
	public void initSinceId() {			
		htmlMaxId = findHtmlMaxId();
		if(htmlMaxId != null){
			sinceId = htmlMaxId.getMaxId();
		}
		
	}

	@Override
	public boolean isFirst() {
			if(sinceId == null){
				return true;
			}
			return false;
	}

	@Override
	public void endGrab(Long firstMid) {
		if(firstMid == null){
			return;
		}
		if(htmlMaxId == null){		
			maxIdService.save(createdHtmlMaxId(firstMid));
		}else{
			htmlMaxId.setMaxId(firstMid);
			maxIdService.save(htmlMaxId);
		}
		
		
	}

	@Override
	public void errorDeal(Long currentId, Long firstMid, Long uid) {
		this.endGrab(firstMid);
		if((sinceId != null) && (currentId != null)) {			
			generateAndSaveFragment(currentId,uid);
		}
		
	}

	@Override
	public void exit() {
		getAvaliable().sleep();
		dispatcher.release(getThreadId());
	}
	
	
	
	
	

	@Override
	public void countRuntime(int count) {
		if(!isFirst()){
			getAvaliable().countRuntime(count);
		}
		
	}
	
	protected abstract Avaliable getAvaliable();

	protected HtmlMaxId createdHtmlMaxId(Long bloggerId, Long groupId, String key, Long maxId,
			String mediaType, String mentionType, Long uid){
		HtmlMaxId htmlMaxId = new HtmlMaxId();
		htmlMaxId.setBloggerId(bloggerId);
		htmlMaxId.setIdGroup(groupId);
		htmlMaxId.setKeyword(key);
		htmlMaxId.setMaxId(maxId);
		htmlMaxId.setMediaType(mediaType);
		htmlMaxId.setMentionType(getMentionType());
		htmlMaxId.setUid(uid);
		return htmlMaxId;
	}
	
	/**
	 * 取抓取页面类型
	 * @return
	 */
	protected abstract String getMentionType();
	
	protected abstract Object getThreadId();
	
	protected abstract HtmlMaxId findHtmlMaxId();
	
	protected abstract HtmlMaxId createdHtmlMaxId(Long maxId);
	
	protected abstract void generateAndSaveFragment(Long maxId, Long uid);
	
	
}
