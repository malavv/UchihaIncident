package com.ninja.test.ballctrl;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ParticlesSystem {
	
	private final int xDiv = 20;
	private final int yDiv = 30;
	
	private ArrayList<Collidable> mObstalcesList;
	
	private Drawable SpikesBall;
	
	// largeur de l'écran
	private float mWidth;

	// hauteur de l'écran
	private float mHeight;

	private final float xScale = mWidth * 1/xDiv;
	private final float yScale = mHeight * 1/yDiv;
	
	public ParticlesSystem( Context context ) {
		
		mObstalcesList = new ArrayList<Collidable>();
				
		// Obstacles list later i'd want it to be loaded form an XML file
		PlaceObstacles();
  	
		Log.d("ParticlesSystem::constructor", "on initialise le drawable de la spikesball");
		
		SpikesBall = context.getResources().getDrawable(R.drawable.ball);
	}
	
	public ArrayList<Collidable> GetObstclesList() {
		return mObstalcesList;
	}
	
	public void Draw(Canvas c, RectF bounds) {
	  	
		Log.d("ParticlesSystem::Draw", "on dessine les particules sur le canvas");
		
		Collidable tmp;
		for(int i = 0; i < mObstalcesList.size(); i++){
			tmp = mObstalcesList.get(i);
			SpikesBall.setBounds((int)(tmp.getY()-yScale), 
								 (int)(tmp.getX()+xScale), 
								 (int)(tmp.getY()+yScale), 
								 (int)(tmp.getX()-xScale));
			SpikesBall.draw(c);
		}
	}
	
	public void setSurfaceSize(float width, float height) {
		mWidth = width;
		mHeight = height;
	}
	
	private void PlaceObstacles() {
	  	
		Log.d("ParticlesSystem::PlaceObstacles", "on place les particules sur le canvas");
		
		Wall test = new Wall(1, 1, 1);
		
		test.setX((float) 1.1);
		
		// place des murs sur les cotés
		for(int i = 0; i < 30; i++) {
			mObstalcesList.add(new Wall(0 + xScale, mHeight * i/yDiv + yScale, 1));
			mObstalcesList.add(new Wall(mWidth + xScale, mHeight * i/yDiv + yScale, 1));
		}

		//place des murs en haut et en bas
		for(int i = 0; i < 20; i++) {
			mObstalcesList.add(new Wall(mWidth * i/xDiv + xScale, 0 + yScale, 1));
			mObstalcesList.add(new Wall(mWidth * i/xDiv + xScale, mHeight + yScale, 1));
		}
		
	}
	
}
