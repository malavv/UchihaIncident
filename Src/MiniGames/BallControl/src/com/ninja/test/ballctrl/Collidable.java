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

	public float getmPosX() {
		return mPosX;
	}

	public void setmPosX(float mPosX) {
		this.mPosX = mPosX;
	}

	public float getmPosY() {
		return mPosY;
	}

	public void setmPosY(float mPosY) {
		this.mPosY = mPosY;
	}

	public float getmElasticity() {
		return mElasticity;
	}

}
