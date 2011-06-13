package com.ninja.exMenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
    
  /** L'identifiant du boutton option dans le menu en jeu. */
  private static final int kOptionMenu = 1;
    
  /** L'identifiant du boutton option dans le menu en jeu. */
  private static final int kPauseMenu = 2;
  
  /** L'identifiant du boutton option dans le menu en jeu. */
  private static final int kResumeMenu = 3;
  
  /** Niveau de difficulté du jeux */
  private static int kDifficulty = 1;
  
	
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

	public static void setkDifficulty(int kDifficulty) {
		PlayContent.kDifficulty = kDifficulty;
	}
	
	public static int getkDifficulty() {
		return kDifficulty;
	}
}
