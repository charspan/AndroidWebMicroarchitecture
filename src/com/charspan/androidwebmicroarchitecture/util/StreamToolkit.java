package com.charspan.androidwebmicroarchitecture.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 读流工具类
 */
public class StreamToolkit {

	/**
	 * 从输入流过滤CRLF读取一行
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static final String readLine(InputStream inputStream)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		int c1 = 0;
		int c2 = 0;
		while (c2 != -1 && !(c1 == '\r' && c2 == '\n')) {
			c1 = c2;
			c2 = inputStream.read();
			sb.append((char) c2);
		}
		if (sb.length() == 0)
			return null;
		return sb.toString();
	}

	public static byte[] readRawFromStream(InputStream fis) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[10240];
		int nReaded;
		try {
			while ((nReaded = fis.read(buffer)) > 0) {
				bos.write(buffer, 0, nReaded);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}

}
