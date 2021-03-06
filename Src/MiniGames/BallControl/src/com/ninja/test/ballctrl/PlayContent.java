package com.ninja.test.ballctrl;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public class PlayContent extends Activity implements SensorEventListener{
	
	private PlayContentView mContentView;
	private PlayContentThread mContentThread;
	   
    private Display mDisplay;
    private WindowManager mWindowManager;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
	  
	/** L'identifiant du boutton option dans le menu en jeu. */
	private static final int kResumeMenu = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
	   	setContentView(R.layout.content);
		
		mContentView = (PlayContentView) findViewById(R.id.pc_view);
		mContentThread = mContentView.getThread();
		mContentView.SetActivity(this);
         
		mWindowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
		mDisplay = mWindowManager.getDefaultDisplay();
	}
    
	  public void BackToMenu() {
	    finish();
	  }
	  
	  public void Retry() {
		    mContentThread.FreshStart();
	  }
    
	  /** Cr�ation du menu et des options qui s'y retrouve. */
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    
	    menu.add(0, kResumeMenu, 0, R.string.menu_resume);
	        
	    return true;
	  }
	  
	  @Override
	  public boolean onPrepareOptionsMenu (Menu menu) {
		  mContentThread.pause();
		  return true;
	  }
	  
	  @Override
	  public void onOptionsMenuClosed (Menu menu) {
		  Resume();
	  }
	    
	  /**
	   * Actions � prendre d�pendament de l'option choisit dans le menu.
	   */
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	  	  case kResumeMenu:
	  	}
	    return true;
	  }
	    
	  /**
	   * Phase du cycle de vie ou nous ne sommes probablement plus visible et
	   * pr�t � �tre mis hors de m�moire.
	   */
	  @Override
	  public void onPause() {
	    super.onPause();
	    
        mSensorManager.unregisterListener(this);
	    Pause();
	  }
	    
	  /**
	   * Finalement, on es revenu du onPause et apparemment, Yay, Android nous
	   * dis que nos informations sont suppps� encore �tre en m�moire.
	   */
	  @Override
	  public void onResume() {
	    super.onResume();
	    
	    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
	    Resume();
	  }
	    
	  /** M�thode priv� permettant de g�r� l'�v�nement pause. */
	  private void Pause() {  mContentThread.pause();  }
	    
	  /** M�thode priv� permettant de g�r� l'�v�nement resum�. */
	  private void Resume() {  mContentThread.unpause();  }
	    
	  /**
	   * M�thode que nous donne Android pour nous permettre de sauvegarder notre
	   * �tat en m�moire juste avant que notre application se fasse fermer pour
	   * lib�r� de la m�moire.
	   */
	  @Override
	  protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	  }
	    
	  @Override
	  public void onStop() {
	    super.onStop();
        mSensorManager.unregisterListener(this);
	    mContentThread.Panic();
	  }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
        
		mContentView.mContentThread.mParticlesSystem.theOne.onAccelerometerEvent(event, mDisplay);
		
	}
	
}
