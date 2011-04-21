package com.ninja.exMenu;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * This class will control everything that is visible after pressing
 * play in the main menu.
 */
public class PlayContent extends Activity {
	
    /** A handle to the thread that's actually running the animation. */
    private PlayContentThread mContentThread;

    /** A handle to the View in which the game is running. */
    private PlayContentView mContentView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.content);

        // get handles to the LunarView from XML, and its LunarThread
    	//mContentView = (PlayContentView) findViewById(R.id.ContentView);
    	//mContentThread = mContentView.getThread();

        // give the LunarView a handle to the TextView used for messages
    	//mContentView.setTextView((TextView) findViewById(R.id.Text));
    }
}
