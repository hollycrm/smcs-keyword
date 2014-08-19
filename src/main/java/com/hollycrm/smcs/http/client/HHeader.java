package com.hollycrm.smcs.http.client;

/**
 * http请求Header
 *
 * @author dingqj 
 * @since 
 * 2013-12-23 上午11:41:48
 */
public class HHeader {
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 值
	 */
	private String value;
	
	public HHeader(){}
	
	public HHeader(String name, String value){
		this.name = name;
		this.value = value;
	}

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return name+":"+value;
	}

}
