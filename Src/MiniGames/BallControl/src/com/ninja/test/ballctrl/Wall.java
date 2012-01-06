package com.ninja.test.ballctrl;

public class Wall extends Collidable {

	/**
	 * Wall constructor
	 * @param aPosX
	 * @param aPosY
	 * @param aElasticity
	 */
	Wall(int aPosX, int aPosY, float aElasticity) {
		super(aPosX, aPosY, .5f);
	}

}
