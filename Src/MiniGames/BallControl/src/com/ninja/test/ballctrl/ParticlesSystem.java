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
	private Drawable background;
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
		
		theOne = new NinjaBall(50, 50, 1, context);
		
		SpikesBall = context.getResources().getDrawable(R.drawable.spikes_ball);

		shuriken = context.getResources().getDrawable(R.drawable.b_shuriken);
	}
	
	public Iterator<Collidable> GetObstclesList() {
		return mObstalcesList.iterator();
	}
	
	public Iterator<Collidable> GetCoinsList() {
		return mCoinsList.iterator();
	}
	
	public void Draw(Canvas c, RectF bounds) {
		
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
		
		ninjaB = theOne.getNinja();
		ninjaB.setBounds(theOne.getX() - theOne.getRayon(), 
						 theOne.getY() - theOne.getRayon()-5, 
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
	
	public void DrawBackground(Canvas c) {
		background.setBounds(0, 0, mWidth, mHeight);
		background.draw(c);
	}
	
	public void placeItems() {
		if(!placed) {
			Map map = MapsManager.GetMapFromID(1);
			mObstalcesList = map.mObstaclesList;
			mCoinsList = map.mItemsList;
			background = map.mBackground;
			placed = true;
		}
	}
	
	public void setSurfaceSize(float width, float height) {
		
		// on set la taille de l'écran
		mWidth = (int)width;
		mHeight = (int)height;
		
		scaler();
	}
	
	private void scaler() {
		// comme l'écran est toujours en format paysage (setté dans le xml)
		// on a toujours with > height
		
		// On prend le plus petit coefficient pour que tout l'affichage 
		// rentre dans l'écran ce qui est une bonne idée selon moi ^^
		int tmp = (2*mHeight > mWidth) ? mWidth/2 : mHeight;
		
		// On a ici la taille des Drawable en pixels de manière a en avoir
		// N en y et 2N en x (N = yDiv)
		offset = tmp/Global.yDiv;
		
		Collidable.setOffset(offset);
	}

	public int getOffset() {
		return offset;
	}
	
}
