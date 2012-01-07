package com.ninja.test.ballctrl;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class ParticlesSystem {
	
	// largeur et hauteur des éléments dessinables
	private int offset;
	
	// liste des éléments de murs
	private ArrayList<Collidable> mObstalcesList;
	
	// liste des éléments de murs
	private ArrayList<Coin> mCoinsList;
	
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
	//private boolean placed;
	
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
		mCoinsList = new ArrayList<Coin>();
		
		theOne = new NinjaBall(250, 120, 2f, context);
		
		SpikesBall = context.getResources().getDrawable(R.drawable.spikes_ball);

		shuriken = context.getResources().getDrawable(R.drawable.b_shuriken);
	}
	
	public Iterator<Collidable> GetObstclesList() {
		return mObstalcesList.iterator();
	}
	
	public int GetObstaclesListeSize() {
		return mObstalcesList.size();
	}
	
	public Iterator<Coin> GetCoinsList() {
		return mCoinsList.iterator();
	}
	
	public int GetCoinsListeSize() {
		return mCoinsList.size();
	}
	
	public void Draw(Canvas c, RectF bounds) {
		
		// On dessine les obstacles
		for(int i = 0; i < mObstalcesList.size(); i++){
			tmp = mObstalcesList.get(i);
			SpikesBall.setBounds((int)tmp.getX() - tmp.getRayon(), 
					(int)tmp.getY() - tmp.getRayon(), 
					(int)tmp.getX() + tmp.getRayon(), 
					(int)tmp.getY() + tmp.getRayon() );

			SpikesBall.draw(c);
		}
	}
	
	public void MoveNinja(float delta) {
		
		// Puis on calcule le déplacement des objets notemment le Ninja
		theOne.computePhysics(delta);
	}
	
	public void DrawNinja(Canvas c) {
		
		ninjaB = theOne.getNinja();
		ninjaB.setBounds((int)theOne.getX() - theOne.getRayon(), 
				(int)theOne.getY() - theOne.getRayon()-5, 
				(int)theOne.getX() + theOne.getRayon(), 
				(int)theOne.getY() + theOne.getRayon() );

		//BlkSquare.draw(c);
		ninjaB.draw(c);
	}
	
	public void DrawShurikens(Canvas c) {
		// On dessine les obstacles
		for(int i = 0; i < mCoinsList.size(); i++){
			tmp = mCoinsList.get(i);
			if(tmp.isActive()) {
				shuriken.setBounds((int)tmp.getX() - tmp.getRayon(), 
						(int)tmp.getY() - tmp.getRayon(), 
						(int)tmp.getX() + tmp.getRayon(), 
						(int)tmp.getY() + tmp.getRayon() );
				
				shuriken.draw(c);
			}
		}
	}
	
	public void DrawBackground(Canvas c) {
		background.setBounds(0, 0, mWidth, mHeight);
		background.draw(c);
	}
	
	public void placeItems() {
		MapsManager.Init();
		Map map = MapsManager.GetMapFromFile("test");
		//Map map = MapsManager.GetMapFromID(0);
		mObstalcesList = map.mObstaclesList;
		mCoinsList = map.mItemsList;
		background = map.mBackground;
		theOne.setX(map.mNinjaInitPos.x);
		theOne.setY(map.mNinjaInitPos.y);
	}
	
	public void enableAllCoins() {
		for (Coin iter : mCoinsList) {
			iter.setActive(true);
		}
	}
	
	public void setSurfaceSize(float width, float height) {
		
		// on set la taille de l'écran
		mWidth = (int)width;
		mHeight = (int)height;
		
		scaler();
	}
	
	public float getHeight() {
		return mHeight;
	}
	
	public float getWidth() {
		return mWidth;
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
