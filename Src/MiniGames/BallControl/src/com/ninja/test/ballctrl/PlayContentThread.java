package com.ninja.test.ballctrl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
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
	
	public ParticlesSystem mParticlesSystem;

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

				    c.drawColor(Color.BLACK);
				    
					doDraw(c);
					 
				}
			}finally {
				if (c != null)
					mSurfaceHolder.unlockCanvasAndPost(c);
			}
		}
	}
	
	public void Tick() {
		profiler.Tick();
	}
	
    public void doDraw(Canvas c) {
    	profiler.Tick();
    	long delta = profiler.Delta();
    	
		Log.d("ContentThread::doDraw", "fonction qui dessine le canevas");
    	
    	//if(!obstaclesDrawn) {
	    	mParticlesSystem.Draw(c, mCanvasDim);
	    	obstaclesDrawn = true;
    	//}

		// ensuite on dessinera la ninja ball
    	mParticlesSystem.DrawNinja(c, delta);
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
