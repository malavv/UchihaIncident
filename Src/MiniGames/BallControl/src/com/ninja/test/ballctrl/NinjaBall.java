package com.ninja.test.ballctrl;

import android.hardware.SensorEvent;
import android.util.Log;
import android.view.Display;
import android.view.Surface;




public class NinjaBall extends Movable{
	
    private float mAccelX;
    private float mAccelY;
    private int mLastPosX;
    private int mLastPosY;
	private float mSensorX;
	private float mSensorY;

    /** 
     * Constructor for the NinjaBall 
     * with the start position and rebound 
     * coefficient also called elasticity 
     */
	NinjaBall(int aPosX, int aPosY, float aElasticity) {
		super(aPosX, aPosY, aElasticity);
		mLastPosX = aPosX;
		mLastPosY = aPosY;
	}
	
	public void onAccelerometerEvent(SensorEvent event, Display display) {

		Log.d("Accelerometre", "value 0 = " + Integer.toString((int)event.values[0]) + "\nvalue 1 = " + Integer.toString((int)event.values[1]));
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                mSensorX = event.values[0];
                mSensorY = event.values[1];
                break;
            case Surface.ROTATION_90:
                mSensorX = -event.values[1];
                mSensorY = event.values[0];
                break;
            case Surface.ROTATION_180:
                mSensorX = -event.values[0];
                mSensorY = -event.values[1];
                break;
            case Surface.ROTATION_270:
                mSensorX = event.values[1];
                mSensorY = -event.values[0];
                break;
        }
        
//        mAccelX = event.values[0];
//        mAccelY = event.values[1];
        mAccelX = - mSensorX/10000;
        mAccelY = - mSensorY/10000;
	}

	public void computePhysics(long delta) {
		
		long deltaS = delta*delta;
		float ax = 1f/2 * mAccelX * deltaS;
		float vx = (getX() - mLastPosX) * delta;
		float x = getX() + vx + ax;
		float y = getY() + (getY() - mLastPosY) * delta + 1f/2 * mAccelY * deltaS;
		
		mLastPosX = getX();
		mLastPosY = getY();
		
		setX((int)x);
		setY((int)y);
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
