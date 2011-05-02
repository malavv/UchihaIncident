package com.ninja.exMenu;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Le thread sous-jacent au jeu. Il contient la physique,
 * les animations et les timers.
 */
public class PlayContentThread extends Thread {

  private int mNDots = 4;
  private long mTimeBeforeGameMs = 1850;
  private long mJiggerMs = 400;
  private int mRadiusInterDots = 30;
  private double mUnusedPercentBorder = 0.13;
  
	/**
	 * Une linked list de coordonnée que j'utilise comme une queue.
	 */
	private LinkedList<PointF> coords;
	
	private LinkedList<Cible> UserInputPoint;
	
	private class Profiler {
	  private long lastTick;
	  private long tickDiff;
	  public long Fps() { return 1000 / tickDiff; }
	  public String PrintFps() { return Long.toString(Fps()); }
	  public long Delta() { return tickDiff; }
	  public void Tick() {
	    long time = System.currentTimeMillis();
	    tickDiff = time - lastTick;
	    lastTick = time;
	  }
	}
	
	private class Cible {
	  public Cible(Point position) { p = position; isHit = false; }
	  public Point p;
	  public boolean isHit;
	}
	
	/** Les dimensions du canvas de l'écran. */
	private Point mCanvasDim;
	
	/** La suface de la vue de l'écran. */
	private SurfaceHolder mSurfaceHolder;
	
	/** Brosse pour les lignes. */
	private Paint mLinePaint;
	/** Brosse blanche */
	private Paint mRedPaint;
	
	/** Renseignement sur le context dans lequel l'application roule. */
	private Context mContext;
	
	/** Petit profiler rapide pour garder compte du FPS. */
  private Profiler profiler;
  
  /** Es-ce que le thread de l'application est actif? */
  private boolean mRun = false;
  
  /** Le mode dans lequel l'application se trouve en se moment. */
  private int mMode = kInitial;
  
  private Random mRandom = new Random();
  
  private long countReady = 0;
  private long countUserInput = 0;
  private long timeBeforeGame = 0;
  
  private boolean aGagne = false;
  
  /**
   * Le mode dans lequel nous nous trouvions avant le dernier changement
   * d'état, il sert principalement à revenir d'un resume.
   */
  private int mLastMode = kInitial;
  
  /**
   * On retrouve ici les états dans lesquels peut ce situer le minigame.
   */
  public static final int kInitial = 0;
  public static final int kAnimationDebut = 1;
  public static final int kReady = 2;
  public static final int kUserInput = 3;
  public static final int kAnimationMouvement = 4;
  public static final int kAnimationFin = 5;
  public static final int kPause = 6;
  
  
  private LinearBitmapAnimation Samurai;
  private LinearBitmapAnimation Tameshigiri;
  
  private SoundManager mSound = new SoundManager();
  
  private Handler msgHandler;
	
  /**
   * Créé un thread sous-jacent au minigame pour géré les animations, la physique et les timers.
   * @param surface La surface fournie par la vue, visible par l'utilisateur.
   * @param context Le context android dans lequel roule l'application.
   * @param msgHandler La pompe à événement servant à communiqué avec la vue.
   */
  public PlayContentThread(SurfaceHolder surface, Context context, Handler hMsg) {
    mSurfaceHolder = surface;
    mContext = context;
    msgHandler = hMsg;
  
    mLinePaint = new Paint();
    mLinePaint.setAntiAlias(true);
    mLinePaint.setARGB(255, 255, 255, 255);
    
    mRedPaint = new Paint();
    mRedPaint.setAntiAlias(true);
    mRedPaint.setARGB(255,255, 0, 0);
  		
    coords = new LinkedList<PointF>();
    mCanvasDim = new Point(1, 1);
    
    UserInputPoint = new LinkedList<Cible>();
    
    profiler = new Profiler();
  		
    mSound.Init(context);
    mSound.Load(1, R.raw.fourreau);
    
    Sprite samurai = new Sprite(context.getResources().getDrawable(R.drawable.kendo));
    samurai.Scale(1.1);
    
    Sprite tameshigiri = new Sprite(context.getResources().getDrawable(R.drawable.tameshigiri));
    tameshigiri.Scale(0.5);
    
    Samurai = new LinearBitmapAnimation(samurai, 2000, new Point(600, 100), new Point(0, 0));
    Tameshigiri = new LinearBitmapAnimation(tameshigiri, 2000, new Point(100, 100), new Point(300, 100));
  }
	
  public void doStart() {
    profiler.Tick();
    setState(kInitial);
  }
	
  public void Panic() {
    mRun = false;
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
    
	  switch (mMode) {
	    case kInitial:
	      setState(kAnimationDebut);
		    break;
      case kAnimationDebut:
        AnimationDebut(c, profiler.Delta());
		    break;
      case kReady:
        Ready(c, profiler.Delta());
        break;
	    case kUserInput:
	      UserInput(c, profiler.Delta());
		    break;
	    case kAnimationMouvement:
	      AnimationMvt(c, profiler.Delta());
		    break;
	    case kAnimationFin:
		    break;
      case kPause:
        break;
	  }
	}
  
  private void AnimationMvt(Canvas c, long delta) {
    synchronized (mSurfaceHolder) {
      c.drawColor(Color.BLACK);
      Fps(c);
    }
  }
	
	private void AnimationDebut(Canvas c, long delta) {
	  if (!Samurai.isActive && !Tameshigiri.isActive) {
	    setState(kReady);
	    return;
	  }
	  Samurai.Animate(delta);
	  Tameshigiri.Animate(delta);
	  
	  synchronized (mSurfaceHolder) {
	    c.drawColor(Color.BLACK);
	    Tameshigiri.Sprite().Draw(c);
	    Samurai.Sprite().Draw(c);
	    Fps(c);
	  }
	}
	
