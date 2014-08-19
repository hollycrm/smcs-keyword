package com.hollycrm.smcs.receive.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weibo4j.http.BASE64Encoder;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.email.util.EmailUtils;
import com.hollycrm.smcs.entity.log.EarlyWarning;
import com.hollycrm.smcs.entity.log.WarnLevel;
import com.hollycrm.smcs.http.util.BaseAuth;
import com.hollycrm.smcs.monitor.ReceiveMessageMonitorContainer;
import com.hollycrm.smcs.monitor.bean.MonitorBean;
import com.hollycrm.smcs.receive.IReceiveMessageContainer;
import com.hollycrm.smcs.receive.IReceiveMessageStrategy;
import com.hollycrm.smcs.receive.bean.ExitMessage;
import com.hollycrm.smcs.receive.parse.ReceiveMessageParseFactory;
import com.hollycrm.smcs.service.log.EarlyWarningService;
import com.hollycrm.smcs.util.JsonUtil;

/**
 * 接收消息接口实现
 * @author dingqj
 *
 */
public class ReceiveMessageStrategy implements IReceiveMessageStrategy{

	private static final Logger logger = LoggerFactory.getLogger(ReceiveMessageStrategy.class);
	
	private static final ExitMessage EXIT_MESSAGE = new ExitMessage();
	
	/**
	 * @param source 申请应用时分配的AppKey，调用接口时候代表应用的唯一身份。
	 * @param uid  需要接收的蓝V用户ID。
	 */
	private static final String RECEIVE_MESSAGE_URL = "https://m.api.weibo.com/2/messages/receive.json?source=%s&uid=%s";
	
	/**
	 * @param since_id 上次连接断开时的消息ID。保存断开后5分钟内的新消息，可以通过since_id获取断开五分钟内的新消息。
	 */
	private static final String RECEIVE_MESSAGE_URL_SUFFIX = "&since_id=%s";
	
	private static final String DISCONNECTION_LOG = "uid{%s}连接断开,连接时长{%s},断开原因{%s},抓取数量{%s}";
	
	/**回车符**/
	private static final char ENTER_BREAK = '\r';
	
	/**换行符**/
	private static final char LINE_BREAK = '\n';
	
	private final   HttpClient client;
	
	private final long uid;
	
	private final long connectionTime; 
	
	/**存放消息容器**/
	private final IReceiveMessageContainer receiveMessageContainer;
	
