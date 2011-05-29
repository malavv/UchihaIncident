package com.ninja.test.ballctrl;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

public class PlayContentThread extends Thread {
	
	/** La suface de la vue de l'écran. */
	private SurfaceHolder mSurfaceHolder;

	/** Renseignement sur le context dans lequel l'application roule. */
	private Context mContext;

	/** Petit profiler rapide pour garder compte du FPS et obtenir le temps entre les frames. */
	private Profiler profiler;

	/** Es-ce que le thread de l'application est actif? */
	private boolean mRun = false;
	
	/** Les dimensions du canvas de l'écran. */
	private RectF mCanvasDim;
	
	private ParticlesSystem mParticlesSystem;

	private boolean obstaclesDrawn = false;
	
	public PlayContentThread(SurfaceHolder surface, Context context) {
	    mSurfaceHolder = surface;
		mParticlesSystem = new ParticlesSystem( context );

	    mCanvasDim = new RectF();  
	    profiler = new Profiler();
	}
	
	public void Panic() {
		mRun = false;
	}

	public void pause() {}
	public void unpause() {}
	
	@Override
	public void run() {
		while (mRun) {
			Canvas c = null;
			try {
				c = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder) {
					doDraw(c);
				}
			} catch(Exception e) { 
				Log.wtf("ContentThread::run", "probleme en dessinant le canevas");
			}finally {
				if (c != null)
					mSurfaceHolder.unlockCanvasAndPost(c);
			}
		}
	}
    public void doDraw(Canvas c) {
    	profiler.Tick();
    	
		Log.d("ContentThread::doDraw", "fonction qui dessine le canevas");
		
    	ArrayList<Collidable> obstacles = mParticlesSystem.GetObstclesList();
    	
    	if(!obstaclesDrawn) {
	    	for(int i = 0; i < obstacles.size(); i++) {
	    		// on va dessiner les obstacles ici plus tard
	    		obstacles.get(i);
	    	}
	    	obstaclesDrawn = true;
    	}

		// ensuite on dessinera la ninja ball
  	}

	public void setRunning(boolean running) {
		mRun = running;
	}
	
	public void setSurfaceSize(int width, int height) {
		Log.d("ContentThread::setSurfaceSize", "on get la taillle du canevas");
		synchronized (mSurfaceHolder) {
		  mCanvasDim.left = 0;
		  mCanvasDim.top = 0;
		  mCanvasDim.right = width;
		  mCanvasDim.bottom = height;
		  mParticlesSystem.setSurfaceSize(width, height);
		}		
	}
	
}
