package com.ninja.test.ballctrl;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuPage  extends Activity {
	
	public static int gameMode;
	private static final int kThirtySec = 0;
	private static final int kOneMin = 1;
	private static final int kTwoMin = 2;
	private static final int kFiveMin = 5;
	private static final int kTenMin = 10;
	
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
        
        buttonOption(moveToPlay, R.id.Normal, Global.MODE_NORMAL);
        buttonOption(moveToPlay, R.id.Timed, Global.MODE_TIMED);
        buttonOption(moveToPlay, R.id.Survival, Global.MODE_SURVIVAL);
        buttonOption(moveToPlay, R.id.Help_Credits, Global.MODE_CREDITS);
    }

	private void buttonOption(final Intent intent, final int id, final int mode) {

        ((Button)findViewById(id)).setOnClickListener(new OnClickListener() {
    	    public void onClick(View v) {
    			  gameMode = mode;
    			  if (mode == Global.MODE_TIMED) {
    				  openOptionsMenu();
    			  } else {
    				  try {
	    			    startActivity(intent);
	    			  } catch(ActivityNotFoundException e) {
	    			    Log.wtf("Front Screen start button click.", "Activity not found.");
	    			  }
    			  }
    			}
    		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    
	    menu.add(0, kThirtySec, 0, "0\'30");
	    menu.add(0, kOneMin, 0, "1\'00");
	    menu.add(0, kTwoMin, 0, "2\'00");
	    menu.add(0, kFiveMin, 0, "5\'00");
	    menu.add(0, kTenMin, 0, "10\'00");
	        
	    return true;
	}

	  /**
	   * Actions à prendre dépendament de l'option choisit dans le menu.
	   */
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
		  switch (item.getItemId()) {
		  case kThirtySec: 
			  ModeTimed.limit = 30;
			  break;
		  case kOneMin: 
			  ModeTimed.limit = 60;
			  break;
		  case kTwoMin: 
			  ModeTimed.limit = 2*60;
			  break;
		  case kFiveMin: 
			  ModeTimed.limit = 5*60;
			  break;
		  case kTenMin: 
			  ModeTimed.limit = 10*60;
			  break;
		  }

		  try {
		    startActivity(new Intent(this, PlayContent.class));
		  } catch(ActivityNotFoundException e) {
		    Log.wtf("Front Screen start button click.", "Activity not found.");
		  }
		  
		  return false;
	  }

}
