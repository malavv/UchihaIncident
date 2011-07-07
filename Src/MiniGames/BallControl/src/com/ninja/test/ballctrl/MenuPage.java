package com.ninja.test.ballctrl;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuPage  extends Activity {
	
	static int gameMode;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.menu);
        
        /*
         * Dans Android, le passage d'une activité à l'autre et donc implicitement
         * d'une page à l'autre se fait par "Intent". Un intention peut être directement
         * le nom de la classe ou nous voulons aller comme ici, ou une intention plus
         * générale comme "Une applciation capable de prendre des notes" ce qui laisse
         * à l'utilisateur le choix de choisir l'application de notes qu'il veut.
         */
        final Intent moveToPlay = new Intent(this, PlayContent.class);
        
        buttonOption(moveToPlay, R.id.Normal, 0);
        buttonOption(moveToPlay, R.id.Timed, 1);
        buttonOption(moveToPlay, R.id.Survival, 2);
        buttonOption(moveToPlay, R.id.Help_Credits, 3);
    }

	private void buttonOption(final Intent intent, final int id, final int mode) {

        ((Button)findViewById(id)).setOnClickListener(new OnClickListener() {
    	    public void onClick(View v) {
    			  gameMode = mode;
    			  try {
    			    startActivity(intent);
    			  } catch(ActivityNotFoundException e) {
    			    Log.wtf("Front Screen start button click.", "Activity not found.");
    			  }
    			}
    		});
	}

}
