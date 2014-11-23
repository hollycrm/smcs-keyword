package com.hollycrm.smcs.http.note.impl;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.http.note.INoteMessage;


public class NoteMessage implements INoteMessage{

	@Override
	public String contentNoteMessageScript() {
		return "\"domid\":\"v6_pl_content_notebox\"";
	}

	@Override
	public Elements getNoteMessageElements(Document doc) {
		return doc.select("div.msg_bubble_list");
	
	}

}
