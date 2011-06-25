package com.ninja.test.ballctrl;

import java.util.ArrayList;

public class Map {
	public ArrayList<Collidable> mObstaclesList;
	
	public ArrayList<Movable> mEnnemiesList;
	
	public ArrayList<Collidable> mItemsList;
	
	Map() {
		mObstaclesList = new ArrayList<Collidable>();
		mEnnemiesList = new ArrayList<Movable>();
		mItemsList = new ArrayList<Collidable>();
	}
}
