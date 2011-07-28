package com.ninja.test.ballctrl;

public class Coin extends Collidable {

	Coin(int aPosX, int aPosY, float aElasticity) {
		super(aPosX, aPosY, aElasticity);
		active = true;
	}
	
	// Variables de positions
	private int x;
	private int y;
	
	public void replace(int Xmax, int Ymax) {
		x = (int) (Math.random() *Xmax );
		y = (int) (Math.random() *Ymax);
		
		setX(x);
		setY(y);
	}

}
