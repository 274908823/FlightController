package com.example.flight_control_002;

import java.io.File;

import com.example.flight_control_001.R;
import com.example.photo.PhotoActivity;

import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements
		MediaScannerConnectionClient {

	private Button setButton;
	private Button imageButton;
	private Button exitButton;
	private Button connect;
	private Button add;

	private static final String PIC_PATH = "/mnt/sdcard/Flight photo/";
	private static final String FILEBROWER_PackageName="com.estrongs.android.pop";
	private static final String FILEBROWER_ClassName="com.estrongs.android.pop.view.FileExplorerActivity";
	// Wi-Fi管理
	private WifiManager wifi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inter);
		MyActivityManager.getInstance().addActivity(this);
		// 获得wifi管理服务
		wifi = (WifiManager) MainActivity.this
				.getSystemService(Context.WIFI_SERVICE);
		if (!wifi.isWifiEnabled()) {
			Toast.makeText(MainActivity.this, "WiFi已关闭，请打开重试",
					Toast.LENGTH_SHORT).show();
		} else {
			System.out.println("wifi可用");
		}
		connect = (Button) findViewById(R.id.button1);
		connect.setOnClickListener(new connectListener());

		add = (Button) findViewById(R.id.button2);
		add.setOnClickListener(new addListener());

		setButton = (Button) findViewById(R.id.button3);
		setButton.setOnClickListener(new setListener());

		imageButton = (Button) findViewById(R.id.button4);
		imageButton.setOnClickListener(new imageListener());

		exitButton = (Button) findViewById(R.id.button5);
		exitButton.setOnClickListener(new exitListener());
	}

	class connectListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, VideoActivity.class);
			startActivity(intent);
		}
	}

	class addListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, ConnectActivity.class);
			startActivity(intent);
		}
	}

	class setListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// 创建需要对应于目标Activity的Intent
			Intent intent = new Intent(MainActivity.this,
					SettingActivity.class);
			// startActivity(intent);
			startActivityForResult(intent, 0);
		}
	}

	public String equalChanged(String moveCode, String prevMove) {
		if (moveCode != null && moveCode != "" && !moveCode.equals(prevMove)) {
			prevMove = moveCode;
			Log.d("log", prevMove);
		} else {
			System.out.println(prevMove);
			;
		}
		return prevMove;
	}

	public int portChanged(int port, int prevPort) {
		if (port != 0 && port != prevPort) {
			prevPort = port;
		} else {
			System.out.println("notchanged");
		}
		return prevPort;
	}

	class imageListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/*Intent intent = new Intent();
			intent.setComponent(new ComponentName(FILEBROWER_PackageName,
					FILEBROWER_ClassName));
			intent.setData(Uri.fromFile(new File(PIC_PATH)));
			startActivity(intent);*/
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, PhotoActivity.class);
			startActivity(intent);
			
		}
	}

	class exitListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MyActivityManager.getInstance().exit();
		}
	}

	private long exitTime = 0 ;
    @Override
    public void onBackPressed() {
            if(System.currentTimeMillis()-exitTime>2000){
                    Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                    exitTime=System.currentTimeMillis();
            }
            else{
            	MyActivityManager.getInstance().exit();
            }
//            super.onBackPressed();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onMediaScannerConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		// TODO Auto-generated method stub
		
	}

}
