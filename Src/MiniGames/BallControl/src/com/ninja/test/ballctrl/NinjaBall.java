package com.ninja.test.ballctrl;

import android.hardware.SensorEvent;
import android.util.Log;
import android.view.Display;
import android.view.Surface;




public class NinjaBall extends Movable{
	
    private float mCoefX;
    private float mCoefY;
    private int mLastPosX;
    private int mLastPosY;
	private float mSensorX;
	private float mSensorY;
	
	private int speed = 10;

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

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                mSensorX = event.values[0];
                mSensorY = -event.values[1];
                break;
            case Surface.ROTATION_90:
                mSensorX = -event.values[1];
                mSensorY = -event.values[0];
                break;
            case Surface.ROTATION_180:
                mSensorX = -event.values[0];
                mSensorY = event.values[1];
                break;
            case Surface.ROTATION_270:
                mSensorX = event.values[1];
                mSensorY = event.values[0];
                break;
        }
        // on normalise la vitesse en x,y
        float norme = (float) Math.pow( Math.pow(mSensorX, 2) + Math.pow(mSensorY, 2), 1/2 );
        
        mCoefX = - mSensorX/norme;
        mCoefY = - mSensorY/norme;
	}

	/**
	 * Calcule de la position et de la vitesse
	 * @param delta - différentiel de temps
	 */
	public void computePhysics(long delta) {
		
		// La vitesse est constante, ce qui change c'est uniquement la direction
		// dans laquelle on se déplace.
		float x = getX() + speed * delta * mCoefX;
		float y = getY() + speed * delta * mCoefY;
		
		// on récupère l'ancienne position en x,y avant de la mettre à jours
		mLastPosX = getX();
		mLastPosY = getY();
		
		setX((int)x);
		setY((int)y);
	}
	
	/**
	 * met a jours la position courrante avec celle passée en parametre et
	 * l'ancienne position avec celle qu'on veut updater
	 * @param x
	 * @param y
	 */
	public void updatePosition(int x, int y) {
		mLastPosX = getX();
		mLastPosY = getY();
		setX(x);
		setY(y);
	}
	
	/**
	 * 
	 * @param x - vitesse sur l'ave des x
	 * @param y - vitesse sur l'axe des y
	 */
	public void updateSpeed(float x, float y) {
		mCoefX = x;
		mCoefY = y;
	}

	public float getCoefX() {
		return mCoefX;
	}

	public void setCoefX(float mCoefX) {
		this.mCoefX = mCoefX;
	}
	
	public void invCoefX() {
		mCoefX *= -1;
	}

	public float getCoefY() {
		return mCoefY;
	}

	public void setCoefY(float mCoefY) {
		this.mCoefY = mCoefY;
	}
	
	public void invCoefY() {
		mCoefY *= -1;
	}

	public int getLastPosX() {
		return mLastPosX;
	}

	public void setLastPosX(int mLastPosX) {
		this.mLastPosX = mLastPosX;
	}

	public int getLastPosY() {
		return mLastPosY;
	}

	public void setLastPosY(int mLastPosY) {
		this.mLastPosY = mLastPosY;
	}

}
