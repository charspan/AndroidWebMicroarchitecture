package com.charspan.androidwebmicroarchitecture.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

import com.charspan.androidwebmicroarchitecture.handler.itf.IResourceUriHandler;
import com.charspan.androidwebmicroarchitecture.server.HttpContext;

/**
 * http://localhost:8088/request_data/
 * 
 * post data:{"username":"charspan","password":"123456"}
 */
public class PostDataHandler implements IResourceUriHandler {

	private String acceptPrefix = "/request_data/";

	@Override
	public boolean accept(String uri) {
		return uri.startsWith(acceptPrefix);
	}

	@Override
	public void handler(String uri, HttpContext httpContext) {
		try {
			long totalLength = Long.parseLong(httpContext
					.getRequestHeaderValue("Content-Length")
					.replace("\r\n", ""));
			InputStream is = httpContext.getUnderlySocket().getInputStream();
			String line;
			StringBuffer out = new StringBuffer();
			long nLeftLength = totalLength;
			byte[] buffer = new byte[10240];
			int nReaded = 0;
			while (nLeftLength > 0 && (nReaded = is.read(buffer)) > 0) {
				out.append(new String(buffer, 0, nReaded));
				nLeftLength -= nReaded;
			}
			line = out.toString();
			// System.out.println("line:" + line);
			JSONObject json = new JSONObject(line);
			String result = showRequestDatas(json);
			OutputStream nos = httpContext.getUnderlySocket().getOutputStream();
			PrintWriter writer = new PrintWriter(nos);
			writer.println("HTTP/1.1 200 OK");
			writer.println();
			writer.println(result);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String showRequestDatas(JSONObject json) {
		return null;
	}

}
