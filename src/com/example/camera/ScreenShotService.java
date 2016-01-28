package com.example.camera;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;


public class ScreenShotService extends Service {
	public static final boolean SSS_DEFAULT_START = false;
	public static final boolean SSS_START = true;
	public static final boolean SSS_STOP = false;
	public static final String TRANSCRIBE_TAG = "TRANSCRIBE_TAG";
	public static final long SSS_DELAY_MILLIS = 100L;
	public static final String SSS_FILE_PATH = "/luzhi/";
	public static final int SSS_MSG_WHAT = 256;
	private ScreenShotServiceHandler handler;
	private boolean isStart = SSS_STOP;


	private View[] getWindowDecorViews() throws Exception {
		Class<?> windowManager = Class
				.forName("android.view.WindowManagerImpl");
		Field viewsField = windowManager.getDeclaredField("mViews");
		Field instanceField = windowManager.getDeclaredField("mWindowManager");
		viewsField.setAccessible(true);
		instanceField.setAccessible(true);
		Object instance = instanceField.get(null);
		View[] viewarray = (View[]) viewsField.get(instance);


		if ((viewarray == null) || (viewarray.length < 0)) {
			return null;
		}
		int i = 0;
		View[] views = new View[viewarray.length];
		for (View v : viewarray) {
			views[i++] = v;
		}


		int[] arrayOfInt = new int[2];
		View localView;
		int j = 0;
		int length = views.length;
		for (i = 0; i < length; i++) {
			localView = views[i];
			localView.getLocationOnScreen(arrayOfInt);
			if ((arrayOfInt[0] > 0) || (arrayOfInt[1] > 0)) {
				for (j = i + 1; j < views.length; j++) {
					views[j - 1] = views[j];
				}
				views[views.length - 1] = localView;
				length--;
				i--;
			}
		}
		return views;
	}


	private void savePic(Bitmap paramBitmap, String paramString) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(paramString);
			if (null != fos) {
				paramBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void screenShot() {
		try {
			View[] arrayOfView = getWindowDecorViews();
			Bitmap localBitmap = view2Bitmap(arrayOfView);
			new saveFileThread(localBitmap).start();
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}


	private Bitmap view2Bitmap(View paramView) {
		System.out.println("saveFileThread view2Bitmap(View view)");
		paramView.setDrawingCacheEnabled(true);
		paramView.buildDrawingCache();
		Bitmap localBitmap1 = paramView.getDrawingCache();
		Bitmap localBitmap2 = null;
		if (localBitmap1 != null) {
			localBitmap2 = Bitmap.createBitmap(localBitmap1);
		}
		paramView.destroyDrawingCache();
		return localBitmap2;
	}


	private Bitmap view2Bitmap(View[] paramArrayOfView) {
		Canvas localCanvas = null;
		Paint localPaint = null;
		Bitmap newbitmap = null;
		int[] arrayOfInt = new int[2];


		for (View localView : paramArrayOfView) {
			Bitmap localBitmap = view2Bitmap(localView);
			localView.getLocationOnScreen(arrayOfInt);
			if (localBitmap != null) {
				if (localCanvas == null) {
					newbitmap = Bitmap.createBitmap(localView.getWidth(),
							localView.getHeight(), Config.ARGB_8888);// 创建一个新的和SRC
					localCanvas = new Canvas(newbitmap);
					localPaint = new Paint();
				}
				localCanvas.drawBitmap(localBitmap, arrayOfInt[0],
						arrayOfInt[1], localPaint);
				localBitmap.recycle();
				localBitmap = null;
			}
		}
		if (localCanvas != null) {
			localCanvas.save(Canvas.ALL_SAVE_FLAG);// 保存
			localCanvas.restore();// 存储
		}
		return newbitmap;
	}


	public IBinder onBind(Intent paramIntent) {
		return null;
	}


	public void onCreate() {
		super.onCreate();
		System.out.println("ScreenShotService onCreate");
		handler = new ScreenShotServiceHandler();
	}





	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		super.onStartCommand(intent, flags, startId);
		System.out.println("ScreenShotService onStart -----start----");  
/*        String str = TRANSCRIBE_TAG;  
        boolean localstart = intent  
                .getBooleanExtra(str, SSS_DEFAULT_START);  */
		boolean localstart = true;
        System.out.println(localstart); 
        System.out.println(this.isStart); 
        if (this.isStart != localstart) {  
            this.isStart = localstart;  
            if (this.isStart == SSS_START) {  
                handler.sendEmptyMessageDelayed(SSS_MSG_WHAT, SSS_DELAY_MILLIS);  
            } else {  
                handler.removeMessages(SSS_MSG_WHAT);  
            }  
        }  
        return START_STICKY;
	}


	@SuppressLint({ "HandlerLeak", "HandlerLeak" })
	private class ScreenShotServiceHandler extends Handler {
		public void handleMessage(Message paramMessage) {
			if (paramMessage.what == SSS_MSG_WHAT) {
				ScreenShotService.this.screenShot();
				System.out.println("start screenshot");
				if (ScreenShotService.this.isStart) {
					ScreenShotService.this.handler.sendEmptyMessageDelayed(
							SSS_MSG_WHAT, SSS_DELAY_MILLIS);
				}
			}
		}
	}


	private class saveFileThread extends Thread {
		private Bitmap mbitmap;


		public saveFileThread(Bitmap arg2) {
			this.mbitmap = arg2;
		}


		public void run() {
			if (this.mbitmap == null) {
				System.out.println("saveFileThread run mbitmap is null ");
			}
			File localFile1 = Environment.getExternalStorageDirectory();
			File localFile2 = new File(localFile1, SSS_FILE_PATH);
			if (!localFile2.exists()) {
				localFile2.mkdirs();
			}
			String filename = localFile2.getAbsolutePath() + "/"
					+ System.currentTimeMillis() + ".png";
			System.out.println("saveFileThread run picPath = ");
			ScreenShotService.this.savePic(mbitmap, filename);
			this.mbitmap.recycle();
			this.mbitmap = null;
		}
	}
}

