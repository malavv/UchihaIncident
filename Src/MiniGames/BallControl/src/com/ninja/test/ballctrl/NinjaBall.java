package com.ninja.test.ballctrl;


public class NinjaBall extends Movable {
	
    private float mAccelX;
    private float mAccelY;
    private int mLastPosX;
    private int mLastPosY;

    /** 
     * Constructor for the NinjaBall 
     * with the start position and rebound 
     * coefficient also called elasticity 
     */
	NinjaBall(int aPosX, int aPosY, float aElasticity) {
		super(aPosX, aPosY, aElasticity);
	}

	public float getmAccelX() {
		return mAccelX;
	}

	public void setmAccelX(float mAccelX) {
		this.mAccelX = mAccelX;
	}

	public float getmAccelY() {
		return mAccelY;
	}

	public void setmAccelY(float mAccelY) {
		this.mAccelY = mAccelY;
	}

	public int getmLastPosX() {
		return mLastPosX;
	}

	public void setmLastPosX(int mLastPosX) {
		this.mLastPosX = mLastPosX;
	}

	public int getmLastPosY() {
		return mLastPosY;
	}

	public void setmLastPosY(int mLastPosY) {
		this.mLastPosY = mLastPosY;
	}

}
