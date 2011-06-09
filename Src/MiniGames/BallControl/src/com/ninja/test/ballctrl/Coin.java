package com.ninja.test.ballctrl;

public class Coin extends Collidable {

	Coin(int aPosX, int aPosY, float aElasticity) {
		super(aPosX, aPosY, aElasticity);
		// TODO Auto-generated constructor stub
	}
	
	public void replace(int Xmax, int Ymax) {
		int x = (int) (Math.random() *Xmax );
		int y = (int) (Math.random() *Ymax);
		
		setX(x);
		setY(y);
	}

}
