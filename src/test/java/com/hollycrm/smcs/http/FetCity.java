package com.hollycrm.smcs.http;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.hollycrm.smcs.assist.ApplicationContextHolder;
import com.hollycrm.smcs.entity.base.Dictionary;
import com.hollycrm.smcs.http.impl.CommonHttpClient;
import com.hollycrm.smcs.service.base.DictionaryService;

public class FetCity {
	DictionaryService service;
	String token = "2.00XfdRiC1vlHnD06d7e7f1770B6h8_";
	
	//@Before
	public void init(){
		ApplicationContextHolder.init();
		service= ApplicationContextHolder.getBean(DictionaryService.class);
	}
	@Test
	public void fetchCity() throws InvalidHttpClientException, Exception {
		
		IHttpClient client = new CommonHttpClient();
		Dictionary dict = new Dictionary();
		dict.setId(1L);
		String key = "001";
		String url = "https://api.weibo.com/2/common/get_province.json?country="+key+"&access_token="+token;
		String entity = client.simpleHttpGet(url);
		List<City> list = convert(entity);
		Dictionary parent = null;
		for(City city:list){
			parent = new Dictionary();
			parent.setdFullCode(city.getCode());
			parent.setdCode(city.getCode().replace(key, ""));
			parent.setdName(city.getName());
			parent.setdValue(Integer.parseInt(parent.getdCode())+"");
			parent.setdType("BN_PROVINCE");
			parent.setParentDict(dict);
			parent.setStatus(true);
			service.save(parent);
			fetch(parent,client);
		}
		
	}
	
	public void fetch(Dictionary parent,IHttpClient client) throws Exception{
		String key = parent.getdFullCode();
		String url = "https://api.weibo.com/2/common/get_city.json?province="+key+"&access_token="+token;
		String entity = client.simpleHttpGet(url);
		List<City> list = convert(entity);
		Dictionary child = null;
		for(City city:list){
			child = new Dictionary();
			child.setdFullCode(city.getCode());
			child.setdCode(city.getCode().replace(key, ""));
			child.setdName(city.getName());
			child.setdValue(Integer.parseInt(child.getdCode())+"");
			child.setdType("BN_CITY");
			child.setParentDict(parent);
			child.setStatus(true);
			service.save(child);
			
		}
	}
	
	
	
	public List<City> convert(String entity) {
		JSONArray jsonArray = JSONArray.fromObject(entity);
		JSONObject jsonObject;

		List<City> list = new ArrayList<City>();
		City city = null;
		for (int i = 0; i < jsonArray.size(); i++) {			
			jsonObject = jsonArray.getJSONObject(i);
			for(Object key:jsonObject.keySet()){
				 city= new City();
				city.setCode(key.toString());
				city.setName(jsonObject.get(key).toString());
			}
			list.add(city);

		}
		return list;
	}

	@Test
	public void test22() {
		 final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh");
		 System.out.println(sdf.format(DateUtils.addMinutes(new Date(), -30)));
	}
}
