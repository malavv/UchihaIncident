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
 * Vue centrale utilisé par le Mini-jeu, elle dérive de la classe surface
 * qui donne une facon facile de s'occuper de la gestion des événements et
 * de pouvoir dessiner.
 */
public class PlayContentView extends SurfaceView implements SurfaceHolder.Callback {

  /** Zone de texte nous permettant d'afficher des messages à l'utilisateur. */
	private TextView mStatusText;
	
	/** Le thread sous-jacent à l'application. */
	private PlayContentThread thread;
	
	public PlayContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		SurfaceHolder h = getHolder();
		h.addCallback(this);
		
		thread = new PlayContentThread(h, context, new Handler() {
			@Override
			public void handleMessage(Message m) {
				mStatusText.setText(m.getData().getString("t"));
				mStatusText.setVisibility(m.getData().getInt("v"));
			}
		});
		
		setFocusable(true);
	}
	
	public PlayContentThread getThread() { return thread; }

  /**
   * Implémentation de l'interface.
   */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);		
	}

	public void surfaceCreated(SurfaceHolder holder) {
    thread.setRunning(true);
    thread.start();
	}

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
	
	/**
	 * Handle de touch event.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	  thread.doTouch(event);
	  return true;
	}
}
