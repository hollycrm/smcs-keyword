package com.hollycrm.smcs.task.board.impl;

import com.hollycrm.smcs.assist.Avaliable;
import com.hollycrm.smcs.assist.AvaliableBoard;
import com.hollycrm.smcs.entity.fetch.Fragment;
import com.hollycrm.smcs.entity.fetch.HtmlMaxId;
import com.hollycrm.smcs.task.AbsGrabNormalHtml;
import com.hollycrm.smcs.task.IDispatcher;

public class GrabBoardHtml extends AbsGrabNormalHtml {

	private final AvaliableBoard board;

	public GrabBoardHtml(IDispatcher dispatcher, AvaliableBoard board) {
		super(dispatcher);
		this.board = board;
	}

	@Override
	protected String getMentionType() {
		return MENTION_BOARD;
	}

	@Override
	protected Object getThreadId() {
		return board.getBloggerId();
	}

	@Override
	protected HtmlMaxId findHtmlMaxId() {
		return maxIdService.findBoardMax(board.getGroupId(), board.getBloggerId());
	}

	@Override
	protected HtmlMaxId createdHtmlMaxId(Long maxId) {
		return this.createdHtmlMaxId(board.getBloggerId(), board.getGroupId(), null, maxId, board.getMediaType(),
				getMentionType(), null);
	}

	@Override
	protected void generateAndSaveFragment(Long maxId, Long uid) {
		this.createAndSaveFragment(new Fragment(board.getGroupId(), MENTION_BOARD, sinceId, maxId, board.getBloggerId()));
	}

	@Override
	protected Avaliable getAvaliable() {
		return board;
	}

}
