package com.hollycrm.smcs.http.note.impl;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.hollycrm.smcs.http.note.INoteMessage;


public class NoteMessage implements INoteMessage{

	@Override
	public String contentNoteMessageScript() {
		return "\"pid\":\"pl_content_notebox\"";
	}

	@Override
	public Elements getNoteMessageElements(Document doc) {
		Elements msgDialogueElement = doc.select("div.msg_dialogue");
		if(msgDialogueElement.isEmpty()){
			return msgDialogueElement;
		}		
		return msgDialogueElement.first().children();
	}

}
