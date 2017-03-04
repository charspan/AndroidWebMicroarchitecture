package com.charspan.androidwebmicroarchitecture.handler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import android.os.Environment;
import android.util.Log;

import com.charspan.androidwebmicroarchitecture.handler.itf.IResourceUriHandler;
import com.charspan.androidwebmicroarchitecture.server.HttpContext;

/**
 * 上传图片
 * http://localhost:8088/upload_image/
 */
public class UploadImageHandler implements IResourceUriHandler {

	private String acceptPrefix = "/upload_image/";

	@Override
	public boolean accept(String uri) {
		return uri.startsWith(acceptPrefix);
	}

	@Override
	public void handler(String uri, HttpContext httpContext) {
		String tmpPath = Environment.getExternalStorageDirectory().getPath() + "/test_upload.jpg";
		long totalLength = Long.parseLong(httpContext.getRequestHeaderValue("Content-Length").replace("\r\n", ""));
		try {
			FileOutputStream fos = new FileOutputStream(tmpPath);
			InputStream nis = httpContext.getUnderlySocket().getInputStream();
			byte[] buffer = new byte[10240];
			int nReaded = 0;
			long nLeftLength = totalLength;
			while (nLeftLength > 0 && (nReaded = nis.read(buffer)) > 0) {
				fos.write(buffer, 0, nReaded);
				nLeftLength -= nReaded;
			}
			fos.close();
			OutputStream nos = httpContext.getUnderlySocket().getOutputStream();
			PrintStream printer = new PrintStream(nos);
			printer.println("HTTP/1.1 200 OK");
			printer.println();
			onImageLoaded(tmpPath);
		} catch (FileNotFoundException e) {
			Log.d("spy", "FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("spy", "IOException");
			e.printStackTrace();
		}
	}

	protected void onImageLoaded(String path) {

	}

}
