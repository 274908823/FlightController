package com.example.camera;

import java.io.*;
import java.net.*;

import org.apache.http.util.ByteArrayBuffer;




import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MjpegView extends SurfaceView implements SurfaceHolder.Callback {
    public final static int POSITION_UPPER_LEFT  = 9;
    public final static int POSITION_UPPER_RIGHT = 3;
    public final static int POSITION_LOWER_LEFT  = 12;
    public final static int POSITION_LOWER_RIGHT = 6;

    public final static int SIZE_STANDARD   = 1; 
    public final static int SIZE_BEST_FIT   = 4;
    public final static int SIZE_FULLSCREEN = 8;
    
    private MjpegViewThread thread;
    private MjpegInputStream mIn = null;    
    private boolean showFps = false;
    private boolean mRun = false;
    private boolean surfaceDone = false;    
    private Paint overlayPaint;
    private int overlayTextColor;
    private int overlayBackgroundColor;
    private int ovlPos;
    private int dispWidth;
    private int dispHeight;
    private int displayMode;

    public class MjpegViewThread extends Thread {
        private SurfaceHolder mSurfaceHolder;
        private int frameCounter = 0;
        private long start;
        private Bitmap ovl;
         
        public MjpegViewThread(SurfaceHolder surfaceHolder, Context context) { mSurfaceHolder = surfaceHolder; }

        private Rect destRect(int bmw, int bmh) {
            int tempx;
            int tempy;
            if (displayMode == MjpegView.SIZE_STANDARD) {
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegView.SIZE_BEST_FIT) {
                float bmasp = (float) bmw / (float) bmh;
                bmw = dispWidth;
                bmh = (int) (dispWidth / bmasp);
                if (bmh > dispHeight) {
                    bmh = dispHeight;
                    bmw = (int) (dispHeight * bmasp);
                }
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegView.SIZE_FULLSCREEN) return new Rect(0, 0, dispWidth, dispHeight);
            return null;
        }
         
        public void setSurfaceSize(int width, int height) {
            synchronized(mSurfaceHolder) {
                dispWidth = width;
                dispHeight = height;
            }
        }
         
        private Bitmap makeFpsOverlay(Paint p, String text) {
            Rect b = new Rect();
            p.getTextBounds(text, 0, text.length(), b);
            int bwidth  = b.width()+2;
            int bheight = b.height()+2;
            Bitmap bm = Bitmap.createBitmap(bwidth, bheight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            
            p.setColor(overlayBackgroundColor);
            c.drawRect(0, 0, bwidth, bheight, p);
            p.setColor(overlayTextColor);
            c.drawText(text, -b.left+1, (bheight/2)-((p.ascent()+p.descent())/2)+1, p);
            return bm;        	 
        }
        
       public boolean saveImage()  {
			
			String fileName = String.valueOf(System.currentTimeMillis());			
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Flight photo" + File.separator;
			Bitmap bm;
			System.out.println(path);
			try {
		     
			bm = mIn.readMjpegFrame();
            File dirFile = new File(path);    
			System.out.println(dirFile.mkdirs());    
	        
	        File photofile = new File(path + fileName+".jpeg"); 
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(photofile));    
	        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);    
	        bos.flush();    
	        bos.close();    
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
       
       public boolean saveVideo()  {
			
			String fileName = String.valueOf(System.currentTimeMillis());
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Flight Video" + File.separator;

			try {
				
				String URL = "http://192.168.1.150:8888?action=stream";
				InputStream is = MjpegInputStream.read(URL);
	           /* BufferedInputStream bis = new BufferedInputStream(is);   
	            // ÓÃByteArrayBuffer»º´æ   
	            ByteArrayBuffer baf = new ByteArrayBuffer(50);   
	            int current = 0;   
	            while ((current = bis.read()) != -1) {   
	                baf.append((byte) current);   
	            }   */
		     
            File dirFile = new File(path);    
			System.out.println(dirFile.mkdirs());   
	        File Videofile = new File(path + fileName + ".avi"); 
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(Videofile));    
	        byte buffer [] = new byte[4 * 1024];
			int temp;
			while((temp=is.read(buffer)) != -1){
				bos.write(buffer,0,temp);
			}
			bos.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
       
       
        public void run() {
            start = System.currentTimeMillis();
            PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
            Bitmap bm;
            int width;
            int height;
            Rect destRect;
            Canvas c = null;
            Paint p = new Paint();
            String fps = "";
            while (mRun) {
                if(surfaceDone) {
                    try {
                        c = mSurfaceHolder.lockCanvas();
                        synchronized (mSurfaceHolder) {
                            try {
                                bm = mIn.readMjpegFrame();
                                destRect = destRect(bm.getWidth(),bm.getHeight());
                                c.drawColor(Color.BLACK);
                                c.drawBitmap(bm, null, destRect, p);
                                if(showFps) {
                                    p.setXfermode(mode);
                                    if(ovl != null) {
                                    	height = ((ovlPos & 1) == 1) ? destRect.top : destRect.bottom-ovl.getHeight();
                                    	width  = ((ovlPos & 8) == 8) ? destRect.left : destRect.right -ovl.getWidth();
                                        c.drawBitmap(ovl, width, height, null);
                                    }
                                    p.setXfermode(null);
                                    frameCounter++;
                                    if((System.currentTimeMillis() - start) >= 1000) {
                                        fps = String.valueOf(frameCounter)+"fps";
                                        frameCounter = 0; 
                                        start = System.currentTimeMillis();
                                        ovl = makeFpsOverlay(overlayPaint, fps);
                                    }
                                }
                            } catch (IOException e) {}
                        }
                    } finally { if (c != null) mSurfaceHolder.unlockCanvasAndPost(c); }
                }
            }
        }
    }

    private void init(Context context) {
    	System.out.println("2222");
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        thread = new MjpegViewThread(holder, context);
        setFocusable(true);
        overlayPaint = new Paint();
        overlayPaint.setTextAlign(Paint.Align.LEFT);
        overlayPaint.setTextSize(12);
        overlayPaint.setTypeface(Typeface.DEFAULT);
        
        overlayTextColor = Color.WHITE;
        overlayBackgroundColor = Color.BLACK;
        ovlPos = MjpegView.POSITION_LOWER_RIGHT;
        displayMode = MjpegView.SIZE_STANDARD;
        dispWidth = getWidth();
        dispHeight = getHeight();
    }
    
    public void startPlayback() { 
        if(mIn != null) {
        	
            mRun = true;
            thread.start();    		
        }
    }
    
    public void stopPlayback() { 
        mRun = false;
        boolean retry = true;
        while(retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
    }

    public MjpegView(Context context, AttributeSet attrs) { super(context, attrs); init(context); }
    public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) { thread.setSurfaceSize(w, h); }

    public void surfaceDestroyed(SurfaceHolder holder) { 
        surfaceDone = false; 
        stopPlayback(); 
    }
	public MjpegViewThread getThread() {
		return thread;
	}
	
    public MjpegView(Context context) { super(context); init(context); }    
    public void surfaceCreated(SurfaceHolder holder) { surfaceDone = true; }
    public void showFps(boolean b) { showFps = b; }
    public void setSource(MjpegInputStream source) { mIn = source; startPlayback();}
    public void setOverlayPaint(Paint p) { overlayPaint = p; }
    public void setOverlayTextColor(int c) { overlayTextColor = c; }
    public void setOverlayBackgroundColor(int c) { overlayBackgroundColor = c; }
    public void setOverlayPosition(int p) { ovlPos = p; }
    public void setDisplayMode(int s) { displayMode = s; }
}