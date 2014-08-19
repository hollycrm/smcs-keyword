package com.hollycrm.smcs.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
	private static SimpleDateFormat sdf;
	/**
	 * 把str转化为时间
	 * @param str
	 * @return
	 */
	public static Date formatDate(String str){
		 if((str==null)||"".equals(str)){
     	return null;
     }
		 if(sdf == null){
			 sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
	     sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		 }
     try {
    	 synchronized(sdf){
    		 return sdf.parse(str);
    	 }       
     } catch (ParseException pe) {
       return null;
     }
	}

}
