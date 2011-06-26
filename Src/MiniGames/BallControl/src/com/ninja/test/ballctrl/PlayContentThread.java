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

	/** Petit profiler rapide pour garder compte du FPS et obtenir le temps entre les frames. */
	private Profiler profiler;

	/** Es-ce que le thread de l'application est actif? */
	private boolean mRun = false;
	
	/** 
	 * Les dimensions du canvas de l'écran. 
	 */
	private RectF mCanvasDim;
	
	/**
	 * Systeme de particules contenant les obstacles, les objets à ramasser
	 * et la balle Ninja
	 */
	public ParticlesSystem mParticlesSystem;

	/** 
	 * On prend la ninjaBall et on la fait collisionner avec le systeme de particules 
	 */
	private NinjaBall ball;
	
	/**
	 *  nombre de shurikens ramassés durant le jeu
	 */
	private int shurikensCollected = 0;
	
	/**
	 * Variable servant à meusurer l'intervale de temps écoulé entre chaque affichage
	 */
	private long delta;

	private Paint stringBrush;
	private final static int stringColor = Color.WHITE;

	// Varialbles utiles dans la fonctions computeCollisions
	private int prodVect;
	private int newX;
	private int newY;
	private Point translateVect;
	private Point collideVect;
//	private Collidable it;
	private boolean more;
	final int NB_MAX_ITERATIONS = 10;
	
	// Variable pour la fonction computeCollisionWithBounds
	private int rayonN;
	
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

	public void pause() {
		mRun = false;
	}
	
	public void unpause() {
		mRun = true;
    	profiler.Tick();
	}
	
	@Override
	public void run() {
		Canvas c;
		mParticlesSystem.placeItems();
		
		while (mRun) {
	    	profiler.Tick();
	    	
	    	delta = profiler.Delta();
	    	mParticlesSystem.MoveNinja(delta);
			computeCollisions();
			
			c = null;
			try {
				c = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder) {

					// reset l'écran
				    c.drawColor(Color.BLACK);
				    
					doDraw(c);
				    
				    // affiche les fps
					profiler.Draw(c, mCanvasDim);
					c.drawText(Integer.toString(shurikensCollected), 5, 15, stringBrush);
					 
				}
			}finally {
				if (c != null)
					mSurfaceHolder.unlockCanvasAndPost(c);
			}
		}
	}
	
	private void computeCollisionWithBounds() {
		
		rayonN = ball.getRayon();
		
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
		
		for(Iterator<Collidable> i = mParticlesSystem.GetCoinsList(); i.hasNext();) {
			Coin it = (Coin) i.next();
			
			if(ball.collided(it)) {
				shurikensCollected++;
				it.replace((int)mCanvasDim.right, (int)mCanvasDim.bottom);
			}
		}
	}
	
	private void computeCollisions() {
		// On prend la ninjaBall et on la fait collisionner avec le systeme de particules
		ball = mParticlesSystem.theOne;
		
		more = true;
		for(int count = 0; count < NB_MAX_ITERATIONS && more; ++count) {
			more = false;
			// boucle qui gère les collisions entre les obstacles et le Ninja
			for(Iterator<Collidable> i = mParticlesSystem.GetObstclesList(); i.hasNext();) {
				Collidable it = i.next();
				
				// S'il y a collision entre les 2 objets
				if(ball.collided(it)) {
					
					// on calcule le produit vectoriel entre le vecteur déplacement de la balle
					// et le vecteur de collision entre les deux éléments
					translateVect = new Point(ball.getLastPosX()-ball.getX(), ball.getLastPosY()-ball.getY());
					collideVect = new Point(it.getX()-ball.getX(), it.getY()-ball.getY());
					
					// produit vectoriel entre les 2 vecteurs (règle de la main droite)
					prodVect = translateVect.x * collideVect.y - translateVect.y * collideVect.x;
					
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
    	// on dessine les obstacles
	    mParticlesSystem.Draw(c, mCanvasDim);
	    
	    // On dessine les shurikens a ramasser
	    mParticlesSystem.DrawShurikens(c);

		// On dessine la balle Ninja en dernier pour que dans le cas ou elle 
	    // chevauche quelque chose elle soit dessinée par dessus
    	mParticlesSystem.DrawNinja(c);
  	}

	public void setRunning(boolean running) {
		mRun = running;
	}
	
	public void setSurfaceSize(int width, int height) {
		synchronized (mSurfaceHolder) {
		  mParticlesSystem.setSurfaceSize(width, height);
		  MapsManager.setBounds(mParticlesSystem.getOffset());
		  mCanvasDim.left = 0;
		  mCanvasDim.top = 0;
		  mCanvasDim.right = MapsManager.getBounds().x;
		  mCanvasDim.bottom = MapsManager.getBounds().y;
		}
	}
	
}
