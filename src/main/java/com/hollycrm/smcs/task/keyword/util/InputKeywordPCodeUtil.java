package com.hollycrm.smcs.task.keyword.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.hollycrm.smcs.config.AppConfig;
import com.hollycrm.smcs.http.IHttpClient;
import com.hollycrm.smcs.util.JsonUtil;

public abstract class InputKeywordPCodeUtil {

	public  static void inputKeywordPCode(String encodeKey, String secode, IHttpClient client) throws Exception{
		
			String url = AppConfig.get(KeywordConstant.SOSO_PINCODE_URL) + new Date().getTime();
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Referer", AppConfig.get(KeywordConstant.SINA_SOSO_URL) + encodeKey + "&Refer=STopic_box");
			Map<String, String> params = new HashMap<String, String>();
			params.put("_t", "0");
			params.put("pageid", "weibo");
			params.put("secode", secode);
			params.put("type", "sass");
			Map map = JsonUtil.getMap4Json(client.post(url, params, headers));
			String returnCode = (String) map.get("code");
			if (!"100000".equals(returnCode)) {
				throw new Exception("验证码出错");
			}

		
	}
}
