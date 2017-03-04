package com.charspan.androidwebmicroarchitecture;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.charspan.androidwebmicroarchitecture.config.WebConfiguration;
import com.charspan.androidwebmicroarchitecture.handler.PostDataHandler;
import com.charspan.androidwebmicroarchitecture.handler.ResourceInAssetsHandler;
import com.charspan.androidwebmicroarchitecture.handler.UploadImageHandler;
import com.charspan.androidwebmicroarchitecture.server.SimpleHttpServer;

public class MainActivity extends Activity {

	private Button btn_operator;
	private TextView username;
	private TextView password;

	private SimpleHttpServer shs;

	private boolean isServerRun;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		username = (TextView) findViewById(R.id.username);
		password = (TextView) findViewById(R.id.password);
		btn_operator = (Button) findViewById(R.id.btn_operator);

		WebConfiguration webConfiguration = new WebConfiguration();
		webConfiguration.setPort(8088);
		webConfiguration.setMaxParallels(50);
		shs = new SimpleHttpServer(webConfiguration);
		shs.registerResourceHandler(new ResourceInAssetsHandler(this));
		shs.registerResourceHandler(new UploadImageHandler() {

			@Override
			protected void onImageLoaded(String path) {
				showImage(path);
			}
		});
		shs.registerResourceHandler(new PostDataHandler() {

			@Override
			public String showRequestDatas(JSONObject json) {
				return showDatas(json);
			}
		});

		btn_operator.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isServerRun) {
					isServerRun = false;
					btn_operator.setText("启动服务");
					shs.stopAsync();
				} else {
					isServerRun = true;
					btn_operator.setText("关闭服务");
					shs.startAsync();
				}
			}
		});
	}

	protected void showImage(final String path) {
		 Log.d("spy", "showImage:"+path);
		// UI线程
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ImageView mImageView = (ImageView) findViewById(R.id.iv_main);
				Bitmap bm = BitmapFactory.decodeFile(path);
				mImageView.setImageBitmap(bm);
				Toast.makeText(MainActivity.this, "upload success!",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected String showDatas(final JSONObject jsonStr) {
		final String[] result = { "faile" };
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				username.setText(jsonStr.optString("username", "username"));
				password.setText(jsonStr.optString("password", "password"));
				result[0] = "success";
			}
		});
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result[0];
	}

	@Override
	protected void onDestroy() {
		shs.stopAsync();
		super.onDestroy();
	}
    
}
