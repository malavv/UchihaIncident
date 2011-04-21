package com.ninja.exMenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

/**
 * This class will control everything that is visible after pressing
 * play in the main menu.
 */
public class PlayContent extends Activity {

	private boolean IsFirstTouch = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.content);
    }
    
	public boolean onTouchEvent(View v, MotionEvent event) {
		if (!IsFirstTouch) {
		  IsFirstTouch = false;
		  return true;
		}
		
		return true;
	}

}
