package com.charspan.androidwebmicroarchitecture.server;

import java.net.Socket;
import java.util.HashMap;

/**
 *
 */
public class HttpContext {

	/**
	 * 头信息 注意value后面都有\r\n
	 */
	private final HashMap<String, String> requestHeaders;

	public HttpContext() {
		requestHeaders = new HashMap<String, String>();
	}

	/**
	 * 当前客户端socekt
	 */
	private Socket underlySocket;

	public void setUnderlySocket(Socket remotePeer) {
		this.underlySocket = remotePeer;
	}

	public Socket getUnderlySocket() {
		return underlySocket;
	}

	public void addRequestHeader(String headerName, String headerValue) {
		requestHeaders.put(headerName, headerValue);
	}

	public String getRequestHeaderValue(String headerName) {
		return requestHeaders.get(headerName);
	}

}
