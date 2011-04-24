package com.ninja.exMenu;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * Le thread sous-jacent au jeu. Il contient la physique,
 * les animations et les timers.
 */
public class PlayContentThread extends Thread {

	/**
	 * Une linked list de coordonnée que j'utilise comme une queue.
	 */
	private LinkedList<PointF> coords;
	
	private class Dimension {
	  public Dimension(int width, int height) { Width = width; Height = height; }
	  public int Height;
	  public int Width;
	}
	
	private class Profiler {
	  private long lastTick;
	  private long tickDiff;
	  public long Fps() { return 1000 / tickDiff; }
	  public String PrintFps() { return Long.toString(Fps()); }
	  public void Tick() {
	    long time = System.currentTimeMillis();
	    tickDiff = time - lastTick;
	    lastTick = time;
	  }
	}
	
	/** Les dimensions du canvas de l'écran. */
	private Dimension mCanvasDim;
	
	/** Le service de messagerie servant à envoyé des messages à la vue. */
	private Handler mMsgHandler;
	
	/** La suface de la vue de l'écran. */
	private SurfaceHolder mSurfaceHolder;
	
	/** Brosse pour les lignes. */
	private Paint mLinePaint;
	
	/** Renseignement sur le context dans lequel l'application roule. */
	private Context mContext;
	
	/** Petit profiler rapide pour garder compte du FPS. */
  private Profiler profiler;
  
  /** Es-ce que le thread de l'application est actif? */
  private boolean mRun = false;
  
  /** Le mode dans lequel l'application se trouve en se moment. */
  private int mMode = kInitial;
  
  /**
   * Le mode dans lequel nous nous trouvions avant le dernier changement
   * d'état, il sert principalement à revenir d'un resume.
   */
  private int mLastMode = kInitial;
  
  /**
   * On retrouve ici les états dans lesquels peut ce situer le minigame.
   */
  public static final int kInitial = 0;
  public static final int kkPause = 1;
  public static final int kAnimationDebut = 2;
  public static final int kUserInput = 3;
  public static final int kAnimationMouvement = 4;
  public static final int kAnimationFin = 5;
	
  /**
   * Créé un thread sous-jacent au minigame pour géré les animations, la physique et les timers.
   * @param surface La surface fournie par la vue, visible par l'utilisateur.
   * @param context Le context android dans lequel roule l'application.
   * @param msgHandler La pompe à événement servant à communiqué avec la vue.
   */
	public PlayContentThread(SurfaceHolder surface, Context context, Handler msgHandler) {
		mSurfaceHolder = surface;
		mContext = context;
		mMsgHandler = msgHandler;
		
		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setARGB(255, 255, 255, 255);
		
		coords = new LinkedList<PointF>();
		mCanvasDim = new Dimension(1, 1);
		profiler = new Profiler();
	}
	
	public void doStart() {
	  profiler.Tick();
		setState(kInitial);
	}
	
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
		
		profiler.Tick();
		
		String s = "FPS : " + profiler.PrintFps();
		
		synchronized (mSurfaceHolder) {
			
			c.drawColor(Color.BLACK);
			
			c.drawText(s, 5, mCanvasDim.Height - 5, mLinePaint);
			
			if (coords.size() > 2) {
			  for (int i = coords.size() - 2, j = coords.size() - 1; i > 0; i--, j--) {
				  c.drawLine(coords.get(j).x, coords.get(j).y, coords.get(i).x, coords.get(i).y, mLinePaint);
			  }
			}
		}
	}
	
	public void doTouch(MotionEvent e) {
	  synchronized (mSurfaceHolder) {
	    coords.add(new PointF(e.getX(), e.getY()));
	    if (coords.size() > 80)  coords.poll();
	  }
	}
	
	public void setState(int mode) {
	  synchronized (mSurfaceHolder) {
		  mMode = mode;
	  }
	}
	
	public void setSurfaceSize(int width, int height) {
		synchronized (mSurfaceHolder) {
		  mCanvasDim.Height = height;
		  mCanvasDim.Width = width;
		}
	}
	public void pause() {}
	public void unpause() {}
}
