package com.ninja.test.ballctrl;

import java.util.Iterator;

import android.util.Log;

public class Coin extends Collidable {

	Coin(int aPosX, int aPosY, float aElasticity) {
		super(aPosX, aPosY, aElasticity);
		active = true;
	}
	
	// Variables de positions
	private int x;
	private int y;
	
	public void replace(int Xmax, int Ymax, Iterator<Collidable> obstacles) {
		Collidable c;
		boolean retry;
		do {
			x = (int) (Math.random() * (Global.xDiv-2)) * Collidable.getOffset() + Collidable.getOffset();
			y = (int) (Math.random() * (Global.yDiv-2)) * Collidable.getOffset() + Collidable.getOffset();
			c = new Collidable(x, y, 1);
			retry = false;
			for(Iterator<Collidable> i = obstacles; i.hasNext();){
				Collidable it = i.next();
				if(it.equals(c)){
					Log.d("DEBUG", "EQUALITY");
					retry = true;
				}
			}
		}while(retry);
		
		setX(x);
		setY(y);
	}

}
