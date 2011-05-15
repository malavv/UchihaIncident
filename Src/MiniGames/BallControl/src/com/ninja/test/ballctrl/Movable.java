package com.ninja.test.ballctrl;

// Interface for movable objects. Those who implements this interface will be refresh every single frame
// for a matter of implementation, we assume that anything movable is also collidable.
public class Movable extends Collidable{

	Movable(float aPosX, float aPosY, float aElasticity) {
		super(aPosX, aPosY, aElasticity);
		// TODO Auto-generated constructor stub
	}

}
