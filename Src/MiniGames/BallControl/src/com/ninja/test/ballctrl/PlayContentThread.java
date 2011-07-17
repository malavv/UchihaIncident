package com.ninja.test.ballctrl;

import java.util.Iterator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

public class PlayContentThread extends Thread {
	
	/** La suface de la vue de l'�cran. */
	private SurfaceHolder mSurfaceHolder;

	/** Petit profiler rapide pour garder compte du FPS et obtenir le temps entre les frames. */
	private Profiler profiler;
	
	private StopWatch stopWatch;

	/** Es-ce que le thread de l'application est actif? */
	private boolean mRun = false;
	
	/** 
	 * Les dimensions du canvas de l'�cran. 
	 */
	private RectF mCanvasDim;
	
	/**
	 * Systeme de particules contenant les obstacles, les objets � ramasser
	 * et la balle Ninja
	 */
	public ParticlesSystem mParticlesSystem;

	/** 
	 * On prend la ninjaBall et on la fait collisionner avec le systeme de particules 
	 */
	private NinjaBall ball;
	
	/**
	 *  nombre de shurikens ramass�s durant le jeu
	 */
	private int shurikensCollected = 0;
	
	/**
	 * Variable servant � meusurer l'intervale de temps �coul� entre chaque affichage
	 */
	private long delta;

	private Paint stringBrush;
	private final static int stringColor = Color.WHITE;

	private static final int kRun = 0;
	private static final int kFinished = 1;
	private static final int kReady = 2;

	// Varialbles utiles dans la fonctions computeCollisions
	private int prodVect;
	private int newX;
	private int newY;
	private Point translateVect;
	private Point collideVect;
//	private Collidable it;
	private boolean more;
	final int NB_MAX_ITERATIONS = 10;
	private int gameMode;
	
	// Variable pour la fonction computeCollisionWithBounds
	private int rayonN;

	private int finished;

	private Handler msgHandler;

	private int mode;
	
	public PlayContentThread(SurfaceHolder surface, Handler hMsg) {
	    mSurfaceHolder = surface;
	    msgHandler = hMsg;
	    
		mParticlesSystem = new ParticlesSystem( PlayContentView.sContext );
		stringBrush = new Paint();
	    mCanvasDim = new RectF();  
	    profiler = new Profiler();
	    stopWatch = new StopWatch();

	    stringBrush.setColor(stringColor);
	    
	    gameMode = MenuPage.gameMode;
	    finished = Global.END_NOT_YET;
	}
	
	public void FreshStart() {
		mParticlesSystem.placeItems();
		stopWatch.Start();
	    finished = Global.END_NOT_YET;
	    mode = kRun;
	}
	
	public boolean isGameFinished() {
		return finished > Global.END_NOT_YET;
	}
	
	public void Panic() {
		pause();
		//SaveTheWorld.saveGame(mParticlesSystem);
		//PanicMsg(stopWatch.Diff());
	}

	public void pause() {
		//mRun = false;
		stopWatch.Pause();
		mode = kReady;
		//SaveTheWorld.saveGame(mParticlesSystem);
	}
	
	public void unpause() {
		stopWatch.Resume();
		//mRun = true;
		mode = kRun;
		//SaveTheWorld.loadGame(PlayContentView.sContext);
    	profiler.Tick();
	}
	
	public void stopGame() {
		finished = Global.END_WIN;
	}
	
	@Override
	public void run() {
		Canvas c;
		mParticlesSystem.placeItems();
		stopWatch.Start();
		while (mRun) {
			switch(mode) {
			case kRun:
				profiler.Tick();
		    	delta = profiler.Delta();
		    	mParticlesSystem.MoveNinja(delta);
				computeCollisions();
				c = null;
				try {
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {doDraw(c);}
				}finally {
					if (c != null) mSurfaceHolder.unlockCanvasAndPost(c);
				}
				if(finished > Global.END_NOT_YET)
					mode = kFinished;
				break;
			case kFinished:
				boolean endType = finished == Global.END_WIN; 
				ShowMenu(endType, stopWatch.Diff());
				shurikensCollected = 0;
				mode = kReady;
				break;
			case kReady:
				break;
			}
		    	
		}
	}
	
