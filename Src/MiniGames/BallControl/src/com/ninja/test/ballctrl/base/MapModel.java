package com.ninja.test.ballctrl.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.R.string;
import android.graphics.Rect;
import android.util.Log;

/**
 * MapModel est supposé être la classe qui permet d'abstraire le concept
 * de la carte et d'importer ou exporter vers un fichier.
 * 
 * Les fichiers map doivent être placer dans le dossier Raw de ressources.
 * 
 * ex:
 * 0 // Id de map 0
 * 0 // Id du background est 0
 * 22 // La map à 22 de large
 * 5 // La map à 5 de haut.
 */
public class MapModel {
  public static Map<Character, Integer> CharToType = new HashMap<Character, Integer>();
  public int ID, Background;
  public Rect Dimension;
  public ArrayList<MapItem> Items;
  
  public MapModel() {
    ID = Background = 0;
    Dimension = new Rect();
    Items = new ArrayList<MapItem>();
  }
  
  public static final MapModel Import(BufferedReader file) throws NumberFormatException, IOException {
    MapModel map = new MapModel();
    String s = file.readLine();
    Log.d("line", s);
    map.ID = Integer.parseInt(s);
    map.Background = Integer.parseInt(file.readLine());
    map.Dimension.right = Integer.parseInt(file.readLine());
    map.Dimension.top = Integer.parseInt(file.readLine());
    
    for (int i = 0; i < map.Dimension.top; i++) {
      String line = file.readLine();
      for(int j = 0; j < line.length(); j++) {
         Character c = line.charAt(j);
         if (MapModel.CharToType.containsKey(c))
       	  map.Items.add(new MapItem(MapModel.CharToType.get(c), j, map.Dimension.top - i));
      }
    }
	return map;
  }
}
