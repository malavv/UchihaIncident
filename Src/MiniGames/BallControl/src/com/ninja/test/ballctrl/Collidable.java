package com.ninja.test.ballctrl;

public class Collidable {
    private int mPosX;
    private int mPosY;
    private float mElasticity;
    
    Collidable(int aPosX, int aPosY, float aElasticity) {
    	mPosX = aPosX;
    	mPosY = aPosY;
    	mElasticity = aElasticity;
    }

	public int getX() {
		return mPosX;
	}

	public void setX(int mPosX) {
		this.mPosX = mPosX;
	}

	public int getY() {
		return mPosY;
	}

	public void setY(int mPosY) {
		this.mPosY = mPosY;
	}

	public float getmElasticity() {
		return mElasticity;
	}

}
