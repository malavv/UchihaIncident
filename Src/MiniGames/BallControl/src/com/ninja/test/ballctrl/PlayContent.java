package com.ninja.test.ballctrl;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PlayContent extends Activity {
	
	private PlayContentView mContentView;
	private PlayContentThread mContentThread;
    
	/** L'identifiant du boutton option dans le menu en jeu. */
	private static final int kOptionMenu = 1;
	    
	/** L'identifiant du boutton option dans le menu en jeu. */
	private static final int kPauseMenu = 2;
	  
	/** L'identifiant du boutton option dans le menu en jeu. */
	private static final int kResumeMenu = 3;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
  	
		Log.d("PlayContent::onCreate", "on cree l'activite");
		
	   	setContentView(R.layout.content);
		
		mContentView = (PlayContentView) findViewById(R.id.ContentView);
		mContentThread = mContentView.getThread();
	}
    
	  /**
	   * Création du menu et des options qui s'y retrouve.
	   */
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	        
	    menu.add(0, kOptionMenu, 0, R.string.menu_options);
	    menu.add(0, kPauseMenu, 0, R.string.menu_pause);
	    menu.add(0, kResumeMenu, 0, R.string.menu_resume);
	        
	    return true;
	  }
	    
	  /**
	   * Actions à prendre dépendament de l'option choisit dans le menu.
	   */
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	  	  case 1:  return true;
	  	  case 2:
	        Pause();
	  		return true;
	  	  case 3:
	  		Resume();
	  		return true;
	  	}
	    return false;
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
	    
	  /**
	   * Méthode que nous donne Android pour nous permettre de sauvegarder notre
	   * état en mémoire juste avant que notre application se fasse fermer pour
	   * libéré de la mémoire.
	   */
	  @Override
	  protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	  }
	    
	  @Override
	  public void onStop() {
	    super.onStop();
	    mContentThread.Panic();
	  }
	
}
