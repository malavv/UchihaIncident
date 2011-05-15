package com.ninja.test.ballctrl;

public class NinjaBall extends Movable {
	
    private float mAccelX;
    private float mAccelY;
    private float mLastPosX;
    private float mLastPosY;

    /** 
     * Constructor for the NinjaBall 
     * with the start position and rebound 
     * coefficient also called elasticity 
     */
	NinjaBall(float aPosX, float aPosY, float aElasticity) {
		super(aPosX, aPosY, aElasticity);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * The same constructor with a default elasticity
	 */
	NinjaBall(float aPosX, float aPosY) {
		super(aPosX, aPosY, (float) 0.9);
		// TODO Auto-generated constructor stub
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

	public float getmLastPosX() {
		return mLastPosX;
	}

	public void setmLastPosX(float mLastPosX) {
		this.mLastPosX = mLastPosX;
	}

	public float getmLastPosY() {
		return mLastPosY;
	}

	public void setmLastPosY(float mLastPosY) {
		this.mLastPosY = mLastPosY;
	}

}
