package com.ninja.test.ballctrl;

import android.hardware.SensorEvent;
import android.view.Display;
import android.view.Surface;




public class NinjaBall extends Movable{
	private float mSensorX;
	private float mSensorY;

    /** 
     * Constructor for the NinjaBall 
     * with the start position and rebound 
     * coefficient also called elasticity 
     */
	NinjaBall(int aPosX, int aPosY, float aElasticity) {
		super(aPosX, aPosY, aElasticity);
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
        float norme = 5;//(float) Math.pow( Math.pow(mSensorX, 2) + Math.pow(mSensorY, 2), 1/2 );
        
        setCoefX(- mSensorX/norme);
        setCoefY(- mSensorY/norme);
	}

}
