package com.ninja.exMenu;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;


/**
 * Vue centrale utilisé par le Play Content, c'est dans cette vue que se fera l'affichage et tout.
 */
public class PlayContentView extends SurfaceView implements SurfaceHolder.Callback {

	private TextView mStatusText;
	
	private PlayContentThread thread;
	
	public PlayContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		SurfaceHolder h = getHolder();
		h.addCallback(this);
		
		this.
		
		thread = new PlayContentThread(h, context, new Handler() {
			@Override
			public void handleMessage(Message m) {
				mStatusText.setText(m.getData().getString("t"));
				mStatusText.setVisibility(m.getData().getInt("v"));
			}
		});
		
		setFocusable(true);
	}
	
	public PlayContentThread getThread() {
	  return thread;
	}
	
    public void setTextView(TextView textView) {
        mStatusText = textView;
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	  thread.doTouch(event);
	  return true;
	}
}
