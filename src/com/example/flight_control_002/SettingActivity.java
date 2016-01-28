package com.example.flight_control_002;

import com.example.flight_control_001.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends Activity {

	public EditText ipAddress;
	public EditText port;
	public EditText leftCode;
	public EditText rightCode;
	public EditText foCode;
	public EditText backCode;
	public EditText stopCode;
	public Button OKbutton;
	public Button Cancelbutton;
	public static String forward;
	public static String backward;
	public static String turnLeft;
	public static String turnRight;
	public static String stop;
	public static String ipAddr;
	public static int portNum;

	SharedPreferences preferences;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		MyActivityManager.getInstance().addActivity(this);
		// 取得EditText对象
		foCode = (EditText) findViewById(R.id.editText1);
		backCode = (EditText) findViewById(R.id.editText2);
		leftCode = (EditText) findViewById(R.id.editText3);
		rightCode = (EditText) findViewById(R.id.editText4);
		stopCode = (EditText) findViewById(R.id.editText6);
		ipAddress = (EditText) findViewById(R.id.editText7);
		port = (EditText) findViewById(R.id.editText8);
		
		preferences = getSharedPreferences("Testinfo", Activity.MODE_PRIVATE);
		//获取上传的数据信息
		foCode.setText(preferences.getString("focode", null));
		backCode.setText(preferences.getString("backcode", null));
		leftCode.setText(preferences.getString("leftcode", null));
		rightCode.setText(preferences.getString("rightcode", null));
		stopCode.setText(preferences.getString("stopcode", null));
		ipAddress.setText(preferences.getString("ip", null));

		OKbutton = (Button) findViewById(R.id.okbutton);
		OKbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent=new Intent();
				// intent.setClass(secSettingActivity.this, MainActivity.class);

				// 取得EditText框中的数值
				forward = foCode.getText().toString();
				backward = backCode.getText().toString();
				turnLeft = leftCode.getText().toString();
				turnRight = rightCode.getText().toString();
				stop = stopCode.getText().toString();
				ipAddr = ipAddress.getText().toString();
				String portString = port.getText().toString();
				portNum = Integer.parseInt(portString);

				// 获取启动该activity之前的activity对应的Intent
				Intent data = getIntent();
				data.setClass(SettingActivity.this, MainActivity.class);
				startActivity(data);
				// 结束当前activity
				SettingActivity.this.finish();
			}
		});

		Cancelbutton = (Button) findViewById(R.id.cancelbutton);
		Cancelbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, MainActivity.class);
				startActivity(intent);
				SettingActivity.this.finish();
			}
		});

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// 获取只能被本应由程序读、写的SharedPreferences对象
		preferences = getSharedPreferences("Testinfo", Activity.MODE_PRIVATE);
		editor = preferences.edit();

		editor.putString("focode", forward);
		editor.putString("backcode", backward);
		editor.putString("leftcode", turnLeft);
		editor.putString("rightcode", turnRight);
		editor.putString("stopcode", stop);
		editor.putString("ip", ipAddr);
		editor.putInt("port", portNum);
		editor.commit();

		finish();
	}

}
