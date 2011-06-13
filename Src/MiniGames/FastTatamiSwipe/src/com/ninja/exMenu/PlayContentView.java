package com.ninja.exMenu;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Vue centrale utilisé par le Mini-jeu, elle dérive de la classe surface qui
 * donne une facon facile de s'occuper de la gestion des événements et de
 * pouvoir dessiner.
 */
public class PlayContentView extends SurfaceView 
                             implements SurfaceHolder.Callback {
  
  /** Envoyé si le message demande un ajustement de status. */
  public static final int kStatusUpdate = 0;
  /** Envoyé si le message demande d'afficher le menu. */
  public static final int kShowMenu = 1;
  
  /** Le thread sous-jacent à l'application. */
  private PlayContentThread thread;
  /** Le text de status au milieu de l'écran. */
  public TextView mTextStatus;
  /** Le menu qui apparait à la fin de l'application. */
  public Dialog alert;
  
  public PlayContentView(Context context, AttributeSet attrs) {
    super(context, attrs);
    
    getHolder().addCallback(this);
    thread = new PlayContentThread(getHolder(), getContext(), new Handler() {
      @Override
      public void handleMessage(Message m) {
        int mode = m.getData().getInt("mode");
        switch (mode) {
          case kStatusUpdate:
            mTextStatus.setText(m.getData().getString("status"));
            break;
          case kShowMenu:
          {
            boolean hasWon = m.getData().getBoolean("hasWon");
            int time = m.getData().getInt("time");
            ShowEndGameMenu(hasWon, time);
          }
            break;
        }
      }
    }, GameContext.GetCurrent());
    setFocusable(true);
  }
  
  private void ShowEndGameMenu(boolean hasWon, int time) {
    GameContext.Save();
    
    String msg;
    Opponent op = GameContext.GetCurrent();
    if (hasWon) {
      alert.setTitle(R.string.end_victory);
      msg = getResources().getString(R.string.end_vic_msg);
    } else {
      alert.setTitle(R.string.end_defeat);
      msg = getResources().getString(R.string.end_def_msg);
    }
    
    ((TextView)alert.findViewById(R.id.text)).setText(msg.replace("%s", op.getSenseiName()));
    float timeS = time / 1000.0f;
    ((TextView)alert.findViewById(R.id.time)).setText(Float.toString(timeS) + " s");
    alert.show();
  }
  
  public void SetActivity(final PlayContent content) {
    alert = new Dialog(getContext());
    alert.setContentView(R.layout.end_game_menu);
    
    Button btnRetry = (Button)alert.findViewById(R.id.btn_retry);
    btnRetry.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        content.Retry();
        alert.cancel();
      }
    });
    
    Button btnMenu = (Button)alert.findViewById(R.id.btn_menu);
    btnMenu.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        content.BackToMenu();
        alert.cancel();
      }
    });
    
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
