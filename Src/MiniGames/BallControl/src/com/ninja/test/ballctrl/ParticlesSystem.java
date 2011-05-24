package com.ninja.test.ballctrl;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.Surface;
import android.view.SurfaceHolder;

public class ParticlesSystem {
	
	private ArrayList<Collidable> mObstalcesList;
	
	private Drawable SpikesBall;
	
	// largeur de l'écran
	private float mWidth;

	// hauteur de l'écran
	private float mHeight;
	
	public ParticlesSystem( Context context ) {
				
		// Obstacles list later i'd want it to be loaded form an XML file
		PlaceObstacles();
		
		SpikesBall = context.getResources().getDrawable(R.drawable.ball);
	}
	
	public ArrayList<Collidable> GetObstclesList() {
		return mObstalcesList;
	}
	
	public void Draw(Canvas c, RectF bounds) {
		Collidable tempObstacle;
		for(int i = 0; i < mObstalcesList.size(); i++){
			tempObstacle = mObstalcesList.get(i);
			SpikesBall.
			SpikesBall.draw(c);
		}
	}
	
	public void setSurfaceSize(float width, float height) {
		
	}
	
	private void PlaceObstacles() {
		
		// place des murs sur les cotés
		for(int i = 0; i < 30; i++) {
			mObstalcesList.add(new Wall(0, mHeight * i/30, 1));
			mObstalcesList.add(new Wall(mWidth, mHeight * i/30, 1));
		}

		//place des murs en haut et en bas
		for(int i = 0; i < 20; i++) {
			mObstalcesList.add(new Wall(mWidth * i/20, 0, 1));
			mObstalcesList.add(new Wall(mWidth * i/20, mHeight, 1));
		}
		
	}
	
}
