package com.example.flight_control_002;

import java.io.DataOutputStream;
import java.io.IOException;
import com.example.camera.MjpegInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import com.example.camera.MjpegView;
import com.example.camera.MjpegView.MjpegViewThread;
import com.example.camera.ScreenShotService;

import com.example.flight_control_001.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class VideoActivity extends Activity implements SensorEventListener {

	// 各种按钮
	private Button info;
	private ImageButton btnLeft;
	private ImageButton btnRight;
	private ImageButton btnForward;
	private ImageButton btnBack;
	private ImageButton btnStop;
	private CheckBox IsGravity;
	private ImageButton btnPic;
	private ImageButton btnVideo;
	private MjpegView mv;
	// 各种数据
	public String forward;
	public String backward;
	public String turnLeft;
	public String turnRight;
	public String stop;
	public String ipAddr;
	public int port;

	public Socket s;
	public DataOutputStream dout = null;
	Handler handler;
	// 文本界面
	public TextView show;
	static ScrollView scroll;
	// 传感器管理服务
	private SensorManager mSensorManager;
	// 重力感应标记
	boolean UseGravity = false;
	// Wi-Fi管理
	private WifiManager wifi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video);
		setWifi("Enable");// 设置wifi在主线程可用
		MyActivityManager.getInstance().addActivity(this);
		// 取得二级菜单按钮info
		info = (Button) findViewById(R.id.info);
		info.setOnClickListener(new infoListener());
		// 获得视频界面
		mv = (MjpegView) findViewById(R.id.mySurfaceView1);
		// 取得方向键对象
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnLeft = (ImageButton) findViewById(R.id.btnLeft);
		btnRight = (ImageButton) findViewById(R.id.btnRight);
		btnStop = (ImageButton) findViewById(R.id.btnStop);
		// 拍照键
		btnPic = (ImageButton) findViewById(R.id.btnLen);
		btnPic.setOnClickListener(new PicListener());
		// 录像键
		btnVideo = (ImageButton) findViewById(R.id.btnLen2);
		btnVideo.setOnClickListener(new VideoListener());
		// 取得重力感应按钮
		IsGravity = (CheckBox) findViewById(R.id.checkBox1);
		// 取得ScrollView对象
		scroll = (ScrollView) findViewById(R.id.scroll);
		// 取得Textview对象
		show = (TextView) findViewById(R.id.text4);
		// 获得控制方向命令
		getDirectionCode();
		// 获取传感器管理服务
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// 获得wifi管理服务
		wifi = (WifiManager) VideoActivity.this
				.getSystemService(Context.WIFI_SERVICE);
		// 视频传输方面的代码
		if (!wifi.isWifiEnabled()) {
			Toast.makeText(VideoActivity.this, "WiFi已关闭，请打开重试",
					Toast.LENGTH_SHORT).show();
		} else {
			String URL = "http://192.168.1.150:8888?action=stream";
			System.out.println("0000");
			mv.setSource(MjpegInputStream.read(URL));// 设置数据来源，后MjpegInputStream去读取
			mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
			mv.showFps(true);
			System.out.println("0011");
		}
		// 多线程用handler
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (msg.what == 0x123) {
					// show.setMovementMethod(ScrollingMovementMethod.getInstance());
					// show.setText( msg.obj.toString());
					show.append("\n" + msg.obj.toString());
					scroll.fullScroll(View.FOCUS_DOWN);// 使文字保持显示最后一行
				}
			}
		};
		try {
			if (!wifi.isWifiEnabled()) {
				Toast.makeText(VideoActivity.this, "WiFi已关闭，请打开重试",
						Toast.LENGTH_SHORT).show();
			} else {
				s = new Socket(ipAddr, 5567);
				// 启动ClientThread来处理该线程的socket通信
				new Thread(new ClientThread(s, handler)).start();
				dout = new DataOutputStream(s.getOutputStream());
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 不使用重力感应时，才使用方向键控制方向
		if (UseGravity == false) {
			UseDirectionKey();
		}
	}

	class ForwardClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			try {
				dout.writeUTF(forward);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class LeftClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			try {
				dout.writeUTF(turnLeft);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class BackwardClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			try {
				dout.writeUTF(backward);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class RightClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			try {
				dout.writeUTF(turnRight);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class StopClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			try {
				dout.writeUTF(stop);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 重力感应checkbox被勾选的功能实现
	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		// boolean checked = ((CheckBox) view).isChecked();
		if (IsGravity.isChecked()) {
			UseGravity = true;
			// 当使用重力感应时，隐去方向键按钮
			btnForward.setVisibility(View.GONE);
			btnBack.setVisibility(View.GONE);
			btnLeft.setVisibility(View.GONE);
			btnRight.setVisibility(View.GONE);
			btnStop.setVisibility(View.GONE);
		} else {
			UseGravity = false;
			// 不使用重力感应时，隐去方向键按钮
			btnForward.setVisibility(View.VISIBLE);
			btnBack.setVisibility(View.VISIBLE);
			btnLeft.setVisibility(View.VISIBLE);
			btnRight.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.VISIBLE);
		}
	}

	// 获取上传的数据信息
	private void getDirectionCode() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("Testinfo",
				Activity.MODE_PRIVATE);
		forward = preferences.getString("focode", null);
		backward = preferences.getString("backcode", null);
		turnLeft = preferences.getString("leftcode", null);
		turnRight = preferences.getString("rightcode", null);
		stop = preferences.getString("stopcode", null);
		ipAddr = preferences.getString("ip", null);
		// port=preferences.getInt("port", 0);
	}

	class infoListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(VideoActivity.this, InfoActivity.class);
			startActivity(intent);
		}
	}

	// 设置wifi可用
	private void setWifi(String mode) {
		// TODO Auto-generated method stub
		if (mode == "Enable") {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork() // or
																			// .detectAll()
																			// for
																			// all
																			// detectable
																			// problems
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
					.penaltyLog().penaltyDeath().build());
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mSensorManager.unregisterListener(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 为系统的方向传感器注册监听器
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mSensorManager.unregisterListener(this);
		super.onStop();
	}

/*	protected void onDestroy() {
		// TODO Auto-generated method stub
		VideoActivity.this.finish();
	}*/

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		// 当重力感应checkBox选中的时候，使用传感器发送数据
		if (UseGravity == true) {
			UseSensor(event);// 传感器功能方法
		}
	}

	// 使用方向键发送数据
	private void UseDirectionKey() {
		// TODO Auto-generated method stub
		btnForward.setOnClickListener(new ForwardClickListener());
		btnBack.setOnClickListener(new BackwardClickListener());
		btnLeft.setOnClickListener(new LeftClickListener());
		btnRight.setOnClickListener(new RightClickListener());
		btnStop.setOnClickListener(new StopClickListener());
	}

	// 使用加速传感器发送数据
	private void UseSensor(SensorEvent event) {
		// TODO Auto-generated method stub
		int xaxis = (int) event.values[0];
		int yaxis = (int) event.values[1];
		int zaxis = (int) event.values[2];

		int flag = 0;

		if (zaxis >= 0 && yaxis > -7 && yaxis <= -1) {
			flag = 1;// 前进标志为1,飞机左转

		} else if (zaxis >= 0 && yaxis >= 1 && yaxis < 7)// 前进标志为2,飞机右转
		{
			flag = 2;
		}
		// 如果前后方向有信号：
		else if (zaxis >= 0 && xaxis > -7 && xaxis <= -1) {
			flag = 3;// 前进标志为3,飞机前进
		} else if (zaxis >= 0 && xaxis >= 3 && xaxis < 9) {
			flag = 4;// 前进标志为4,飞机后退
		} else {
			flag = 0;// 前进标志为0，飞机停止
		}

		switch (flag) {
		case 0:
			// btnStop.setOnClickListener(new StopClickListener());
			break;
		case 1:
			try {
				dout.writeUTF(turnLeft);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				dout.writeUTF(turnRight);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 3:
			try {
				dout.writeUTF(forward);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 4:
			try {
				dout.writeUTF(backward);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	// 拍照功能
	class PicListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			DownloadThread downloadThread = new DownloadThread();
			downloadThread.start();

		}
	}

	// 录像功能
	class VideoListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(VideoActivity.this, ScreenShotService.class);
			startService(intent);

		}
	}

	class DownloadThread extends Thread {

		public void run() {

			MjpegViewThread mvt = null;

			mvt = mv.getThread();

			if (mvt.saveImage()) {
				System.out.println("保存图片成功");
				// Toast.makeText(getApplicationContext(), "保存成功！",
				// Toast.LENGTH_SHORT).show();
			} else {
				// Toast.makeText(getApplicationContext(), "保存失败!",
				// Toast.LENGTH_LONG).show();
			}
		}
	}

	class RecordingThread extends Thread {

		public void run() {
			MjpegViewThread mvt = null;
			mvt = mv.getThread();
			if (mvt.saveVideo()) {
				System.out.println("保存视频成功");
			}
		}
	}
}
