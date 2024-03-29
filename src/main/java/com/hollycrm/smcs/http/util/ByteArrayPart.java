package com.hollycrm.smcs.http.util;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.multipart.PartBase;

public class ByteArrayPart  extends PartBase {
	private final byte[] mData;
	private final String mName;

	public ByteArrayPart(byte[] data, String name, String type)
			throws IOException {
		super(name, type, "UTF-8", "binary");
		mName = name;
		mData = data;
	}

	@Override
	protected void sendData(OutputStream out) throws IOException {
		out.write(mData);
	}

	@Override
	protected long lengthOfData() throws IOException {
		return mData.length;
	}

	@Override
	protected void sendDispositionHeader(OutputStream out)
			throws IOException {
		super.sendDispositionHeader(out);
		StringBuilder buf = new StringBuilder();
		buf.append("; filename=\"").append(mName).append("\"");
		out.write(buf.toString().getBytes());
	}

}
