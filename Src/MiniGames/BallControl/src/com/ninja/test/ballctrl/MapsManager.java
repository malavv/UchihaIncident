package com.ninja.test.ballctrl;

import android.graphics.Point;

public class MapsManager {
	private static Point bounds;
	private static boolean boundSet = false;
	
	public static void GetMapFromFile(String name) {
		//TODO : faire l'implémetation de cette fonction.
	}
	
	public static Map GetMapFromID(int id) {
		switch(id) {
		case 0 :
			return testMap1();
		case 1 :
			return testMap2();
		default :
			return testMap1();
		}
	}
	
	public static Map testMap1() {
		Map map = new Map();
		
		// Ajouts d'obstacles
		map.mObstaclesList.add(new Wall(50, 50, 1));
		map.mObstaclesList.add(new Wall(150, 150, 1));
		map.mObstaclesList.add(new Wall(50, 150, 1));
		map.mObstaclesList.add(new Wall(150, 50, 1));
		
		// Ajout d'objets à collecter
		map.mItemsList.add(new Coin(100, 100, 1));
		map.mItemsList.add(new Coin(200, 200, 1));
		map.mItemsList.add(new Coin(100, 200, 1));
		map.mItemsList.add(new Coin(200, 100, 1));
		
		// Ajout d'objets mobiles
		//map.mEnnemiesList.add(new NinjaBall(135, 135, 1));

		// Position initialie du ninja
		map.mNinjaInitPos.x = 250;
		map.mNinjaInitPos.y = 120;
		
		return map;
	}
	
	public static Map testMap2() {
		Map map = new Map();
		int offset = Collidable.getOffset();
		// place des murs sur les cotés
		for(int i = 0; i < Global.yDiv; i++) {
			map.mObstaclesList.add(new Wall(0, i*offset, 1));
			map.mObstaclesList.add(new Wall(bounds.x,i*offset, 1));
		}

		//place des murs en haut et en bas
		for(int i = 0; i < Global.xDiv-1; i++) {
			map.mObstaclesList.add(new Wall(i*offset, 0, 1));
			map.mObstaclesList.add(new Wall(i*offset, bounds.y, 1));
		}
		
		// Ajouts d'obstacles
		map.mObstaclesList.add(new Wall(50, 50, 1));
		map.mObstaclesList.add(new Wall(150, 150, 1));
		map.mObstaclesList.add(new Wall(50, 150, 1));
		map.mObstaclesList.add(new Wall(150, 50, 1));
		
		// Ajout d'objets à collecter
		map.mItemsList.add(new Coin(100, 100, 1));
		map.mItemsList.add(new Coin(200, 200, 1));
		map.mItemsList.add(new Coin(100, 200, 1));
		map.mItemsList.add(new Coin(200, 100, 1));
		
		// Ajout d'objets mobiles
		//map.mEnnemiesList.add(new NinjaBall(135, 135, 1));

		// Position initialie du ninja
		map.mNinjaInitPos.x = 250;
		map.mNinjaInitPos.y = 120;
		
		return map;
	}

	public static void setBounds(int offset) {
		if(!boundSet) {
			bounds = new Point();
			bounds.x = offset*(Global.xDiv-1);
			bounds.y = offset*(Global.yDiv-1);
			boundSet = true;
		}
	}

	public static Point getBounds() {
		return bounds;
	}

}
