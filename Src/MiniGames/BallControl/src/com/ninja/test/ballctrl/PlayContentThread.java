package com.ninja.test.ballctrl;

import java.util.Iterator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
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
	 * Pour éviter certains bogues, on considèrera le temps entre 2 images constant
	 * la valeur permet de controler la vitesse de déplacement du Ninja
	 */
	private float delta = 0.8f;

	private Paint stringBrush;
	private Paint blackBrush;
	private final static int stringColor = Color.WHITE;

	private static final int kRun = 0;
	private static final int kFinished = 1;
	private static final int kReady = 2;

	// Varialbles utiles dans la fonctions computeCollisions
//	private int prodVect;
	private int newX;
	private int newY;
	private PointF translateVect;
	private PointF collideVect;
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
		blackBrush = new Paint();
	    mCanvasDim = new RectF();  
	    stringBrush.setColor(stringColor);
	    blackBrush.setColor(Color.BLACK);
	    
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
		    	//delta = profiler.Delta();
		    	
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
	    	gameMode = new ModeTimed();
	    	break;
	    case Global.MODE_SURVIVAL:
	    	gameMode = new ModeSurvival();
	    	break;
	    default:
	    	gameMode = new ModeNormal(mParticlesSystem.GetCoinsListeSize());
	    }
	}
	
	private int computeCollisionWithBounds() {
		
		rayonN = ball.getRayon();
		boolean collided = false;
		int bound = Global.BOUND_NONE;
		
		// collision avec le bord de l'écran
		if(ball.getX()-rayonN < mCanvasDim.left){
			ball.setLastPosX(ball.getX());
			ball.setX((int) (mCanvasDim.left + rayonN));
			collided = true;
			bound = Global.BOUND_LEFT;
		} else if(ball.getX()+rayonN > mCanvasDim.right) {
			ball.setLastPosX(ball.getX());
			ball.setX((int) (mCanvasDim.right - rayonN));
			collided = true;
			bound = Global.BOUND_RIGHT;
		}
		if(ball.getY()-rayonN < mCanvasDim.top){
			ball.setLastPosY(ball.getY());
			ball.setY((int) (mCanvasDim.top + rayonN));
			collided = true;
			bound = Global.BOUND_TOP;
		} else if(ball.getY()+rayonN > mCanvasDim.bottom) {
			ball.setLastPosY(ball.getY());
			ball.setY((int) (mCanvasDim.bottom - rayonN));
			collided = true;
			bound = Global.BOUND_BOTTOM;
		}
		
		// some game mode might want to do something here
		if(collided) {
			gameMode.DoCollideBounds();
		}
		
		return bound;
	}
	
	private void findShuriken() {
		
		for(Iterator<Coin> i = mParticlesSystem.GetCoinsList(); i.hasNext();) {
			Coin it = i.next();
			
			if(it.isActive() && ball.collided(it)) {
				shurikensCollected++;
				
				gameMode.foundShuriken(ball, it, mCanvasDim, 
						mParticlesSystem.GetObstclesList());
			}
		}
	}
	
	private PointF vectorialSymmetry(PointF translateVect, PointF collideVect) {
		// on calcule le symétrique du vecteur de translation par rapport 
		// au vecteur de collision , le calcul exacte est le suivant
		// 2*scalaire(translateVect, collideVect)/norme(collideVect)
		//  * collideVect/norme(collideVect) - translateVect 

		// norme du vecteur de collision
		double n_collideVect = Math.sqrt(collideVect.x*collideVect.x + collideVect.y*collideVect.y);
		
		// produit scalaire des 2 vecteurs
		float scalar = translateVect.x*collideVect.x + translateVect.y*collideVect.y;
		
		// vecteur donnant la position suivante par rapport
		// au point d'impact
		PointF res = new PointF();
		res.x = (float) (2*scalar/n_collideVect/n_collideVect*collideVect.x);
		res.y = (float) (2*scalar/n_collideVect/n_collideVect*collideVect.y);
		
		return res;
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
										
					translateVect = new PointF(ball.getLastPosX()-ball.getX(), ball.getLastPosY()-ball.getY());
					collideVect = new PointF(ball.getX()-it.getX(), ball.getY()-it.getY());
					
					PointF res = vectorialSymmetry(translateVect, collideVect);
					
					// l'élasticité ne fonctionne pas exactement comme je le veux
					// mais le résultat est suffisant pour le moment
					res.x *= ball.getElasticity()*it.getElasticity();
					res.y *= ball.getElasticity()*it.getElasticity();
					
					newX = (int) (res.x + ball.getX());
					newY = (int) (res.y + ball.getY());
					
					ball.updatePosition(newX, newY);
				}
			}
			translateVect = new PointF(ball.getLastPosX()-ball.getX(), ball.getLastPosY()-ball.getY());
			PointF res = new PointF();
			
			int collided = computeCollisionWithBounds();
			
			switch(collided) {
			case Global.BOUND_NONE :
				break;
			case Global.BOUND_BOTTOM :
				collideVect = new PointF(0, 1);
				res = vectorialSymmetry(translateVect, collideVect);
				break;
			case Global.BOUND_LEFT :
				collideVect = new PointF(1, 0);
				res = vectorialSymmetry(translateVect, collideVect);
				break;
			case Global.BOUND_RIGHT :
				collideVect = new PointF(1, 0);
				res = vectorialSymmetry(translateVect, collideVect);
				break;
			case Global.BOUND_TOP :
				collideVect = new PointF(0, 1);
				res = vectorialSymmetry(translateVect, collideVect);
				break;
			}
			
			if(collided > Global.BOUND_NONE) {
				res.x *= ball.getElasticity();
				res.y *= ball.getElasticity();
				
				newX = (int) (res.x + ball.getX());
				newY = (int) (res.y + ball.getY());
				
				ball.updatePosition(newX, newY);
				more = false;
			}
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
		int offset = Collidable.getOffset();
    	c.drawRect(0, (Global.yDiv-1)*offset, (Global.xDiv-1)*offset, mParticlesSystem.getHeight(), blackBrush);
    	c.drawRect((Global.xDiv-1)*offset, 0, mParticlesSystem.getWidth(), mParticlesSystem.getHeight(), blackBrush);
		profiler.Draw(c, mCanvasDim);
		gameMode.DrawTime(c, 400, Global.yDiv*offset);
		c.drawText("shuriken : " + Integer.toString(shurikensCollected), 60, Global.yDiv*offset, stringBrush);
//		c.drawLine(0, (Global.yDiv-1)*offset, (Global.xDiv-1)*offset, (Global.yDiv-1)*offset, stringBrush);
//		c.drawLine((Global.xDiv-1)*offset, 0, (Global.xDiv-1)*offset, (Global.yDiv-1)*offset, stringBrush);
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
