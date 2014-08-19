package com.hollycrm.smcs.http;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.apache.commons.lang.StringUtils;

import com.sun.imageio.plugins.bmp.BMPImageReader;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import com.sun.imageio.plugins.png.PNGImageReader;

/**
 * 临时存储上传图片的内容，格式，文件信息等
 * 
 */
public class AttachItem {
	private final byte[] content;
	private final String name;
	private final String contentType;
	public AttachItem(byte[] content) throws Exception {
	    this("pic",content);
	}
	public AttachItem(String name,byte[] content) throws Exception{
		String imgtype = null;
		 imgtype = getContentType(content);
		this.content=content;
  	this.name=name;
  	this.contentType=StringUtils.isBlank(imgtype)? "application/octet-stream": imgtype;
	    
	}
	
	public byte[] getContent() {
		return content;
	}
	public String getName() {
		return name;
	}
	public String getContentType() {
		return contentType;
	}

	/**
	 * 获取图片contentType
	 * 
	 * @param mapObj
	 * @return
	 * @throws IOException
	 */
	public static String getContentType(byte[] mapObj) throws IOException {
		String type = "";
		ByteArrayInputStream bais = null;
		MemoryCacheImageInputStream mcis = null;
		try {
			bais = new ByteArrayInputStream(mapObj);
			mcis = new MemoryCacheImageInputStream(bais);
			Iterator itr = ImageIO.getImageReaders(mcis);
			while (itr.hasNext()) {
				ImageReader reader = (ImageReader) itr.next();
				if (reader instanceof GIFImageReader) {
					type = "image/gif";
				} else if (reader instanceof JPEGImageReader) {
					type = "image/jpeg";
				} else if (reader instanceof PNGImageReader) {
					type = "image/png";
				} else if (reader instanceof BMPImageReader) {
					type = "application/x-bmp";
				}
			}
		} finally {
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException ioe) {

				}
			}
			if (mcis != null) {
				try {
					mcis.close();
				} catch (IOException ioe) {

				}
			}
		}
		return type;
	}
}