	private void Ready(Canvas c, long delta) {
	  countReady += delta;
	  if (countReady > 1100) {
	    setState(kUserInput);
	    return;
	  }
	  synchronized (mSurfaceHolder) {
	    c.drawColor(Color.BLACK);
	    Fps(c);
	  }
	}
	
  private void UserInput(Canvas c, long delta) {
    countUserInput += delta;
    
    // Temps aléatoire avant de commencer.
    if (countUserInput < timeBeforeGame) return;
    
    // T'es trop long, dsl.
    if (countUserInput > 5000) {
      setState(kAnimationMouvement);
      return;
    }
    
    boolean foundAll = true;
    for (int i = 0; i < UserInputPoint.size(); i++) {
      if (!UserInputPoint.get(i).isHit)  foundAll = false;
    }
    
    // Tous les cible ont été touchés
    if (foundAll) {
      aGagne = true;
      setState(kAnimationMouvement);
      return;
    }
    
    synchronized (mSurfaceHolder) {
      c.drawColor(Color.BLACK);
      
      for (int i = 0; i < UserInputPoint.size(); i++) {
        if (UserInputPoint.get(i).isHit)
          c.drawCircle(UserInputPoint.get(i).p.x, UserInputPoint.get(i).p.y, 10, mRedPaint);
        else
          c.drawCircle(UserInputPoint.get(i).p.x, UserInputPoint.get(i).p.y, 10, mLinePaint);
      }
      
      if (coords.size() > 4) {
        PointF p = coords.get(coords.size() - 1);
        
        for(int i = coords.size() - 2; i > 0 ; i--) {
          c.drawLine(p.x, p.y, coords.get(i).x, coords.get(i).y, mLinePaint);
          p = coords.get(i);
        }
      }
      Fps(c);
    }
  }
	
	private void Fps(Canvas c) {
		String s = "FPS : " + profiler.PrintFps();
		c.drawText(s, 5, mCanvasDim.y - 5, mLinePaint);
	}
	
	public void doTouch(MotionEvent e) {
	  if (mMode != kUserInput)  return;
	  
	  synchronized (mSurfaceHolder) {
	    coords.add(new PointF(e.getX(), e.getY()));
	    if (coords.size() > 80)  coords.poll();
	    
      for (int i = 0; i < UserInputPoint.size(); i++) {
        Point p = UserInputPoint.get(i).p;
        float w = e.getX() - p.x;
        float h = e.getY() - p.y;
        
        if (Math.sqrt((w * w) + (h * h)) < 25.0) {
          UserInputPoint.get(i).isHit = true;
        }
      }
	  }
	}
	
	private void SetUpUserInput() {
	  Status("");
	  
	  Point minimumSize = new Point((int)(mCanvasDim.x * mUnusedPercentBorder),
	      (int)(mCanvasDim.y * mUnusedPercentBorder));
	  
	  Point maximumSize = new Point(mCanvasDim.x - minimumSize.x, mCanvasDim.y - minimumSize.y);
	  
	  Rect bounds = new Rect(minimumSize.x, minimumSize.y, maximumSize.x, maximumSize.y);
	  
	  ArrayList<RectF> genRectangle = new ArrayList<RectF>();
	  
	  for (int i = 1; i <= mNDots; i++) {
	    PointF p;
	    boolean isValid = true;
	    
	    do {
	      p = GeneratePoint(bounds);
	      for (int j = 0; j < genRectangle.size(); j++) {
	        if (genRectangle.get(j).contains((int)p.x, (int)p.y)) {
	          isValid = false;
	        }
	      }
	    } while (!isValid);
	    
	    genRectangle.add(new RectF(p.x - mRadiusInterDots, p.y - mRadiusInterDots, p.x + mRadiusInterDots, p.y + mRadiusInterDots));
  	  UserInputPoint.add(new Cible(new Point((int)p.x, (int)p.y)));
	  }
	  
	  timeBeforeGame = mTimeBeforeGameMs + mRandom.nextLong() % mJiggerMs;
	}
	
	private PointF GeneratePoint(Rect bounds) {
    float x = mRandom.nextInt(bounds.right - bounds.left) + bounds.left;
    float y = mRandom.nextInt(bounds.bottom - bounds.top) + bounds.top;
	  return new PointF(x, y);
	}
	
	public void setState(int mode) {
	  
	  switch(mode) {
	    case kAnimationDebut:  break;
	    case kReady:
	      mSound.playSound(1);
	      Status("Ready");
	      break;
	    case kUserInput:
	      SetUpUserInput();
	      break;
	    case kAnimationMouvement:
	      if (aGagne) {
	        Status("Vous avez Gagné! " + Float.toString((float)((countUserInput - timeBeforeGame) / 1000.0)) + " s");
	      }
	      else Status("Trop Long");
	      break;
	  }
	  
	  synchronized (mSurfaceHolder) {
		  mMode = mode;
	  }
	}
	
	public void setSurfaceSize(int width, int height) {
		synchronized (mSurfaceHolder) {
		  mCanvasDim.y = height;
		  mCanvasDim.x = width;
		}		
	}
	public void pause() {}
	public void unpause() {}
	 private void Status(String status) {
	    Message msg = msgHandler.obtainMessage();
	    Bundle b = new Bundle();
	    b.putString("text", status);
	    msg.setData(b);
	    msgHandler.sendMessage(msg);
	  }
}
