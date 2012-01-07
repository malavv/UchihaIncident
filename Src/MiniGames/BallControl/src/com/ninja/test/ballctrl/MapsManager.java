package com.ninja.test.ballctrl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Point;

import com.ninja.test.ballctrl.base.MapItem;
import com.ninja.test.ballctrl.base.MapModel;

public class MapsManager {
	private static Resources res_ = null;
	private static String package_ = null;
	private static Point bounds;
	private static boolean boundSet = false;
	private static ArrayList<Map> mapList = new ArrayList<Map>();
	
	public static void Init() {
	  res_ = PlayContentView.sContext.getResources();
	  package_ = PlayContentView.sContext.getPackageName();
	  MapModel.CharToType.put('W', MapItem.wall);
	  MapModel.CharToType.put('X', MapItem.start);
	  MapModel.CharToType.put('S', MapItem.shuriken);	
	}
	
	public static Map GetMapFromFile(String name) {
      if (res_ == null)  Init();
		
	  int id = PlayContentView.sContext.getResources().getIdentifier(name, "raw", package_);
      if (id == 0)  throw new IllegalArgumentException("Le fichier map voulu n'existe pas.");
	
      MapModel map = null;
      try {
         BufferedReader data = new BufferedReader(new InputStreamReader(res_.openRawResource(id)));
		 if (!data.ready())  throw new IOException("Unable to open the map file");
		 map = MapModel.Import(data);
		 data.close();
	  } catch (IOException e) { e.printStackTrace(); }
      
      Map createdMap = new Map();
      for (MapItem item : map.Items) {
    	int xPos = item.X * Collidable.getOffset();
    	int yPos = bounds.y - item.Y * Collidable.getOffset();
    	
        switch (item.Type) {
          case MapItem.wall:
        	createdMap.mObstaclesList.add(new Wall(xPos, yPos, 1));
        	break;
          case MapItem.start:
        	  createdMap.mNinjaInitPos.x = xPos;
        	  createdMap.mNinjaInitPos.y = yPos;
        	break;
          case MapItem.shuriken:
        	  createdMap.mItemsList.add(new Coin(xPos, yPos, 1));
        	  break;
        }
      }
      return createdMap;
      
      /*
      try {
		String maps[] = null;
		// si la liste de maps est vide, on utilise la map par défault
		if(maps == null) {
			Log.d("MapLoading", "Could not get Maps");
			mapList.add(testMap1());
			mapList.add(testMap2());
		}
	
		Map map = null;
		BufferedReader reader = null;
		String line = null;
		
		// loop sur tous les fichier maps existants
		for(String iter : maps) {
			try {
				reader = new BufferedReader(new InputStreamReader(ass.open(iter.toString())));
			
				map = new Map();
				// loop sur toutes les lignes
				for(int i = 0; i < Global.yDiv; i++) {
					try {
						line = reader.readLine();
					} catch (IOException e) {
						Log.d("MapLoading", "Could not read line");
					}
					
					// loop sur toutes les colonnes d'une ligne (tous les charactères)
					for(int j = 0 ; j < Global.xDiv; j++) {
						parseMapContent(map, line.charAt(j), j, i );
					}
					
				}
				mapList.add(map);
			} catch (IOException e) {
				Log.d("MapLoading", "Could not open file");
			}
		}
		
		if(mapList.isEmpty()) {
			mapList.add(testMap1());
			mapList.add(testMap2());
		}
		*/
	}
	
	public static Map GetMapFromID(int id) {
		return mapList.get(id);
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
	
	/**
	 * @Nomenclature :
	 * W = mur normal
	 * N = position initiale du ninja
	 * S = position initiale d'un des shuriken
	 * @param c
	 */
	private static void parseMapContent(Map map, char c, int line, int collumn) {
		int nLine = line*Global.yDiv;
		int nColl = collumn*Global.xDiv;
		
		switch(c) {
		case 'W':
		case 'w':
			map.mObstaclesList.add(new Wall(nColl, nLine, 1));
			break;
		case 'O':
		case 'o':
			map.mNinjaInitPos.x = nColl;
			map.mNinjaInitPos.y = nLine;
			break;
		case 'S':
		case 's':
			map.mItemsList.add(new Coin(nColl, nLine, 1));
			break;
		}
	}

}
