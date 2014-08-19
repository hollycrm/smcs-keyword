package com.hollycrm.smcs.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;

public class OauthUtil {
	public static String encodeAccount(String account) {
		String userName = "";
		try {
			userName = new String(Base64.encodeBase64(URLEncoder.encode(account, "UTF-8").getBytes()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return userName;
	}

	public static String makeNonce(int len) {
		String x = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String str = "";
		for (int i = 0; i < len; i++) {
			str += x.charAt((int) (Math.ceil(Math.random() * 1000000) % x.length()));
		}
		return str;
	}

	public static String getServerTime() {
		long servertime = new Date().getTime() / 1000;
		return String.valueOf(servertime);
	}

	public static long getServerTimeLong() {
		long servertime = new Date().getTime() / 1000;
		return servertime;
	}

}
