package com.ninja.exMenu;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
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
    
  private Opponent mOpponent;
  
  /** Le temps après Ready. */
  private static long kTimeBeforeGameMs = 1850;
  
  /** Le jigger dans le temps après ready. Donc le temps final est 
   *  kTimeBeforeGameMs + kJiggerMs.
   */
  private static long kJiggerMs = 400;
  
  private static int kSoundSword = 1;
	
	/** La suface de la vue de l'écran. */
	private SurfaceHolder mSurfaceHolder;
	
	 /** Les dimensions du canvas de l'écran. */
  private RectF mCanvasDim;
	
	/** Petit profiler rapide pour garder compte du FPS et obtenir le temps entre les frames. */
  private Profiler profiler;
  
  /** Es-ce que le thread de l'application est actif? */
  private boolean mRun = false;
  
  /** Le mode dans lequel l'application se trouve en se moment. */
  private int mMode = kInitial;
  
  
  /**
   * J'ai ici plusieurs compte qui serait bien d'être ailleur.
   */
  private long countReady = 0;
  private long countUserInput = 0;
  private long countAnimMvt = 0;
  private long countEnd = 0;
  private long timeBeforeGame = 0;
  private ArrayList<Integer> times = new ArrayList<Integer>();
  private int nOfGame = 0;
  
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
  private LinearBitmapAnimation Katana;
  private RotationBitmapAnimation KatanaRot;
  
  private Sprite samurai;
  private Sprite tatami_top;
  private Sprite tatami_bottom;
  private Sprite tatami_complet;
  private Sprite katanaSprite;
  
  private SoundManager mSound = new SoundManager();
  
  private Handler msgHandler;
	
  private PlayGrid mPlayGrid;
  private FollowupLine mFollowupLine;
  
  private boolean hasWon;
  
  /**
   * Créé un thread sous-jacent au minigame pour géré les animations, la physique et les timers.
   * @param surface La surface fournie par la vue, visible par l'utilisateur.
   * @param context Le context android dans lequel roule l'application.
   * @param msgHandler La pompe à événement servant à communiqué avec la vue.
   */
  public PlayContentThread(SurfaceHolder surface, Context context, Handler hMsg, Opponent opponent) {
    mSurfaceHolder = surface;
    msgHandler = hMsg;
    mOpponent = opponent;
  	
    mCanvasDim = new RectF();    
    profiler = new Profiler();
  		
    mSound.Init(context);
    mSound.Load(kSoundSword, R.raw.fourreau);
    
    samurai = new Sprite(context.getResources().getDrawable(R.drawable.kendo));
    tatami_top = new Sprite(context.getResources().getDrawable(R.drawable.tatami_top));
    tatami_bottom = new Sprite(context.getResources().getDrawable(R.drawable.tatami_bottom));
    tatami_complet = new Sprite(context.getResources().getDrawable(R.drawable.tatami_uncut));
    katanaSprite =  new Sprite(context.getResources().getDrawable(R.drawable.katana_low_jeu));
    
    samurai.Scale(1.1);
    tatami_complet.Scale(0.7);
    katanaSprite.Scale(0.5);
    
    tatami_top.Scale(0.5);
    tatami_bottom.Scale(0.5);
    
    FreshStart();
  }
  
  // Recommence le combat au complet.
  public void FreshStart() {
    times.clear();
    nOfGame = 0;
    startRun();
  }
  
  // Réinitialise ce qui doit rester entre plusieurs manche d'un combat.
  private void startRun() {
    setState(kInitial);
    countReady = countUserInput = countAnimMvt = timeBeforeGame = countEnd = 0;
    Samurai = new LinearBitmapAnimation(samurai, 2000, new Point(600, 100), new Point(0, 0));
    Tameshigiri = new LinearBitmapAnimation(tatami_complet, 2000, new Point(100, 100), new Point(300, 100));
    mPlayGrid = new PlayGrid();
    mFollowupLine = new FollowupLine();
    mPlayGrid.Dots(mOpponent.getNDots());
  }
	
  public void doStart() {
    profiler.Tick();
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
	      AnimationEnd(c, profiler.Delta());
		    break;
      case kPause:
        break;
	  }
	}
  
  private int GetJiggerFromTime(int timeMs) {
    return (timeMs - 200) / 3;
  }
  
  private void SetUpAnimationMvt() {
    
    float halfHeight = mCanvasDim.bottom / 2;
    long timeToCompletion = countUserInput - timeBeforeGame;
    
    float percentSpeed = 0f;
    
    float diffWithWinningTime = timeToCompletion - mOpponent.getLengthMs();
    
    if (diffWithWinningTime < 0)  percentSpeed = 1.0f;
    else {
      percentSpeed = 1 - (diffWithWinningTime / GetJiggerFromTime(mOpponent.getLengthMs()));
      if (percentSpeed < 0.0f)  percentSpeed = 0.0f;
    }
    
    // À ce moment percentSpeed devrais contenir la valeur en pourcentage
    // du tatami coupé.
    Point depart;
    Point arrive;
    float degree;
    
    if (percentSpeed >= 1.0f) {
      depart = new Point(370, 90);
      arrive = new Point(200, 190);
      degree = -18;
      hasWon = true;
    } else {
      int dx = 310 - 230;
      int dy = 170 - 190;
      
      depart = new Point(370, 90);
      arrive = new Point(310 - (int)(percentSpeed * dx), 170 - (int)(percentSpeed * dy));
      degree = -11;
      hasWon = false;
    }
    
    Katana = new LinearBitmapAnimation(katanaSprite, 500, depart, arrive);
    KatanaRot = new RotationBitmapAnimation(katanaSprite, 500, degree);
    
    tatami_top.SetPosition(new PointF(mCanvasDim.right / 2 - 4, halfHeight - 53));
    tatami_bottom.SetPosition(new PointF(mCanvasDim.right / 2 + 4, halfHeight + 53));
    Katana.Sprite().Rotate(70.0f);
    Katana.Sprite().PlaceRotationCenter(new PointF(mCanvasDim.right / 2, mCanvasDim.bottom / 2));
  }
  
  private void AnimationEnd(Canvas c, long delta) {
    
    // Cas spécial où l'on as gagné et on affiche le menu live.
    if (nOfGame == 0)  return;
    
    // On n'as pas finit, une autre manche arrive.
    if (nOfGame < mOpponent.getNTimes()) {
      if (countEnd > 1100)  startRun();
      else countEnd += delta;
    
    // On a gagné ou perdu, on affiche le menu.
    } else {
      int moy = 0;
      for(Integer time : times) moy += time;
      moy = (int)((moy / (float)times.size()) + 0.5);
      hasWon = (moy < mOpponent.getLengthMs());
      
      if (nOfGame > 0) {
        nOfGame = 0;
        GameContext.SetEndGameInfo(hasWon, moy);
        ShowMenu(hasWon, moy);
      }
    }
  }
  
  private void AnimationMvt(Canvas c, long delta) {
    
    boolean stillMoving = true;
    if (countAnimMvt < 500) {
      countAnimMvt += delta;
      KatanaRot.Animate(delta);
    } else if (countAnimMvt < 1600) {
      stillMoving = false;
    }
    
    Katana.Animate(delta);
    
    synchronized (mSurfaceHolder) {
      c.drawColor(Color.BLACK);
      
      tatami_bottom.Draw(c);
      Katana.Sprite().Draw(c);
      tatami_top.Draw(c);
      
      profiler.Draw(c, mCanvasDim);
      
      if (!stillMoving) {
        long time = countUserInput - timeBeforeGame;
        times.add((int)time);
        nOfGame++;
        setState(kAnimationFin);
      }
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
	    profiler.Draw(c, mCanvasDim);
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
	    profiler.Draw(c, mCanvasDim);
	  }
	}
	
  private void UserInput(Canvas c, long delta) {
    countUserInput += delta;
    
    // Temps aléatoire avant de commencer.
    if (countUserInput < timeBeforeGame) return;
    
    // T'es trop long, dsl.
    if (countUserInput - timeBeforeGame > 3300) {
      setState(kAnimationMouvement);
      return;
    }
    
    // Tous les cible ont été touchés
    if (mPlayGrid.IsWon()) {
      setState(kAnimationMouvement);
      return;
    }
    
    synchronized (mSurfaceHolder) {
      c.drawColor(Color.BLACK);
      
      mPlayGrid.Draw(c, mCanvasDim);
      mFollowupLine.Draw(c);
      
      profiler.Draw(c, mCanvasDim);
    }
  }
	
	public void doTouch(MotionEvent e) {
	  if (mMode != kUserInput)  return;
	  
	  synchronized (mSurfaceHolder) {
      mPlayGrid.DoTouch(e);
      mFollowupLine.DoTouch(e);
	  }
	}
	
	private void SetUpUserInput() {
	  Status("");
	  
	  Random r = new Random();
	  timeBeforeGame = kTimeBeforeGameMs + r.nextLong() % kJiggerMs;
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
	      SetUpAnimationMvt();
	      break;
	  }
	  
	  synchronized (mSurfaceHolder) {
		  mMode = mode;
	  }
	}
	
	public void setSurfaceSize(int width, int height) {
		synchronized (mSurfaceHolder) {
		  mCanvasDim.left = 0;
		  mCanvasDim.top = 0;
		  mCanvasDim.right = width;
		  mCanvasDim.bottom = height;
		}		
	}
	public void pause() {}
	public void unpause() {}
	
	private void ShowMenu(boolean hasWon, int time) {
	   Message msg = msgHandler.obtainMessage();
	   Bundle b = new Bundle();
	   b.putInt("mode", PlayContentView.kShowMenu);
	   b.putBoolean("hasWon", hasWon);
	   b.putInt("time", time);
	   msg.setData(b);
	   msgHandler.sendMessage(msg);
	}
	
	private void Status(String status) {
	  Message msg = msgHandler.obtainMessage();
	  Bundle b = new Bundle();
	  b.putInt("mode", PlayContentView.kStatusUpdate);
	  b.putString("status", status);
	  msg.setData(b);
	  msgHandler.sendMessage(msg);
	}
}
