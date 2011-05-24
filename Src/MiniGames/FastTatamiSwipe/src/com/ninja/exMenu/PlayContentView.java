package com.ninja.exMenu;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

/**
 * Vue centrale utilisé par le Mini-jeu, elle dérive de la classe surface qui
 * donne une facon facile de s'occuper de la gestion des événements et de
 * pouvoir dessiner.
 */
public class PlayContentView extends SurfaceView implements
    SurfaceHolder.Callback {

  /** Le thread sous-jacent à l'application. */
  private PlayContentThread thread;

  public TextView mTextStatus;

  public PlayContentView(Context context, AttributeSet attrs) {
    super(context, attrs);

    SurfaceHolder h = getHolder();
    h.addCallback(this);

    thread = new PlayContentThread(h, context, new Handler() {
      @Override
      public void handleMessage(Message m) {
        mTextStatus.setText(m.getData().getString("text"));
      }
    }, PlayContent.getkDifficulty());

    setFocusable(true);
  }

  public PlayContentThread getThread() {
    return thread;
  }

  /**
   * La surface sur laquelle ont dessine a été modifiée. {@link SurfaceHolder}
   */
  public void surfaceChanged(SurfaceHolder holder, int format, int width,
      int height) {
    thread.setSurfaceSize(width, height);
  }

  /**
   * La surface sur laquelle ont dessine viens c'être créé et est prête à être
   * utilisée. {@link SurfaceHolder}
   */
  public void surfaceCreated(SurfaceHolder holder) {
    thread.setRunning(true);
    thread.start();

    mTextStatus.setText("Player 1");
  }

  /**
   * La surface sur laquelle ont dessine sera détruite et nous devons terminer.
   * {@link SurfaceHolder}
   */
  public void surfaceDestroyed(SurfaceHolder holder) {
    boolean retry = true;
    thread.setRunning(false);
    while (retry) {
      try {
        thread.join();
        retry = false;
      } catch (InterruptedException e) {
      }
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
