package com.example.camera;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import com.example.flight_control_001.R;
import com.example.flight_control_001.R.id;
import com.example.flight_control_001.R.layout;


public class PlayBackActivity extends Activity {
	private PlayActivityHandler mHandler;
	private ImageView play_iv;
	private File[] arrayOfFile2;

	private File[] getFileList() {
		File localFile1 = Environment.getExternalStorageDirectory();
		File localFile2 = new File(localFile1, ScreenShotService.SSS_FILE_PATH);
		File[] arrayOfFile;
		if (!localFile2.exists()) {
			return null;
		}
		arrayOfFile = localFile2.listFiles();
		Arrays.sort(arrayOfFile, new Comparator<File>() {

			@Override
			public int compare(File paramFile1, File paramFile2) {
				long file1name = Long.valueOf(getFileNameNoEx(paramFile1
						.getName()));
				long file2name = Long.valueOf(getFileNameNoEx(paramFile2
						.getName()));
				return (int) (file1name - file2name);
			}

		});
		return arrayOfFile;
	}

	private void play() {
		arrayOfFile2 = getFileList();
		mHandler.setCount(0);
		mHandler.sendEmptyMessageDelayed(ScreenShotService.SSS_MSG_WHAT,
				ScreenShotService.SSS_DELAY_MILLIS);
	}

	public String getExtensionName(String paramString) {
		if ((paramString != null) && (paramString.length() > 0)) {
			int i = paramString.lastIndexOf('.');
			if (i > -1) {
				int j = paramString.length() + -1;
				if (i < j) {
					int k = i + 1;
					paramString = paramString.substring(k);
				}
			}
		}
		return paramString;
	}

	public String getFileNameNoEx(String paramString) {
		if ((paramString != null) && (paramString.length() > 0)) {
			int i = paramString.lastIndexOf('.');
			if (i > -1) {
				int j = paramString.length();
				if (i < j)
					paramString = paramString.substring(0, i);
			}
		}
		return paramString;
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.playback);
		this.play_iv = (ImageView) findViewById(R.id.play_iv);
		mHandler = new PlayActivityHandler();
		play();
	}

	private class PlayActivityHandler extends Handler {
		private int count = 0;
		//private Bitmap localBitmap;
		public void setCount(int vaule) {
			count = vaule;
		}

		@Override
		public void handleMessage(Message msg) {
			if (arrayOfFile2 != null && count < arrayOfFile2.length) {				
				Bitmap localBitmap = BitmapFactory
						.decodeFile(arrayOfFile2[count++].getAbsolutePath());
				play_iv.setImageBitmap(localBitmap);
				mHandler.sendEmptyMessageDelayed(
						ScreenShotService.SSS_MSG_WHAT,
						ScreenShotService.SSS_DELAY_MILLIS);				
			}
		}

	}
}

