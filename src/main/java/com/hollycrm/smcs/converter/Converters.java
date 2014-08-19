package com.hollycrm.smcs.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author fly
 *
 */
public abstract class Converters {

	public static final String A_REGEX = "<a[\\s\\S]*?>(.*?)</a>";
	
	public static final String REPLACE_A_REGEX = "<a[\\s\\S]*?>|</a>";
	
	public static final String SPAN_REGEX = "<span[\\s\\S]*?>(.*?)</span>";
	
	public static final String REPLACE_SPAN_REGEX = "<span[\\s\\S]*?>|</span>";
	
	public static final String IMG_REGEX = "<img[\\s\\S]*?>";

	public static String removeAt(String original){
		return remove(original,A_REGEX,REPLACE_A_REGEX);
	}
	
	public static String removeSpan(String original){
		return remove(original,SPAN_REGEX,REPLACE_SPAN_REGEX);
	}
	
	public static String removeImg(String original){
		return remove(original, IMG_REGEX, IMG_REGEX);
	}
	
	public static String remove(String original,String matchRegex, String replaceRegex){
		Pattern p=Pattern.compile(matchRegex);
		Matcher m=p.matcher(original);
		String temp = original.replaceAll(matchRegex, "%s");
		List<String> list = new ArrayList<String>();
		while(m.find()){
			list.add(m.group().replaceAll(replaceRegex, ""));			
		}
		return String.format(temp, list.toArray());
	}

	
	public static String convert(String original){
		return removeImg(removeSpan(removeAt(original)));
	}

	public static void main(String[] args) {
		String a = "<a class=\"a_topic\" href=\"http://huati.weibo.com/k/%E5%B0%8F%E7%B1%B3%E6%89%8B%" +
				"E6%9C%BA%E5%B0%B1%E6%98%AF%E5%BF%AB?from=526\" target=\"_blank\">#小米<span style=\"color:red;\">" +
				"手机</span>就是快#</a>保佑让我中奖吧，2s，<a href=\"http://weibo.com/n/%E5%B0%8F%E7%B1%B3%E6%89%8B%E6%9C%BA\"" +
				" usercard=\"name=小米手机\">@小米手机</a>";
				System.out.println(removeImg(removeSpan(removeAt(a))));
	}

}
