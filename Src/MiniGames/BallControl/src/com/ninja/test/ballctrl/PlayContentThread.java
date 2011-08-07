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

	private static final int kRun = 0;
	private static final int kFinished = 1;
	private static final int kReady = 2;

	// Varialbles utiles dans la fonctions computeCollisions
	private int prodVect;
	private int newX;
	private int newY;
	private Point translateVect;
	private Point collideVect;
	private boolean more;
	final int NB_MAX_ITERATIONS = 10;
	private GameMode gameMode;
	
	// Variable pour la fonction computeCollisionWithBounds
	private int rayonN;

	private Handler msgHandler;

	private int mode;
	
	public PlayContentThread(SurfaceHolder surface, Handler hMsg) {
	    mSurfaceHolder = surface;
	    msgHandler = hMsg;
	    
		mParticlesSystem = new ParticlesSystem( PlayContentView.sContext );
		stringBrush = new Paint();
	    mCanvasDim = new RectF();  
	    stringBrush.setColor(stringColor);
	    
	    profiler = new Profiler();
	}
	
	public void FreshStart() {
		gameMode.resetGame();
		mParticlesSystem.placeItems();
		mParticlesSystem.enableAllCoins();
		ball.resetSpeed();
		shurikensCollected = 0;
		StopWatch.Instance().Start();
	    mode = kRun;
	}
	
	public boolean isGameFinished() {
		return gameMode.isGameFinished();
	}
	
	public void Panic() {
		pause();
		//SaveTheWorld.saveGame(mParticlesSystem);
		//PanicMsg(stopWatch.Diff());
	}

	public void pause() {
		StopWatch.Instance().Pause();
		mode = kReady;
		//SaveTheWorld.saveGame(mParticlesSystem);
	}
	
	public void unpause() {
		profiler.Tick();
		StopWatch.Instance().Resume();
		mode = kRun;
		//SaveTheWorld.loadGame(PlayContentView.sContext);
    	//profiler.Tick();
	}
	
	public void stopGame() {
		mode = kFinished;
	}
	
	@Override
	public void run() {
		Canvas c;
		mParticlesSystem.placeItems();
		mParticlesSystem.enableAllCoins();
		createMode();
		StopWatch.Instance().Start();
		while (mRun) {
			switch(mode) {
			case kRun:
				profiler.Tick();
		    	delta = profiler.Delta();
		    	
		    	// délai nécessaire pour que la balle ne commence pas a bouger 
		    	// avant que l'écran ne soit affiché pour la première fois
		    	if(StopWatch.Instance().Diff() > 0.2)
		    		mParticlesSystem.MoveNinja(delta);
		    	
				computeCollisions();
				c = null;
				try {
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {doDraw(c);}
				}finally {
					if (c != null) mSurfaceHolder.unlockCanvasAndPost(c);
				}
				if(gameMode.isGameFinished())
					mode = kFinished;
				break;
			case kFinished:
				ShowMenu();
				mode = kReady;
				break;
			case kReady:
				break;
			}
		    	
		}
	}
	
	private void createMode() {
	    switch(MenuPage.gameMode) {
	    case Global.MODE_NORMAL:
	    	gameMode = new ModeNormal(mParticlesSystem.GetCoinsListeSize());
	    	break;
	    case Global.MODE_TIMED:
	    	gameMode = new ModeTimed(30);
	    	break;
	    case Global.MODE_SURVIVAL:
	    	gameMode = new ModeSurvival();
	    	break;
	    default:
	    	gameMode = new ModeNormal(mParticlesSystem.GetCoinsListeSize());
	    }
	}
	
	private void computeCollisionWithBounds() {
		
		rayonN = ball.getRayon();
		boolean collided = false;
		
		// collision avec le bord de l'écran
		if(ball.getX()-rayonN < mCanvasDim.left){
			ball.setLastPosX(ball.getX());
			ball.setX((int) (mCanvasDim.left + rayonN));
			collided = true;
		} else if(ball.getX()+rayonN > mCanvasDim.right) {
			ball.setLastPosX(ball.getX());
			ball.setX((int) (mCanvasDim.right - rayonN));
			collided = true;
		}
		if(ball.getY()-rayonN < mCanvasDim.top){
			ball.setLastPosY(ball.getY());
			ball.setY((int) (mCanvasDim.top + rayonN));
			collided = true;
		} else if(ball.getY()+rayonN > mCanvasDim.bottom) {
			ball.setLastPosY(ball.getY());
			ball.setY((int) (mCanvasDim.bottom - rayonN));
			collided = true;
		}
		
		// some game mode might want to do something here
		if(collided) {
			gameMode.DoCollideBounds();
		}
	}
	
	private void findShuriken() {
		
		for(Iterator<Coin> i = mParticlesSystem.GetCoinsList(); i.hasNext();) {
			Coin it = i.next();
			
			if(it.isActive() && ball.collided(it)) {
				shurikensCollected++;
				
				gameMode.foundShuriken(ball, it, mCanvasDim);
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
					
					gameMode.DoCollidedWall();
										
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
		// reset l'écran
		mParticlesSystem.DrawBackground(c);
		
    	// on dessine les obstacles
	    mParticlesSystem.Draw(c, mCanvasDim);
	    
	    // On dessine les shurikens a ramasser
	    mParticlesSystem.DrawShurikens(c);

		// On dessine la balle Ninja en dernier pour que dans le cas ou elle 
	    // chevauche quelque chose elle soit dessinée par dessus
    	mParticlesSystem.DrawNinja(c);
	    
	    // affiche les fps
		profiler.Draw(c, mCanvasDim);
		StopWatch.Instance().Draw(c, 400, 20);
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
	
	private void ShowMenu() {
		Message msg = msgHandler.obtainMessage();
		Bundle b = gameMode.DoEndingMessage( shurikensCollected);
		msg.setData(b);
		msgHandler.sendMessage(msg);
	}
	
}
