package com.ninja.exMenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * PlayContent est l'activité/page qui contient toute la logique nécessaire
 * à faire fonctionner le minigame. Elle contient principalement un thread
 * sous-jacent à la logique et une surface faisant l'abstraction des inputs
 * et des outputs.
 */
public class PlayContent extends Activity {
	
  /** Le thread sur lequel se passe la physique et les animations. */
  private PlayContentThread mContentThread;

  /** La vue s'occupant de tout le input/output. */
  private PlayContentView mContentView;
  	
  /**
   * Phase du cycle de vie correspondant à l'initialisation.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    /* Full screen */
    requestWindowFeature(Window.FEATURE_NO_TITLE); 
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    
    
   	setContentView(R.layout.content);
    
    // On va chercher la référence des trois éléments.
    mContentView = (PlayContentView) findViewById(R.id.ContentView);
    mContentThread = mContentView.getThread();
    mContentView.SetActivity(this);
    
    mContentView.mTextStatus = (TextView)findViewById(R.id.Status);    
  }
    
  public void BackToMenu() {
    finish();
  }
  
  public void Retry() {
    mContentThread.FreshStart();
  }
  
  /**
   * Phase du cycle de vie ou nous ne sommes probablement plus visible et
   * prêt à être mis hors de mémoire.
   */
  @Override
  public void onPause() {
    super.onPause();
    Pause();
  }
    
  /**
   * Finalement, on es revenu du onPause et apparemment, Yay, Android nous
   * dis que nos informations sont supppsé encore être en mémoire.
   */
  @Override
  public void onResume() {
    super.onResume();
    Resume();
  }
    
  /** Méthode privé permettant de géré l'événement pause. */
  private void Pause() {  mContentThread.pause();  }
    
  /** Méthode privé permettant de géré l'événement resumé. */
  private void Resume() {  mContentThread.unpause();  }
    
  @Override
  public void onStop() {
    super.onStop();
    mContentThread.Panic();
  }
}
