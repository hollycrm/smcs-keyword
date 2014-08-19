package com.hollycrm.smcs.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.MimetypesFileTypeMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * 完成Http通讯前各种预处理操作的工具类
 */
public class HttpUtil {
    /**
     * <p>从带文件名的路径中获取文件对应的MIME type.</p>
     * @param fileName
     *            带文件名的文件路径.
     * @return MIME type.
     */
    public static String getContentType(String fileName) {
        return new MimetypesFileTypeMap().getContentType(fileName);
    }

    /**
     * 根据File对象得到其对应MIME typee.
     * @param file
     *            File对象
     * @return MIME type.
     */
    public static String getContentType(File file) {
        return new MimetypesFileTypeMap().getContentType(file);
        // // return null;
        // return "png";
    }
    
    public static List<NameValuePair> buildParamsList(Map<String,String> map){
    	List<NameValuePair> nvps=new ArrayList<NameValuePair>();
    		Iterator<Entry<String,String>> it=map.entrySet().iterator();
    		while(it.hasNext()){
    			Map.Entry<String, String> entry=it.next();
    			nvps.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
    		}
			return nvps;
    }


}
