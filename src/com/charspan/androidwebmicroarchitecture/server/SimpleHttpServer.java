package com.charspan.androidwebmicroarchitecture.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.charspan.androidwebmicroarchitecture.config.WebConfiguration;
import com.charspan.androidwebmicroarchitecture.handler.itf.IResourceUriHandler;
import com.charspan.androidwebmicroarchitecture.util.StreamToolkit;

/**
 * 模拟器上访问当前开发机IP:10.0.2.2
 * 
 * 模拟器映射指令
 * 
 * telnet localhost 5566(模拟器端口)
 * 
 * auth /2mLFLiMjxNZpAkc
 * 
 * redir add tcp:8088:8088(将本机8088端口映射到模拟器8088端口)
 * 
 */
public class SimpleHttpServer {

	private final WebConfiguration webConfiguration;
	private ExecutorService threadPool;
	private boolean isEnable;
	private ServerSocket socket;
	private Set<IResourceUriHandler> resourceUriHandlers;

	public SimpleHttpServer(WebConfiguration webConfiguration) {
		this.webConfiguration = webConfiguration;
		this.threadPool = Executors.newCachedThreadPool();
		this.resourceUriHandlers = new HashSet<IResourceUriHandler>();
	}

	/**
	 * 启动server(异步)
	 */
	public void startAsync() {
		isEnable = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				doProcSync();
			}
		}).start();
	}

	/**
	 * 停止server(异步)
	 */
	public void stopAsync() {
		if (!isEnable) {
			return;
		}
		isEnable = false;
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			Log.e("charspan", e.toString());
		}

	}

	private void doProcSync() {
		try {
			InetSocketAddress socketAddr = new InetSocketAddress(
					webConfiguration.getPort());
			socket = new ServerSocket();
			socket.bind(socketAddr);
			while (isEnable) {
				final Socket remotePeer = socket.accept();
				threadPool.submit(new Runnable() {

					@Override
					public void run() {
						Log.d("charspan", "a remote peer accepted..."
								+ remotePeer.getInetAddress().toString());
						onAcceptRemotePeer(remotePeer);
					}
				});
			}
		} catch (IOException e) {
			Log.e("charspan", e.toString());
		}
	}

	/**
	 * 注册路由
	 * 
	 * @param handler
	 */
	public void registerResourceHandler(IResourceUriHandler handler) {
		resourceUriHandlers.add(handler);
	}

	/**
	 * 处理远程连接
	 * 
	 * @param remotePeer
	 */
	protected void onAcceptRemotePeer(Socket remotePeer) {
		try {
			// remotePeer.getOutputStream().write("success!".getBytes());
			HttpContext httpContext = new HttpContext();
			httpContext.setUnderlySocket(remotePeer);
			InputStream inputStream = remotePeer.getInputStream();
			String headerLine = null;
			// 获取 url 相对路径,头信息第一行用空格分开
			String resourceUri = headerLine = StreamToolkit.readLine(
					inputStream).split(" ")[1];
			// Log.d("charspan", "resourceUri:" + resourceUri);
			// remotePeer.getOutputStream().write(resourceUri.getBytes());
			while ((headerLine = StreamToolkit.readLine(inputStream)) != null) {
				if (headerLine.equals("\r\n"))// 头信息会以两个/r/n 结尾
					break;
				String[] pair = headerLine.split(": ");
				httpContext.addRequestHeader(pair[0], pair[1]);
				// Log.d("charspan", headerLine);
			}
			for (IResourceUriHandler handler : resourceUriHandlers) {
				if (!handler.accept(resourceUri)) {
					continue;
				}
				handler.handler(resourceUri, httpContext);
			}
		} catch (IOException e) {
			Log.e("charspan", e.toString());
		} finally {
			try {
				remotePeer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
