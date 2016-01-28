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

	// ���ְ�ť
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
	// ��������
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
	// �ı�����
	public TextView show;
	static ScrollView scroll;
	// �������������
	private SensorManager mSensorManager;
	// ������Ӧ���
	boolean UseGravity = false;
	// Wi-Fi����
	private WifiManager wifi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video);
		setWifi("Enable");// ����wifi�����߳̿���
		MyActivityManager.getInstance().addActivity(this);
		// ȡ�ö����˵���ťinfo
		info = (Button) findViewById(R.id.info);
		info.setOnClickListener(new infoListener());
		// �����Ƶ����
		mv = (MjpegView) findViewById(R.id.mySurfaceView1);
		// ȡ�÷��������
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnLeft = (ImageButton) findViewById(R.id.btnLeft);
		btnRight = (ImageButton) findViewById(R.id.btnRight);
		btnStop = (ImageButton) findViewById(R.id.btnStop);
		// ���ռ�
		btnPic = (ImageButton) findViewById(R.id.btnLen);
		btnPic.setOnClickListener(new PicListener());
		// ¼���
		btnVideo = (ImageButton) findViewById(R.id.btnLen2);
		btnVideo.setOnClickListener(new VideoListener());
		// ȡ��������Ӧ��ť
		IsGravity = (CheckBox) findViewById(R.id.checkBox1);
		// ȡ��ScrollView����
		scroll = (ScrollView) findViewById(R.id.scroll);
		// ȡ��Textview����
		show = (TextView) findViewById(R.id.text4);
		// ��ÿ��Ʒ�������
		getDirectionCode();
		// ��ȡ�������������
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// ���wifi�������
		wifi = (WifiManager) VideoActivity.this
				.getSystemService(Context.WIFI_SERVICE);
		// ��Ƶ���䷽��Ĵ���
		if (!wifi.isWifiEnabled()) {
			Toast.makeText(VideoActivity.this, "WiFi�ѹرգ��������",
					Toast.LENGTH_SHORT).show();
		} else {
			String URL = "http://192.168.1.150:8888?action=stream";
			System.out.println("0000");
			mv.setSource(MjpegInputStream.read(URL));// ����������Դ����MjpegInputStreamȥ��ȡ
			mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
			mv.showFps(true);
			System.out.println("0011");
		}
		// ���߳���handler
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (msg.what == 0x123) {
					// show.setMovementMethod(ScrollingMovementMethod.getInstance());
					// show.setText( msg.obj.toString());
					show.append("\n" + msg.obj.toString());
					scroll.fullScroll(View.FOCUS_DOWN);// ʹ���ֱ�����ʾ���һ��
				}
			}
		};
		try {
			if (!wifi.isWifiEnabled()) {
				Toast.makeText(VideoActivity.this, "WiFi�ѹرգ��������",
						Toast.LENGTH_SHORT).show();
			} else {
				s = new Socket(ipAddr, 5567);
				// ����ClientThread��������̵߳�socketͨ��
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
		// ��ʹ��������Ӧʱ����ʹ�÷�������Ʒ���
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

	// ������Ӧcheckbox����ѡ�Ĺ���ʵ��
	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		// boolean checked = ((CheckBox) view).isChecked();
		if (IsGravity.isChecked()) {
			UseGravity = true;
			// ��ʹ��������Ӧʱ����ȥ�������ť
			btnForward.setVisibility(View.GONE);
			btnBack.setVisibility(View.GONE);
			btnLeft.setVisibility(View.GONE);
			btnRight.setVisibility(View.GONE);
			btnStop.setVisibility(View.GONE);
		} else {
			UseGravity = false;
			// ��ʹ��������Ӧʱ����ȥ�������ť
			btnForward.setVisibility(View.VISIBLE);
			btnBack.setVisibility(View.VISIBLE);
			btnLeft.setVisibility(View.VISIBLE);
			btnRight.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.VISIBLE);
		}
	}

	// ��ȡ�ϴ���������Ϣ
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

	// ����wifi����
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
		// Ϊϵͳ�ķ��򴫸���ע�������
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
		// ��������ӦcheckBoxѡ�е�ʱ��ʹ�ô�������������
		if (UseGravity == true) {
			UseSensor(event);// ���������ܷ���
		}
	}

	// ʹ�÷������������
	private void UseDirectionKey() {
		// TODO Auto-generated method stub
		btnForward.setOnClickListener(new ForwardClickListener());
		btnBack.setOnClickListener(new BackwardClickListener());
		btnLeft.setOnClickListener(new LeftClickListener());
		btnRight.setOnClickListener(new RightClickListener());
		btnStop.setOnClickListener(new StopClickListener());
	}

	// ʹ�ü��ٴ�������������
	private void UseSensor(SensorEvent event) {
		// TODO Auto-generated method stub
		int xaxis = (int) event.values[0];
		int yaxis = (int) event.values[1];
		int zaxis = (int) event.values[2];

		int flag = 0;

		if (zaxis >= 0 && yaxis > -7 && yaxis <= -1) {
			flag = 1;// ǰ����־Ϊ1,�ɻ���ת

		} else if (zaxis >= 0 && yaxis >= 1 && yaxis < 7)// ǰ����־Ϊ2,�ɻ���ת
		{
			flag = 2;
		}
		// ���ǰ�������źţ�
		else if (zaxis >= 0 && xaxis > -7 && xaxis <= -1) {
			flag = 3;// ǰ����־Ϊ3,�ɻ�ǰ��
		} else if (zaxis >= 0 && xaxis >= 3 && xaxis < 9) {
			flag = 4;// ǰ����־Ϊ4,�ɻ�����
		} else {
			flag = 0;// ǰ����־Ϊ0���ɻ�ֹͣ
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

	// ���չ���
	class PicListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			DownloadThread downloadThread = new DownloadThread();
			downloadThread.start();

		}
	}

	// ¼����
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
				System.out.println("����ͼƬ�ɹ�");
				// Toast.makeText(getApplicationContext(), "����ɹ���",
				// Toast.LENGTH_SHORT).show();
			} else {
				// Toast.makeText(getApplicationContext(), "����ʧ��!",
				// Toast.LENGTH_LONG).show();
			}
		}
	}

	class RecordingThread extends Thread {

		public void run() {
			MjpegViewThread mvt = null;
			mvt = mv.getThread();
			if (mvt.saveVideo()) {
				System.out.println("������Ƶ�ɹ�");
			}
		}
	}
}
