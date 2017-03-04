package com.charspan.androidwebmicroarchitecture.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import android.content.Context;

import com.charspan.androidwebmicroarchitecture.handler.itf.IResourceUriHandler;
import com.charspan.androidwebmicroarchitecture.server.HttpContext;
import com.charspan.androidwebmicroarchitecture.util.StreamToolkit;

/**
 * 静态的资源
 * http://localhost:8088/static/client.html
 */
public class ResourceInAssetsHandler implements IResourceUriHandler {

	private String acceptPrefix = "/static/";
	private Context context;
	
	public ResourceInAssetsHandler(Context context) {
		this.context=context;
	}

	@Override
	public boolean accept(String uri) {
		return uri.startsWith(acceptPrefix);
	}

	@Override
	public void handler(String uri, HttpContext httpContext) {
		int startIndex=acceptPrefix.length();
		String assetsPath=uri.substring(startIndex);
		//Log.e("charspan", "assetsPath:"+assetsPath);
		try {
			InputStream fis = context.getAssets().open(assetsPath);
		    byte[] raw=StreamToolkit.readRawFromStream(fis);
		    fis.close();
		    OutputStream nos=httpContext.getUnderlySocket().getOutputStream();
		    PrintStream printer = new PrintStream(nos);
		    printer.println("HTTP/1.1 200 OK");
		    printer.println("Content-Length:"+raw.length);
		    if(assetsPath.endsWith(".html")){
		    	printer.println("Content-Type:text/html");
		    }else if(assetsPath.endsWith(".js")){
		    	printer.println("Content-Type:text/js");
		    }else if(assetsPath.endsWith(".css")){
		    	printer.println("Content-Type:text/css");
		    }else if(assetsPath.endsWith(".jpg")){
		    	printer.println("Content-Type:text/jpg");
		    }else if(assetsPath.endsWith(".png")){
		    	printer.println("Content-Type:text/png");
		    }
		    printer.println();
		    printer.write(raw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
