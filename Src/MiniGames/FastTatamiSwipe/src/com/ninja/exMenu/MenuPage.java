package com.ninja.exMenu;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * MenuPage représente la première page de l'application celle où l'on
 * reviendra toujours si l'on est en jeu et que l'on appuie sur le
 * bouton back. Elle contient les options majeure. La pluspart des
 * modifications sont à effectuer sur le layout directement.
 */
public class MenuPage extends Activity {
	
	/**
	 * Point de la création dans le cycle de vie, première chance que
	 * l'on as d'initialiser des choses dans l'application.
	 * Le bundle peut contenir de l'information concernant la dernière
	 * utilisation.
	 * @param savedInstanceState Un lot d'information sur la dernière
	 *        utilisation de l'application.
	 */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    /* Il faut toujours, lorsque l'on override, caller l'instance de base. */
  	super.onCreate(savedInstanceState);
    
    /* 
     * Cette ligne indique quelle vue sera utilisé par cette activitée. 
     * Vous trouverez cette vue dans res\layout\.
     */
    setContentView(R.layout.main);
    
    /*
     * Dans Android, le passage d'une activité à l'autre et donc implicitement
     * d'une page à l'autre se fait par "Intent". Un intention peut être directement
     * le nom de la classe ou nous voulons aller comme ici, ou une intention plus
     * générale comme "Une applciation capable de prendre des notes" ce qui laisse
     * à l'utilisateur le choix de choisir l'application de notes qu'il veut.
     */
    final Intent soloMode = new Intent(this, Difficulty.class);
    final Intent dualMode = new Intent(this, Difficulty.class);
    
    GameContext cSolo = new GameContext(), cDual = new GameContext();
    cDual.SetMultiplayer();
    
    soloMode.putExtra("com.ninja.ExMenu.GameContext", cSolo);
    dualMode.putExtra("com.ninja.ExMenu.GameContext", cDual);
    
    
    BindIntentToView(R.id.btnSolo, soloMode);
    BindIntentToView(R.id.btnDual, dualMode);
  }
  
  private void BindIntentToView(final int buttonID, final Intent intent) {
    findViewById(buttonID).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        try {
          startActivity(intent);
        } catch(ActivityNotFoundException e) {
          Log.wtf("Activity started with button " + Integer.toString(buttonID), "Activity not found.");
        }
      }
    });
  }  
}