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
	
	// largeur et hauteur des �l�ments dessinables
	private int offset;
	
	// liste des �l�ments de murs
	private ArrayList<Collidable> mObstalcesList;
	
	// liste des �l�ments de murs
	private ArrayList<Collidable> mCoinsList;
	
	// Objets qui seront dessin�s � l'�cran
	private Drawable SpikesBall;
	private Drawable ninjaB;
	private Drawable shuriken;
	private Drawable background;
	public NinjaBall theOne;
	
	// largeur de l'�cran
	private int mWidth;

	// hauteur de l'�cran
	private int mHeight;
	
	// permet de savoir si les �l�ments du mur on d�j� �t� dessin�s ou pas
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
		
		// Puis on calcule le d�placement des objets notemment le Ninja
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
		
		// on set la taille de l'�cran
		mWidth = (int)width;
		mHeight = (int)height;
		
		scaler();
	}
	
	private void scaler() {
		// comme l'�cran est toujours en format paysage (sett� dans le xml)
		// on a toujours with > height
		
		// On prend le plus petit coefficient pour que tout l'affichage 
		// rentre dans l'�cran ce qui est une bonne id�e selon moi ^^
		int tmp = (2*mHeight > mWidth) ? mWidth/2 : mHeight;
		
		// On a ici la taille des Drawable en pixels de mani�re a en avoir
		// N en y et 2N en x (N = yDiv)
		offset = tmp/Global.yDiv;
		
		Collidable.setOffset(offset);
	}

	public int getOffset() {
		return offset;
	}
	
}
