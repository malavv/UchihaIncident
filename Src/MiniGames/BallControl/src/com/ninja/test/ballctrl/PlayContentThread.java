package com.ninja.test.ballctrl;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Handler;
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
	
	public PlayContentThread(SurfaceHolder surface, Context context, Handler hMsg) {
		mParticlesSystem = new ParticlesSystem( context );
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
    public void doDraw(Canvas c) {
    	profiler.Tick();
    	ArrayList<Collidable> obstacles = mParticlesSystem.GetObstclesList();
    	
    	if(obstaclesDrawn) {
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
		synchronized (mSurfaceHolder) {
		  mCanvasDim.left = 0;
		  mCanvasDim.top = 0;
		  mCanvasDim.right = width;
		  mCanvasDim.bottom = height;
		}		
		mParticlesSystem
	}
	
}
