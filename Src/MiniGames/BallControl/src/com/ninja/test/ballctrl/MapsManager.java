package com.ninja.test.ballctrl;

import java.util.ArrayList;

public class MapsManager {
	
	private static ArrayList<Map> mapList;
	
	public static void GetMapFromFile(String name) {
		//TODO : faire l'implémetation de cette fonction.
	}
	
	public static Map GetMapFromID(int id) {
		if(id > mapList.size()) {
			return GetMapFromID(0);
		}
		return testMap1();
	}
	
	public static void LoadMaps() {
		Map map = new Map();
		mapList.add(map);
	}
	
	public static Map testMap1() {
		Map map = new Map();
		
		// Ajouts d'obstacles
		//map.mObstaclesList.add(new Wall(50, 50, 1));
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
		
		return map;
	}

}
