package com.ninja.exMenu;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FrontScreen extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button = (Button)findViewById(R.id.btnStart);
        button.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
	  Intent intent = new Intent(this, PlayContent.class);
	  
	  try {
	      startActivity(intent);
	  } catch(ActivityNotFoundException e) {
	      Log.wtf("Front Screen start button click.", "Activity not found.");
	  }
	}
}