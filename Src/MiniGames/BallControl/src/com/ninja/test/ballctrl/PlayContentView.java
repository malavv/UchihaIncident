package com.ninja.test.ballctrl;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PlayContentView extends SurfaceView implements SurfaceHolder.Callback{

   public PlayContentThread mContentThread;
   
   /** Le menu qui apparait à la fin de l'application. */
   public Dialog alert;
   
   public static Context sContext;
   
   public PlayContentView(Context context, AttributeSet attrs) {
      super(context, attrs);
      
      sContext = context;
  	
      SurfaceHolder holder = getHolder();
      holder.addCallback(this);
      mContentThread = new PlayContentThread(holder, new Handler() {
          @Override
          public void handleMessage(Message m) {
        	int mode = m.getData().getInt("mode");
        	switch(mode) {
        	case Global.MSG_NORMAL:
	        	ShowEndNormal(m.getData().getDouble("time"));
	        	break;
        	case Global.MSG_TIMED:
        		ShowEndTimed(m.getData().getInt("shuriken"), m.getData().getInt("collisions"));
	        	break;
        	case Global.MSG_SURVIVAL:
	        	ShowEndSurvival(m.getData().getInt("shuriken"), m.getData().getDouble("time"));
	        	break;
        	}
          }
      	}
      );
      
      setFocusable(true);
   }

   protected void ShowEndNormal(double time) {
	  String msg;
	   
      alert.setTitle(R.string.endgame_win);
      msg = getResources().getString(R.string.end_win_msg);

      ((TextView)alert.findViewById(R.id.text)).setText(msg);
      ((TextView)alert.findViewById(R.id.time)).setText(Double.toString(time) + " s");
      alert.show();
   }

   protected void ShowEndTimed(int shuriken, int collision) {
	  String msg;
	   
      alert.setTitle(R.string.endgame_win);
      msg = "You collected " + Integer.toString(shuriken) + " shuriken\nand collided only";

      ((TextView)alert.findViewById(R.id.text)).setText(msg);
      ((TextView)alert.findViewById(R.id.time)).setText(Integer.toString(collision) + " time(s)");
      alert.show();
   }

   protected void ShowEndSurvival(int shuriken, double time) {
	  String msg;
	   
      alert.setTitle(R.string.endgame_win);
      msg = "You collected " + Integer.toString(shuriken) + " shuriken\nwithin";

      ((TextView)alert.findViewById(R.id.text)).setText(msg);
      ((TextView)alert.findViewById(R.id.time)).setText(Double.toString(time) + " s");
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

   public PlayContentThread getThread() { return mContentThread; }

   @Override
   public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      mContentThread.setSurfaceSize(width, height);   
      
   }

   @Override
   public void surfaceCreated(SurfaceHolder holder) {
		
      mContentThread.setRunning(true);
      mContentThread.start();
      
   }

   @Override
   public void surfaceDestroyed(SurfaceHolder holder) {
      boolean retry = true;
      mContentThread.setRunning(false);
      mContentThread.stopGame();
      while (retry) {
        try {
           mContentThread.join();
          retry = false;
        } catch (InterruptedException e) {}
      }
   }
   
}
