package com.example.flight_control_002;

import com.example.flight_control_001.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ConnectActivity extends Activity{
	
	private Button con;
	private Button add;
	private Button modify;
	private Button delete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect);
		MyActivityManager.getInstance().addActivity(this);
		con = (Button) findViewById(R.id.BtnConnect);
		con.setOnClickListener(new conListener());
		
		add = (Button) findViewById(R.id.BtnNew);
		add.setOnClickListener(new addListener());
		
	}

	class conListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent=new Intent();
			intent.setClass(ConnectActivity.this, VideoActivity.class);
			startActivity(intent);
			
		}
		
	}
	
	class addListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent=new Intent();
			intent.setClass(ConnectActivity.this, AddActivity.class);
			startActivity(intent);
			
		}
		
	}
	
}
