package com.ninja.test.ballctrl;

import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
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
			computeCollisions();
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
	
	private void computeCollisions() {
		
		// On prend la ninjaBall et on la fait collisionner avec le systeme de particules
		NinjaBall ball = mParticlesSystem.theOne;
		// l'offset correspond à la taille en x et en y des différents éléments du terrain
		int offset = mParticlesSystem.getOffset();
		
		int rayonN = ball.getRayon();
		
		// collision avec le bord de l'écran
		if(ball.getX()-rayonN < mCanvasDim.left){
			ball.setX((int) (mCanvasDim.left + rayonN));
		} else if(ball.getX()+rayonN > mCanvasDim.right) {
			ball.setX((int) (mCanvasDim.right - rayonN));
		}
		if(ball.getY()-rayonN < mCanvasDim.top){
			ball.setY((int) (mCanvasDim.top + rayonN));
		} else if(ball.getY()+rayonN > mCanvasDim.bottom) {
			ball.setY((int) (mCanvasDim.bottom - rayonN));
		}
		
		// boucle qui gère les collisions entre les obstacles et le Ninja
		for(Iterator<Collidable> i = mParticlesSystem.GetObstclesList(); i.hasNext();) {
			Collidable it = i.next();
			
			// S'il y a collision entre les 2 objets
			if(Collidable.collided(ball, it)) {
				
				// on calcule le produit vectoriel entre le vecteur déplacement de la balle
				// et le vecteur de collision entre les deux éléments
				Point translateVect = new Point(ball.getLastPosX()-ball.getX(), ball.getLastPosY()-ball.getY());
				Point collideVect = new Point(it.getX()-ball.getX(), it.getY()-ball.getY());
				
				// produit vectoriel entr eles 2 vecteurs (règle de la main droite)
				int prodVect = translateVect.x * collideVect.y - translateVect.y * collideVect.x;
				
				int newX;
				int newY;
				
				// si le centre de l'obstacle est a gauche du déplacement
				if(prodVect > 0) {
					newX = ball.getX() + translateVect.y;
					newY = ball.getY() - translateVect.x;
				} 
				// si le centre de l'obstacle est à droite du déplacement
				else if (prodVect < 0) {
					newX = ball.getX() - translateVect.y;
					newY = ball.getY() + translateVect.x;
				} 
				// si les 3 points sont allignés (prodVect == 0)
				else {
					newX = ball.getX() - translateVect.x;
					newY = ball.getY() - translateVect.y;
				}
				
				
				// on gèrera la perte (ou gain) d'énerdie plus tard...
				newX *= ball.getElasticity()*it.getElasticity();
				newY *= ball.getElasticity()*it.getElasticity();
				
				ball.updatePosition(newX, newY);
				
				// si on a trouvé une collision, on ne regardera pas les autres murs
				return;
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
