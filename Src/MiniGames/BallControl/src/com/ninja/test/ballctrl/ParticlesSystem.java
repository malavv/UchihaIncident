package com.ninja.test.ballctrl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ParticlesSystem {
	
	private static final int NB_SHURIKEN = 5;
	// nombre d'éléments du mur dans la largeur (x) et dans la hauteur (y)
	private final int xDiv = 26;
	private final int yDiv = 13;
	
	// largeur et hauteur des éléments dessinables
	private int offset;
	
	// liste des éléments de murs
	private ArrayList<Collidable> mObstalcesList;
	
	// liste des éléments de murs
	private ArrayList<Collidable> mCoinsList;
	
	// Objets qui seront dessinés à l'écran
	private Drawable SpikesBall;
	private Drawable ninjaB;
	private Drawable shuriken;
	public NinjaBall theOne;
	
	// largeur de l'écran
	private int mWidth;

	// hauteur de l'écran
	private int mHeight;
	
	// permet de savoir si les éléments du mur on déjà été dessinés ou pas
	private boolean placed;
	
	// Variable temporaire pour les objets collisionables
	private Collidable tmp;
	
	/*
	 * Screen looks like (on a Nexus): 
	 * 
	 * 				533 px (~ 1)
	 * _________________________________
	 * |0--------> x					|
	 * ||								|
	 * ||								|
	 * ||								| 270 px
	 * ||								| (~ 1/2)
	 * |V y								|
	 * |								|
	 * |________________________________|
	 */
	
	public ParticlesSystem( Context context ) {
		
		mObstalcesList = new ArrayList<Collidable>();
		mCoinsList = new ArrayList<Collidable>();
		
		ninjaB = context.getResources().getDrawable(R.drawable.scared_ball);
		theOne = new NinjaBall(50, 50, 1);
		
		SpikesBall = context.getResources().getDrawable(R.drawable.spikes_ball);

		shuriken = context.getResources().getDrawable(R.drawable.w_shuriken);
	}
	
	public Iterator<Collidable> GetObstclesList() {
		return mObstalcesList.iterator();
	}
	
	public Iterator<Collidable> GetCoinsList() {
		return mCoinsList.iterator();
	}
	
	public void Draw(Canvas c, RectF bounds) {
	  	
		Log.d("ParticlesSystem::Draw", "on dessine les particules sur le canvas");
		
		// On dessine les obstacles
		for(int i = 0; i < mObstalcesList.size(); i++){
			tmp = mObstalcesList.get(i);
			SpikesBall.setBounds(tmp.getX() - tmp.getRayon(), 
								 tmp.getY() - tmp.getRayon(), 
								 tmp.getX() + tmp.getRayon(), 
								 tmp.getY() + tmp.getRayon() );

			SpikesBall.draw(c);
		}
	}
	
	public void MoveNinja(long delta) {
		
		// Puis on calcule le déplacement des objets notemment le Ninja
		theOne.computePhysics(delta/10);
	}
	
	public void DrawNinja(Canvas c) {
		
		ninjaB.setBounds(theOne.getX() - theOne.getRayon(), 
						 theOne.getY() - theOne.getRayon(), 
						 theOne.getX() + theOne.getRayon(), 
						 theOne.getY() + theOne.getRayon() );

		//BlkSquare.draw(c);
		ninjaB.draw(c);
	}
	
	public void DrawShurikens(Canvas c) {
		// On dessine les obstacles
		for(int i = 0; i < mCoinsList.size(); i++){
			tmp = mCoinsList.get(i);
			shuriken.setBounds(tmp.getX() - tmp.getRayon(), 
					 tmp.getY() - tmp.getRayon(), 
					 tmp.getX() + tmp.getRayon(), 
					 tmp.getY() + tmp.getRayon() );
			
			shuriken.draw(c);
		}
	}
	
	public void setSurfaceSize(float width, float height) {
		
		// on set la taille de l'écran et on places les éléments statiques
		// mais on s'assure de ne l'effectuer qu'une seule fois
		if(!placed) {
			mWidth = (int)width;
			mHeight = (int)height;
			
			scaler();
	
			// Obstacles list later i'd want it to be loaded form an XML file
			PlaceObstacles();
			
			PlaceShurikens();
			
			placed = true;
		}
	}
	
	private void scaler() {
		// comme l'écran est toujours en format paysage (setté dans le xml)
		// on a toujours with > height
		
		// On prend le plus petit coefficient pour que tout l'affichage 
		// rentre dans l'écran ce qui est une bonne idée selon moi ^^
		int tmp = (2*mHeight > mWidth) ? mWidth/2 : mHeight;
		
		// On a ici la taille des Drawable en pixels de manière a en avoir
		// N en y et 2N en x (N = yDiv)
		offset = tmp/yDiv;
		
		Collidable.setOffset(offset);
	}
	
	private void PlaceShurikens() {
		for(int i = 0; i < NB_SHURIKEN; i++) {
			int x = 10*i + 5;//(int) (Math.random() % mWidth);
			int y = 10*i + 5;//(int) (Math.random() % mWidth);
			
			mCoinsList.add(new Coin(x, y, 1));
		}
	}
	
	private void PlaceObstacles() {
		
		// Position maximale ou on va placer un élément collisionable 
		// d'une dimention offset*offset
//		final int maxWidth = offset*(xDiv-1);
//		final int maxHeight = offset*(yDiv-1);
		
		// place des murs sur les cotés
		/*for(int i = 0; i < yDiv; i++) {
			mObstalcesList.add(new Wall(0, i*offset, 1));
			mObstalcesList.add(new Wall(maxWidth,i*offset, 1));
		}

		//place des murs en haut et en bas
		for(int i = 0; i < xDiv-1; i++) {
			mObstalcesList.add(new Wall(i*offset, 0, 1));
			mObstalcesList.add(new Wall(i*offset, maxHeight, 1));
		}*/

		// 4 points random pour remplire un peu l'écran
		mObstalcesList.add(new Wall(100, 100, 1));
		mObstalcesList.add(new Wall(200, 200, 1));
		mObstalcesList.add(new Wall(200, 100, 1));
		mObstalcesList.add(new Wall(100, 200, 1));
			
		
	}

	public int getOffset() {
		return offset;
	}
	
}
