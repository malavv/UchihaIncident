package com.ninja.test.ballctrl;

public class Collidable {
    private float mPosX;
    private float mPosY;
    private float mElasticity;
    
    Collidable(float aPosX, float aPosY, float aElasticity) {
    	mPosX = aPosX;
    	mPosY = aPosY;
    	mElasticity = aElasticity;
    }

	public float getX() {
		return mPosX;
	}

	public void setX(float mPosX) {
		this.mPosX = mPosX;
	}

	public float getY() {
		return mPosY;
	}

	public void setY(float mPosY) {
		this.mPosY = mPosY;
	}

	public float getmElasticity() {
		return mElasticity;
	}

}