	private void computeCollisionWithBounds() {
		
		rayonN = ball.getRayon();
		
		// collision avec le bord de l'�cran
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
				
				// Timed ou Survival
				if(gameMode > Global.MODE_NORMAL) {
					it.replace((int)mCanvasDim.right, (int)mCanvasDim.bottom);
					if(gameMode == Global.MODE_SURVIVAL){
						ball.increaseSpeed(.5f);
					}
				} else {
					if(shurikensCollected >= mParticlesSystem.GetCoinsListeSize()) {
						finished = Global.END_WIN;
					}
					it.replace(0, 0);
				}
			}
		}
	}
	
	private void computeCollisions() {
		// On prend la ninjaBall et on la fait collisionner avec le systeme de particules
		ball = mParticlesSystem.theOne;
		
		more = true;
		for(int count = 0; count < NB_MAX_ITERATIONS && more; ++count) {
			more = false;
			// boucle qui g�re les collisions entre les obstacles et le Ninja
			for(Iterator<Collidable> i = mParticlesSystem.GetObstclesList(); i.hasNext();) {
				Collidable it = i.next();
				
				// S'il y a collision entre les 2 objets
				if(ball.collided(it)) {
					
					if(gameMode == Global.MODE_SURVIVAL) {
						mRun = false;
						finished = Global.END_LOSE;
						return;
					}
					
					// on calcule le produit vectoriel entre le vecteur d�placement de la balle
					// et le vecteur de collision entre les deux �l�ments
					translateVect = new Point(ball.getLastPosX()-ball.getX(), ball.getLastPosY()-ball.getY());
					collideVect = new Point(it.getX()-ball.getX(), it.getY()-ball.getY());
					
					// produit vectoriel entre les 2 vecteurs (r�gle de la main droite)
					prodVect = translateVect.x * collideVect.y - translateVect.y * collideVect.x;
					
					// si le centre de l'obstacle est a gauche du d�placement
					if(prodVect > 0) {
						newX = ball.getX() + translateVect.y;
						newY = ball.getY() - translateVect.x;
					} 
					// si le centre de l'obstacle est � droite du d�placement
					else if (prodVect < 0) {
						newX = ball.getX() - translateVect.y;
						newY = ball.getY() + translateVect.x;
					} 
					// si les 3 points sont allign�s (prodVect == 0)
					else {
						newX = ball.getX() + translateVect.x;
						newY = ball.getY() + translateVect.y;
					}
					
					// on g�rera la perte (ou gain) d'�nerdie plus tard...
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
		// reset l'�cran
		mParticlesSystem.DrawBackground(c);
		
    	// on dessine les obstacles
	    mParticlesSystem.Draw(c, mCanvasDim);
	    
	    // On dessine les shurikens a ramasser
	    mParticlesSystem.DrawShurikens(c);

		// On dessine la balle Ninja en dernier pour que dans le cas ou elle 
	    // chevauche quelque chose elle soit dessin�e par dessus
    	mParticlesSystem.DrawNinja(c);
	    
	    // affiche les fps
		profiler.Draw(c, mCanvasDim);
		stopWatch.Draw(c, 400, 20);
		c.drawText(Integer.toString(shurikensCollected), 5, 15, stringBrush);
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
	
	private void ShowMenu(boolean hasWon, double time) {
		Message msg = msgHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt("mode", Global.MSG_SHOW_MENU);
		b.putBoolean("hasWon", hasWon);
		b.putDouble("time", time);
		msg.setData(b);
		msgHandler.sendMessage(msg);
	}
	
	private void PanicMsg(double time) {
		Message msg = msgHandler.obtainMessage();
		Bundle b =new Bundle();
		b.putInt("mode", Global.MSG_PANIC);
		b.putDouble("time", time);
		msg.setData(b);
		msgHandler.sendMessage(msg);
	}
	
}
