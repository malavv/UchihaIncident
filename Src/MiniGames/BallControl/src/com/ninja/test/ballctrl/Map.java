package com.ninja.test.ballctrl;

import java.util.ArrayList;

import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class Map {
	public ArrayList<Collidable> mObstaclesList;
	
	public ArrayList<Movable> mEnnemiesList;
	
	public ArrayList<Coin> mItemsList;
	
	public Drawable mBackground;
	
	public Point mNinjaInitPos;
	
	Map() {
		mObstaclesList = new ArrayList<Collidable>();
		mEnnemiesList = new ArrayList<Movable>();
		mItemsList = new ArrayList<Coin>();
		mNinjaInitPos = new Point(0, 0);
		mBackground = PlayContentView.sContext.getResources().getDrawable(R.drawable.ingame_wp_1);
	}
}