	public ReceiveMessageStrategy(long uid, long connectionTime, IReceiveMessageContainer receiveMessageContainer){
		this.uid = uid;
		this.connectionTime = connectionTime;
		this.receiveMessageContainer = receiveMessageContainer;
		
		SchemeRegistry supportedSchemes = new SchemeRegistry();
    supportedSchemes.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
    supportedSchemes.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
    PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager(supportedSchemes);    
 // 参数设置
    HttpParams httpParams = new SyncBasicHttpParams();
    HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);    
    HttpProtocolParams.setUseExpectContinue(httpParams, false);
    HttpClientParams.setCookiePolicy(httpParams, CookiePolicy.BROWSER_COMPATIBILITY);
     client = new DefaultHttpClient(connectionManager, httpParams);
	
	}
	
		
	
	@Override
	public void receive(Long sinceId) {
		HttpGet httpGet = new HttpGet(buildReceiveMessageUrl(uid, sinceId));
		BaseAuth.auth(httpGet);
		ReceiveMessageMonitorContainer.register(new MonitorBean(uid, httpGet, System.currentTimeMillis(), connectionTime));
		fetchResult(execute(httpGet));
	}
	
	
	private HttpEntity execute(HttpUriRequest request){
		HttpEntity httpEntity = null;
		try{
			httpEntity = client.execute(request).getEntity();
		}catch(Exception e){
			System.out.println("报错时间"+System.currentTimeMillis());
			logger.error(e.toString(), e);
			try {
				receiveMessageContainer.push(EXIT_MESSAGE);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			exit();
		}
		return httpEntity;
		
	}
	
	
	/**
	 * 
	 * @param uid 需要接收的蓝V用户ID
	 * @param sinceId 上次连接断开时的消息ID。保存断开后5分钟内的新消息，可以通过since_id获取断开五分钟内的新消息。
	 * @return 接收消息url
	 */
	private String buildReceiveMessageUrl(Long uid, Long sinceId){
		if (null == sinceId) {
			return String.format(RECEIVE_MESSAGE_URL, AppConfig.get("sina.app.key"), uid);
		} else {
			return String.format(RECEIVE_MESSAGE_URL, AppConfig.get("sina.app.key"), uid)+String.format(RECEIVE_MESSAGE_URL_SUFFIX, sinceId);
		}
	}
	
	/**
	 * 授权，调用接口的登录帐号为该appkey的所有者，需要使用所有者帐号通过Base Auth的方式；
	 * @param httpGet
	 */
	private void auth(HttpGet httpGet){
		httpGet.setHeader("Authorization", "Basic "+BASE64Encoder.encode(
				(AppConfig.get("sina.app.username")+":"+AppConfig.get("sina.app.password")).getBytes()));
	}
	
	
	
	 /**
   * Read the contents of an entity and return it as a String.
   * The content is converted using the character set from the entity (if any),
   * failing that, "ISO-8859-1" is used.
   *
   * @param entity
   * @return String containing the content.
   * @throws ParseException if header elements cannot be parsed
   * @throws IllegalArgumentException if entity is null or if content length > Integer.MAX_VALUE
   * @throws IOException if an error occurs reading the input stream
   */
  private  void fetchResult(final HttpEntity entity) {
  	if(entity == null){
  		return;
  	}
      fetchResult(entity, (Charset)null);
  }
	
	/**
   * Get the entity content as a String, using the provided default character set
   * if none is found in the entity.
   * If defaultCharset is null, the default "ISO-8859-1" is used.
   *
   * @param entity must not be null
   * @param defaultCharset character set to be applied if none found in the entity
   * @return the entity content as a String. May be null if
   *   {@link HttpEntity#getContent()} is null.
   * @throws ParseException if header elements cannot be parsed
   * @throws IllegalArgumentException if entity is null or if content length > Integer.MAX_VALUE
   * @throws IOException if an error occurs reading the input stream
   */
  private  void fetchResult(
          final HttpEntity entity, final Charset defaultCharset)  {
  	
  		long begin = System.currentTimeMillis();
      if (entity == null) {
          throw new IllegalArgumentException("HTTP entity may not be null");
      }
      InputStream instream = null;
			try {
				instream = entity.getContent();
			}catch (Exception e) {
				logger.error(e.toString(), e);
				return;
			}
      if (instream == null) {
          return;
      }
      try {
          if (entity.getContentLength() > Integer.MAX_VALUE) {
              throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
          }
          int i = (int)entity.getContentLength();
          if (i < 0) {
              i = 4096;
          }
          ContentType contentType = ContentType.getOrDefault(entity);
          Charset charset = contentType.getCharset();
          if (charset == null) {
              charset = defaultCharset;
          }
          if (charset == null) {
              charset = HTTP.DEF_CONTENT_CHARSET;
          }
          Reader reader = new InputStreamReader(instream, charset);
          CharArrayBuffer buffer = new CharArrayBuffer(i);
          char[] tmp = new char[1024];
          int l;
          int size = 0;
          //结束符是\r\n
          
          try{
          	while((l = reader.read(tmp)) != -1) {
            	if((tmp[l-2] != ENTER_BREAK) || (tmp[l-1] != LINE_BREAK)) {
            		buffer.append(tmp, 0, l);
            	} else {
            		buffer.append(tmp, 0, l);
            		if(!checkBuffer(buffer.toString())){
            			break;
            		}            		
            		String [] entitys = buffer.toString().split(""+ENTER_BREAK+LINE_BREAK);
            		for(String message:entitys){
            			logger.info("接收到的消息：\n"+message);
            			transformResult(message);              		
            		}            		
            		size++;
            		buffer.clear();
            	}  						
            }          	
          	checkBuffer(buffer.toString());
          	logger.info("缓冲区中的内容:"+buffer.toString());
          	logger(begin, "正常结束", size);
          	
          }catch(SocketException e){
          	logger(begin, e.toString(), size);
          }catch(Exception e){
          	logger.error(e.toString(), e);
          }
      } finally {
      	if(null != instream){
      		try {
      			receiveMessageContainer.push(EXIT_MESSAGE);
						instream.close();
					} catch (Exception e) {
						logger.error(e.toString(), e);
					}
      	}
      }
  }
  
  //{"error":"you are not in developer mode or switch off","error_code":26406,"request":"/2/messages/receive.json"}
  private boolean checkBuffer(String buffer){
  	if((buffer.indexOf("error")!=-1) && (buffer.indexOf("error_code")!=-1) && ((buffer.indexOf("not in developer mode")!=-1) || (buffer.indexOf("switch off")!=-1))){
  		logger.info("错误的消息:\n"+buffer);
  		EarlyWarning earlyWarning = new EarlyWarning();
  		earlyWarning.setContent("boggerId{"+uid+"}的租户不是开发者模式");
  		earlyWarning.setCreatedAt(new Date());
  		earlyWarning.setTitle("私信接收接口异常");
  		earlyWarning.setWarning(EarlyWarning.WAIT_WARN);
  		earlyWarning.setWarnLevel(WarnLevel.WARN.getInfo());
  		earlyWarning.setWarnTarget(AppConfig.get(EmailUtils.MULTIPLE_EMAIL_ADDRESS));
  		ApplicationContextHolder.getBean(EarlyWarningService.class).save(earlyWarning);
  		return false;
  	}
  	return true;
  }
  
  private void logger(long begin, String reason, int count){
  	logger.info(String.format(DISCONNECTION_LOG, uid, System.currentTimeMillis()-begin, reason, count));
  }
  
  private void transformResult(String result) throws Exception{
  	if(StringUtils.isBlank(result)){
  		return;
  	}
  	Map map = JsonUtil.getMap4Json(result);  	
  	receiveMessageContainer.push(ReceiveMessageParseFactory.getReceiveMessageParse(map.get("type").toString()).parse(map));
  }

	@Override
	public void exit() {
		ReceiveMessageMonitorContainer.stop(uid);
	}

	

}
