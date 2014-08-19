package weibo4j;

import weibo4j.http.HttpClient;

/**
 * @author sinaWeibo
 * 
 */

public class Weibo implements java.io.Serializable {

	private static final long serialVersionUID = 4282616848978535016L;

	public  HttpClient client = new HttpClient(150, 10000, 10000, 1024 * 1024);

	/**
	 * Sets token information
	 * 
	 * @param token
	 */
	public synchronized void setToken(String token) {
		client.setToken(token);
	}
	public HttpClient getClient(){
		return client;
	}

}