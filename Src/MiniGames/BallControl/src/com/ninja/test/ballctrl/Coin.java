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
		int offset = Collidable.getOffset();
		boolean retry;
		do {
			x = (int) (Math.random() * (Global.xDiv-2)) * offset + offset;
			y = (int) (Math.random() * (Global.yDiv-2)) * offset + offset;
			c = new Collidable(x, y, 1);
			retry = false;
			for(Iterator<Collidable> i = obstacles; i.hasNext();){
				Collidable it = i.next();
				if(it.getX() == c.getX()-offset/2 && it.getY() == c.getY()-offset/2){
					Log.d("DEBUG", "EQUALITY");
					retry = true;
				}
			}
		}while(retry);
		
		setX(x);
		setY(y);
	}

}
