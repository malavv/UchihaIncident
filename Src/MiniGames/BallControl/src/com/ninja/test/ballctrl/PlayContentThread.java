package com.ninja.test.ballctrl;

import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
	
	// nombre de shurikens ramassés durant le jeu
	private int shurikensCollected = 0;

	private Paint stringBrush;
	private final static int stringColor = Color.WHITE;
	
	public PlayContentThread(SurfaceHolder surface, Context context) {
	    mSurfaceHolder = surface;
		mParticlesSystem = new ParticlesSystem( context );

		stringBrush = new Paint();
	    stringBrush.setColor(stringColor);

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
	    	profiler.Tick();
	    	
	    	long delta = profiler.Delta();
	    	mParticlesSystem.MoveNinja(delta);
			computeCollisions();
			
			Canvas c = null;
			try {
				c = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder) {

					// reset l'écran
				    c.drawColor(Color.BLACK);
				    
				    // affiche les fps
					profiler.Draw(c, mCanvasDim);
					c.drawText(Integer.toString(shurikensCollected), 5, 15, stringBrush);
				    
					doDraw(c);
					 
				}
			}finally {
				if (c != null)
					mSurfaceHolder.unlockCanvasAndPost(c);
			}
		}
	}
	
	private void computeCollisionWithBounds() {
		// On prend la ninjaBall et on la fait collisionner avec le systeme de particules
		NinjaBall ball = mParticlesSystem.theOne;
		int rayonN = ball.getRayon();
		
		// collision avec le bord de l'écran
		if(ball.getX()-rayonN < mCanvasDim.left){
			ball.setLastPosX(ball.getX());
			ball.setX((int) (mCanvasDim.left + rayonN));
		} else if(ball.getX()+rayonN > mCanvasDim.right) {
			ball.setLastPosX(ball.getX());
			ball.setX((int) (mCanvasDim.right - rayonN));
		}
		if(ball.getY()-rayonN < mCanvasDim.top){
			ball.setLastPosY(ball.getY());
			ball.setY((int) (mCanvasDim.top + rayonN));
		} else if(ball.getY()+rayonN > mCanvasDim.bottom) {
			ball.setLastPosY(ball.getY());
			ball.setY((int) (mCanvasDim.bottom - rayonN));
		}
	}
	
	private void findShuriken() {
		// On prend la ninjaBall et on la fait collisionner avec le systeme de particules
		NinjaBall ball = mParticlesSystem.theOne;
		
		for(Iterator<Collidable> i = mParticlesSystem.GetCoinsList(); i.hasNext();) {
			Coin it = (Coin) i.next();
			
			if(Collidable.collided(ball, it)) {
				shurikensCollected++;
				it.replace((int)mCanvasDim.right, (int)mCanvasDim.bottom);
			}
		}
	}
	
	private void computeCollisions() {
		
		// On prend la ninjaBall et on la fait collisionner avec le systeme de particules
		NinjaBall ball = mParticlesSystem.theOne;
		
		final int NB_MAX_ITERATIONS = 10;
		
		boolean more = true;
		
		for(int count = 0; count < NB_MAX_ITERATIONS && more; ++count) {
			more = false;
			// boucle qui gère les collisions entre les obstacles et le Ninja
			for(Iterator<Collidable> i = mParticlesSystem.GetObstclesList(); i.hasNext();) {
				Collidable it = i.next();
				
				// S'il y a collision entre les 2 objets
				if(Collidable.collided(ball, it)) {
					
					// on calcule le produit vectoriel entre le vecteur déplacement de la balle
					// et le vecteur de collision entre les deux éléments
					Point translateVect = new Point(ball.getLastPosX()-ball.getX(), ball.getLastPosY()-ball.getY());
					Point collideVect = new Point(it.getX()-ball.getX(), it.getY()-ball.getY());
					
					// produit vectoriel entre les 2 vecteurs (règle de la main droite)
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
						newX = ball.getX() + translateVect.x;
						newY = ball.getY() + translateVect.y;
					}
					
					
					// on gèrera la perte (ou gain) d'énerdie plus tard...
					newX *= ball.getElasticity()*it.getElasticity();
					newY *= ball.getElasticity()*it.getElasticity();
					
					ball.updatePosition(newX, newY);
					
					more = true;
				}
			}
			computeCollisionWithBounds();
		}
		findShuriken();
	}
	
	public void Tick() {
		profiler.Tick();
	}
	
    public void doDraw(Canvas c) {
    	
		Log.d("ContentThread::doDraw", "fonction qui dessine le canevas");
    	
    	//if(!obstaclesDrawn) {
	    	mParticlesSystem.Draw(c, mCanvasDim);
	    	obstaclesDrawn = true;
    	//}
	    
	    mParticlesSystem.DrawShurikens(c);

		// ensuite on dessinera la ninja ball
    	mParticlesSystem.DrawNinja(c);
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
