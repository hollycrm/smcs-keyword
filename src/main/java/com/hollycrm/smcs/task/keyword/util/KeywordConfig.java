package com.hollycrm.smcs.task.keyword.util;

import java.io.IOException;
import java.util.Properties;

import com.hollycrm.smcs.config.AppConfig;

public abstract class KeywordConfig {
	
	private static final String LOGIN_CONFIG = "keyword.properties";	
	private static Properties prop=new Properties();
	
	 static{		
		 try {
			 prop.load(AppConfig.class.getClassLoader().getResourceAsStream(LOGIN_CONFIG));
			}
			catch (IOException ex) {
				throw new IllegalStateException("Could not load '"+LOGIN_CONFIG+"': " + ex.getMessage());
			}
	}
	public static String get(String key){
		return prop.getProperty(key);
	}
}
