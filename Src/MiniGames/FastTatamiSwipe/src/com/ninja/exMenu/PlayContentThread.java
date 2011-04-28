package com.ninja.exMenu;

import java.util.HashMap;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

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
	  public long Delta() { return tickDiff; }
	  public void Tick() {
	    long time = System.currentTimeMillis();
	    tickDiff = time - lastTick;
	    lastTick = time;
	  }
	}
	
	private class Sound {
		private SoundPool mSound;
		private HashMap<Integer, Integer> mSoundPoolMap;
		private AudioManager mAudioManager;
		private Context mContext;
		public void Init(Context c) {
			mContext = c;
			mSound = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
			mSoundPoolMap = new HashMap<Integer, Integer>();
			mAudioManager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
		}
		public void Load(int index, int id) { mSoundPoolMap.put(index, mSound.load(mContext, id, 1)); }
		public void playSound(int index) {
		  float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		  streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		  mSound.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);
		}
		 
		public void playLoopedSound(int index) {
		    float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		    streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		    mSound.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f);
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
  public static final int kPause = 1;
  public static final int kAnimationDebut = 2;
  public static final int kUserInput = 3;
  public static final int kAnimationMouvement = 4;
  public static final int kAnimationFin = 5;
  
  private Sprite Samurai = null;
  private Sprite Tameshigiri = null;
  
  private int distSamurai = 0;
  private int distTame = 0;
  
  private Sound mSound = new Sound();
  
  
  private long anim1Count = 0;
	
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
		
		mSound.Init(context);
		mSound.Load(1, R.raw.fourreau);
		
		Samurai = new Sprite(context.getResources().getDrawable(R.drawable.kendo));
		Tameshigiri = new Sprite(context.getResources().getDrawable(R.drawable.tameshigiri));
		
		Samurai.Scale(2.5);
		Tameshigiri.Scale(0.8);
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
		
		synchronized (mSurfaceHolder) {
			// Clear de l'écran.
			c.drawColor(Color.BLACK);
			
			switch (mMode) {
				case kInitial:
					mMode = kAnimationDebut;
					break;
				case kPause:
					break;
				case kAnimationDebut:
					FirstAnimation(c, profiler.Delta());
					break;
				case kUserInput:
					break;
				case kAnimationMouvement:
					break;
				case kAnimationFin:
					break;
			}
			Fps(c);
		}
	}
	
	private void FirstAnimation(Canvas c, long delta) {
		anim1Count += delta;
		
		if (anim1Count >= 2500) {
		  anim1Count = 0;
		  mMode = kUserInput;
		  mSound.playSound(1);
		  return;
		} else if (anim1Count >= 2000) {
	      Tameshigiri.Draw(c);
		  Samurai.Draw(c);
		  return;	
		}
		
		Point pTameshigiri = new Point((int)(mCanvasDim.Width * 0.25) + (int)(((double)distTame / 2000.0) * anim1Count), (int)(mCanvasDim.Height * 0.6));
		Point pSamu = new Point(mCanvasDim.Width - (int)(((double)distSamurai / 2000.0) * anim1Count), (int)(mCanvasDim.Height * 0.7));
		
		Samurai.CenterOn(pSamu);
		Tameshigiri.CenterOn(pTameshigiri);
		
		Tameshigiri.Draw(c);
		Samurai.Draw(c);
	}
	
	private void Fps(Canvas c) {
		String s = "FPS : " + profiler.PrintFps();
		c.drawText(s, 5, mCanvasDim.Height - 5, mLinePaint);
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
		distSamurai = (int)(mCanvasDim.Width * 0.75);
		distTame = (int)(mCanvasDim.Width * 0.25);
		
	}
	public void pause() {}
	public void unpause() {}
}
