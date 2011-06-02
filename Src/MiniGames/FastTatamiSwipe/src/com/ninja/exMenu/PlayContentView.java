package com.ninja.exMenu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
  
  public AlertDialog alert;

  private PlayContent activity;
  
  public PlayContentView(Context context, AttributeSet attrs) {
    super(context, attrs);
    
    getHolder().addCallback(this);
    thread = new PlayContentThread(getHolder(), getContext(), new Handler() {
      @Override
      public void handleMessage(Message m) {
        if (m.getData().getString("text").equals("Mudkipz"))  alert.show();
        else
          mTextStatus.setText(m.getData().getString("text"));
      }
    }, PlayContent.GetCurrentOpponent());
    setFocusable(true);
  }
  
  public void SetActivity(final PlayContent content) {
    activity = content;
    
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setMessage("Pleasssse Choose wisely.")
         .setCancelable(false)
         .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
               content.Retry();
               dialog.cancel();
             }
         })
         .setNegativeButton("Menu", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
               content.BackToMenu();
               dialog.cancel();
             }
         });
    alert = builder.create();
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
