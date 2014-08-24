package com.hollycrm.smcs.security;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.email.util.EmailUtils;
import com.hollycrm.smcs.entity.code.VerifyCode;
import com.hollycrm.smcs.remote.ISecurityCodeProxy;
import com.hollycrm.smcs.remote.exception.RmiException;
import com.hollycrm.smcs.remote.impl.SecurityCodeProxy;
import com.hollycrm.smcs.security.Exception.NoInputSecurityCodeException;
import com.hollycrm.smcs.service.code.VerifyCodeServce;



public abstract class AbsSecurityCode implements ISecurityCode{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected static final String SERVER_URL = "server.url";
	
	protected static final String INPUT_SECURITY_CODE_URL = "/code/verify-code!input.do?id=";
	
	private  ISecurityCodeProxy securityCodeProxy;
	
	private final ExecutorService executors =  Executors.newFixedThreadPool(2);
	
	public AbsSecurityCode(){
		securityCodeProxy = ApplicationContextHolder.getBean(SecurityCodeProxy.class);
	}

	@Override
	public String getDoor(String key) throws NoInputSecurityCodeException {
		String door = "";
		for(int i=0;i<=12;i++){
			try {
				door = securityCodeProxy.getSecurityCode(key, getType());
				if(door != null){
					return door;
				}
				Thread.sleep(10000L);
			} catch (Exception e) {				
				continue;
			}
		}
		if(StringUtils.isBlank(door)){
			throw new NoInputSecurityCodeException("验证码没有输入");
		}
		return door;
	}

	@Override
	public String save(String key, Long groupId, String fileName) {
		String uuid = UUID.randomUUID().toString();
		VerifyCode verifyCode = new VerifyCode(uuid, groupId, fileName ,getType());
		removeDoor(uuid);
		ApplicationContextHolder.getBean(VerifyCodeServce.class).save(verifyCode);
		//sendMail(key, "请输入"+getSecurityTitle()+"验证码", verifyCode.getId());
		sendMailThread(key, "请输入"+getSecurityTitle()+"验证码", verifyCode.getId());
		return uuid;
	}

	private void sendMailThread(final String key,final String title, final Long verifyCodeId){
		executors.submit(new Runnable(){
			@Override
			public void run() {
				sendMail(key, title, verifyCodeId);
			}			
		});
	}
	
	protected abstract String getSecurityTitle();
	
	protected void sendMail(String key, String title, Long verifyCodeId) {
		try{
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("username", key);
			values.put("type", getType());
			values.put("url", AppConfig.get(SERVER_URL) + INPUT_SECURITY_CODE_URL + verifyCodeId);	
			EmailUtils.sendCommonMail(title, values);
		}catch(Exception e){
			e.printStackTrace();
			;
		}
		
	}

	
	
	protected void removeDoor(String key) {
		try {
			securityCodeProxy.removeSecurityCode(key, getType());
			logger.info("删除"+getSecurityTitle()+"验证码key:"+key);
		} catch (RmiException e) {
			logger.error(e.getLocalizedMessage(),e);
		}
	}


}
