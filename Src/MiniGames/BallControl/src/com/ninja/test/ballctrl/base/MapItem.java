package com.ninja.test.ballctrl.base;

public class MapItem {
   public MapItem(int type, int x, int y) { Type = type; X = x; Y = y; }
   public final static int wall = 0;
   public final static int start = 1;
   public final static int shuriken = 2;
   public int Type, X, Y;
}
