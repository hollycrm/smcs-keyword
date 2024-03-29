package com.hollycrm.smcs.util;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * 
 * @author yongtree
 * @date 2008-11-22 上午10:54:19
 * @version 1.0
 */
public class JsonDateValueProcessor implements JsonValueProcessor {

	private String format = "yyyy-MM-dd HH:mm:ss";

	public JsonDateValueProcessor() {

	}

	public JsonDateValueProcessor(String format) {
		this.format = format;
	}

	@Override
	public Object processArrayValue(Object value, JsonConfig jsonConfig) {
		String[] obj = {};
		if (value instanceof Date[]) {
			SimpleDateFormat sf = new SimpleDateFormat(this.format);
			Date[] dates = (Date[]) value;
			obj = new String[dates.length];
			for (int i = 0; i < dates.length; i++) {
				obj[i] = sf.format(dates[i]);
			}
		}
		return obj;
	}

	@Override
	public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
		if (value instanceof Date) {
			String str = new SimpleDateFormat(this.format).format((Date) value);
			return str;
		}
		return value == null ? null : value.toString();
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}