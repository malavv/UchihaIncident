package com.ninja.exMenu;

import java.util.ArrayList;
import java.util.LinkedList;

import android.R.string;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * Le thread contenant toute la logique d'application.
 */
public class PlayContentThread extends Thread {
	
	class Point {
	  public float X = 0;
	  public float Y = 0;
	  public Point(float f, float g) { X = f; Y = g; }
	}
	
	// Un list de coordonnées.
	LinkedList<Point> coords;
	
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
    
    private boolean mIsTouch = false;
	
	public PlayContentThread(SurfaceHolder surface, Context context, Handler msgHandler) {
		mSurfaceHolder = surface;
		mContext = context;
		mMsgHandler = msgHandler;
		
		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setARGB(255, 255, 255, 255);
		
		coords = new LinkedList<Point>();
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
		
		String isTouchOn = (mIsTouch)? " ON" : " OFF";
		
		String s = "FPS : " + Long.toString(fps) + isTouchOn;
		
		synchronized (mSurfaceHolder) {
			
			c.drawColor(Color.BLACK);
			
			c.drawText(s, 5, mCanvasHeight - 5, mLinePaint);
			
			if (coords.size() > 2) {
			  for (int i = coords.size() - 2, j = coords.size() - 1; i > 0; i--, j--) {
				  c.drawLine(coords.get(j).X, coords.get(j).Y, coords.get(i).X, coords.get(i).Y, mLinePaint);
			  }
			}
		}
	}
	
	public void doTouch(MotionEvent e) {
		coords.add(new Point(e.getX(), e.getY()));
		if (coords.size() > 20)  coords.poll();
	}
	
	public void SetTouch(boolean isOn) {
		mIsTouch = isOn;
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
