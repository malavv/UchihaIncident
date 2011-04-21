package com.ninja.exMenu;

import android.R.string;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * Le thread contenant toute la logique d'application.
 */
public class PlayContentThread extends Thread {

	private static int mLastX = 0;
	private static int mLastY = 0;
	
	
	// Un list de coordonnées.
	
	// La liste des positions des trois points.
	
	private Bitmap mBackgroundImage;
	
	private int mCanvasHeight = 1;
	private int mCanvasWidth = 1;
	
	private Handler mMsgHandler;
	private SurfaceHolder mSurfaceHolder;
	
	private Paint mLinePaint;
	
	private Context mContext;
	
    private long mLastTime;
    
    private int mMode = STATE_BEFORE;
    
    private boolean mRun = false;
    
    public static final int STATE_BEFORE = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_AFTER = 3;
	
	public PlayContentThread(SurfaceHolder surface, Context context, Handler msgHandler) {
		mSurfaceHolder = surface;
		mContext = context;
		mMsgHandler = msgHandler;
		
		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setARGB(255, 255, 255, 255);
	}
	
	public void doStart() {
	
	  synchronized (mSurfaceHolder) {
		  mLastTime = System.currentTimeMillis();
		  setState(STATE_BEFORE);
	  }
	}
	
	public void pause() {}
	
	@Override
	public void run() {
		while (mRun) {
			Canvas c = null;
			try {
			  c = mSurfaceHolder.lockCanvas(null);
			  synchronized (mSurfaceHolder) {
				doDraw(c);
			  }
			} finally {
				if (c != null)
					mSurfaceHolder.unlockCanvasAndPost(c);
			}
		}
	}
	
	public void setRunning(boolean r) { mRun = r; }
	
	public void doDraw(Canvas c) {
		
		long timeDiff = System.currentTimeMillis() - mLastTime;
		mLastTime = System.currentTimeMillis();
		
		long fps = 1000 / timeDiff;
		
		String s = "FPS : " + Long.toString(fps);
		
		synchronized (mSurfaceHolder) {
			
			c.drawColor(Color.BLACK);
			
			c.drawText(s, 5, mCanvasHeight - 5, mLinePaint);
		}
	}
	
	public void setState(int mode) {
	  synchronized (mSurfaceHolder) {
		  mMode = mode;
	  }
	}
	
	public void setSurfaceSize(int width, int height) {
		synchronized (mSurfaceHolder) {
			mCanvasHeight = height;
			mCanvasWidth = width;
		}
	}
	
	public void unpause() {
	  synchronized (mSurfaceHolder) {
		mLastTime = System.currentTimeMillis() + 100;
	  }
	}
}
