package com.ninja.test.ballctrl;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

public class Map {
	public ArrayList<Collidable> mObstaclesList;
	
	public ArrayList<Movable> mEnnemiesList;
	
	public ArrayList<Collidable> mItemsList;
	
	public Drawable mBackground;
	
	Map() {
		mObstaclesList = new ArrayList<Collidable>();
		mEnnemiesList = new ArrayList<Movable>();
		mItemsList = new ArrayList<Collidable>();
		mBackground = PlayContentView.sContext.getResources().getDrawable(R.drawable.ingame_wp_1);
	}
}
