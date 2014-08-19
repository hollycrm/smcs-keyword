package com.hollycrm.smcs.receive.parse.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.entity.base.ExtraFile;
import com.hollycrm.smcs.entity.base.IdOauth;
import com.hollycrm.smcs.entity.receive.ImageMessage;
import com.hollycrm.smcs.entity.receive.ReceiveMessage;
import com.hollycrm.smcs.http.AttachItem;
import com.hollycrm.smcs.http.util.HttpClientSingle;
import com.hollycrm.smcs.receive.parse.AbsReceiveMessageParse;
import com.hollycrm.smcs.service.base.IdOauthService;
import com.hollycrm.smcs.util.JsonUtil;

/**
 * 
 * @author dingqj 
 *
 * 2013-10-11 下午6:01:29
 */
public class ImageMessageParse extends AbsReceiveMessageParse {
	
	/**
	 * access_token 授权token
	 * fid tovfid 接收人查看id
	 * **/
	private static final String PIC_URL = "https://upload.api.weibo.com/2/mss/msget?access_token=%s&fid=%s";

	@Override
	protected ReceiveMessage parsePrivate(Map map) {
		ImageMessage imageMessage = new ImageMessage();
		Map data = getDataMap(map);
		imageMessage.setVfid(data.get("vfid").toString());
		imageMessage.setTovfid(data.get("tovfid").toString());
		downloadPid((Long) map.get("receiver_id"), imageMessage);
		return imageMessage;
	}
	
	private void downloadPid(Long receiverId, ImageMessage imageMessage){
		IdOauth oauth = ApplicationContextHolder.getBean(IdOauthService.class).findValidOauthByUid(receiverId, "w");
		if(oauth == null){
			imageMessage.setAttachUrl(null);
			imageMessage.setReason("没有找到有效的授权");
			return;
		}
		if(oauth.isExpired()){
			imageMessage.setAttachUrl(null);
			imageMessage.setReason("授权过期");
			return;
		}
		try{
			//Content-Length: 9088
			//Content-Disposition: attachment;filename="20130814124334-121104320.png"
			HttpClient client = HttpClientSingle.getHttpClient().getOriginalClient();
			/*.downloadPic(String.format(PIC_URL, oauth.getAccessToken(), imageMessage.getTovfid())
					, "upload", ".jpg", "imagemessage"));*/
			HttpGet httpGet = new HttpGet(String.format(PIC_URL, oauth.getAccessToken(), imageMessage.getTovfid()));
			HttpResponse httpResponse = client.execute(httpGet);
			Header[] headers = httpResponse.getAllHeaders();
			ExtraFile ef = new ExtraFile();
			for(Header header:headers){
				if("Content-Length".equals(header.getName())){
					ef.setFileSize(header.getValue());
				}else if("Content-Disposition".equals(header.getName())) {
					ef.setFileOldName(getFileOldName(header.getValue()));
				}				
			}
			
			
			List<AttachItem> items = new ArrayList<AttachItem>(1);
	  	items.add(new AttachItem("file", EntityUtils.toByteArray(httpResponse.getEntity())));
	  	Map<String, String> map = new HashMap<String, String>();
	  	map.put("parentFile", "upload");
	  	map.put("suffix", getSuffix(ef.getFileOldName()));
	  	map.put("prefix", "private");
	  	String name = HttpClientSingle.getHttpClient().httpPostWithFile(AppConfig.get("upload.pic.url"), map, items);
	  	Map<String, String> result = JsonUtil.getMap4Json(name);
	  	if(result.get("success").endsWith("1")) {
				ef.setFileNewName( "upload"+"/"+result.get("fileName"));
				imageMessage.setAttachUrl(ef.getFileNewName());
				List<ExtraFile> list = new ArrayList<ExtraFile>(1);
				list.add(ef);
				imageMessage.setExtraFileList(list);
			}else{
				imageMessage.setAttachUrl(null);
				imageMessage.setReason("上传图片失败");
			}
			
		}catch(Exception e){
			imageMessage.setAttachUrl(null);
			imageMessage.setReason(e.toString());
			e.printStackTrace();
		}
		
	}
	
	private String getSuffix(String fileName){
		return fileName.substring(fileName.lastIndexOf("."));
	}
	
	//Content-Disposition: attachment;filename="20130814124334-121104320.png"
	private String getFileOldName(String value){
		return value.substring(value.indexOf("filename")+10, value.length()-1);
	}

}
