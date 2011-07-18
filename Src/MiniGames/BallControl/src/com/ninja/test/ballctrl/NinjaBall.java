package com.ninja.test.ballctrl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.SensorEvent;
import android.view.Display;
import android.view.Surface;




public class NinjaBall extends Movable{
	private float mSensorX;
	private float mSensorY;
	
	private Drawable front;
	private Drawable back;
	private Drawable right;
	private Drawable left;
	
    /** 
     * Constructor for the NinjaBall 
     * with the start position and rebound 
     * coefficient also called elasticity 
     */
	NinjaBall(int aPosX, int aPosY, float aElasticity, Context context) {
		super(aPosX, aPosY, aElasticity);
		front = context.getResources().getDrawable(R.drawable.front);
		back = context.getResources().getDrawable(R.drawable.back);
		right = context.getResources().getDrawable(R.drawable.right);
		left = context.getResources().getDrawable(R.drawable.left);
	}
	
	public Drawable getNinja() {
		switch(orientation) {
		case Global.FRONT:
			return front;
		case Global.BACK:
			return back;
		case Global.LEFT:
			return left;
		default: // Global.RIGHT
			return right;
		}
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
