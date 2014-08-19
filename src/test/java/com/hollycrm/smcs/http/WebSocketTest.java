package com.hollycrm.smcs.http;

import java.io.IOException;

import org.junit.Test;
import org.xlightweb.BodyDataSink;
import org.xlightweb.GetRequest;
import org.xlightweb.HttpRequestHeader;
import org.xlightweb.IHttpRequestHeader;
import org.xlightweb.IHttpResponse;
import org.xlightweb.IHttpResponseHandler;
import org.xlightweb.client.HttpClient;
import org.xsocket.connection.IConnection.FlushMode;

/**
 * 测试远程调用websocket
 * @author fly
 *
 */
public class WebSocketTest {
	
	@Test
	public void connection() throws Exception{
		HttpClient client = new HttpClient();
		
		client.setFollowsRedirect(true);  
		client.setAutoHandleCookies(false);
		GetRequest send = new GetRequest("ws://127.0.0.1/WebSocket/ChatWebSocketServlet");
		send.setHeader("Accept-Encoding", "gzip,deflate");
		send.setHeader("upgrade", "websocket");
		send.setHeader("connection", "upgrade");
		send.setHeader("sec-websocket-version", "13");
		send.setHeader("Sec-WebSocket-Key", "	rDksWEy/+raVbeM3/3ZnBw==");
		send.setHeader("Origin", "http://127.0.0.1");
		
		IHttpResponse response = client.call(send);
		System.out.println(response.getResponseHeader());
		IHttpRequestHeader header = new HttpRequestHeader("GET", "ws://127.0.0.1/WebSocket/ChatWebSocketServlet", "text/html");
		header.addHeader("Accept-Encoding", "gzip,deflate");
		header.addHeader("upgrade", "websocket");
		header.addHeader("connection", "upgrade");
		header.addHeader("sec-websocket-version", "13");
		header.addHeader("Sec-WebSocket-Key", "	rDksWEy/+raVbeM3/3ZnBw==");
		header.addHeader("Origin", "http://127.0.0.1");
		BodyDataSink bodyDataSink = client.send(header,  new IHttpResponseHandler(){

			@Override
			public void onResponse(IHttpResponse response) throws IOException {
				System.out.println("onResponse");
				System.out.println(response.getResponseHeader());
			}

			@Override
			public void onException(IOException ioe) throws IOException {
				System.out.println("onException");
				System.out.println(ioe.toString());
			}
			
		}); 
		
		 bodyDataSink.setAutoflush(false);            // 取消自动写入
     bodyDataSink.setFlushmode(FlushMode.ASYNC);  // 设置为异步方式 
     bodyDataSink.write("哈哈");
     bodyDataSink.flush();
	   client.close();
		
		
	}

}
