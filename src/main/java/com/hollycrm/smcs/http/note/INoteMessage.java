package com.hollycrm.smcs.http.note;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public interface INoteMessage {
	
	String contentNoteMessageScript();
	
	Elements getNoteMessageElements(Document doc);

}
