package com.hollycrm.smcs.http;

public class AuthToken {
	
	public static final String FORMAT = "{\"access_token\": %s, \"expires_in\" : %s, \"uid\":%s}";
	
	/**
	 * 授权accessToken
	 */
	private String accessToken;
	
	/**
	 * 生命周期
	 */
	private String expiresIn;
	
	/**
	 * bogger uid
	 */
	private Long uid;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}
	
	public String toString() {
		return String.format(FORMAT, accessToken, expiresIn, uid);
	}

}
