package com.ninja.exMenu;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class Difficulty extends ListActivity {

   private static final int[] kImgId = new int[] { 0, R.drawable.dif_n_1,
     R.drawable.dif_n_2, R.drawable.dif_n_3, R.drawable.dif_n_4,
     R.drawable.dif_n_5, R.drawable.dif_n_6};
  
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
  	  setContentView(R.layout.difficulty);
   }
  
   @Override
   public void onResume() { 
     super.onResume();
     SoundManager.PlayBGMusic();
     setListAdapter(new OpponentAdapter(getApplicationContext()));
   }
   
   @Override
   public void onPause() {
     super.onPause();
     SoundManager.PauseBGMusic();
   }
      
   @Override
   protected void onListItemClick (ListView l, View v, int position, long id) {
     GameContext.SetCurrentPlayer((int)id);
     
     try { startActivity(new Intent(this, PlayContent.class)); }
     catch(ActivityNotFoundException e) {
       Log.wtf("Play content activity from difficulty failed.", "Activity not found.");
     }
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
     super.onCreateOptionsMenu(menu);
     menu.add(0, 0, 0, "FB");
     return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
     switch (item.getItemId()) {
       case 0: FbLogin();  return true;
     }
     return false;
   }
   
   private void FbLogin() {
     final Facebook fb = new Facebook(GameContext.kFbAppId);
     Session.waitForAuthCallback(fb);
     fb.authorize(this, GameContext.kFbAppPermissions, new AppLoginListener(fb, this));
   }
  
   private class OpponentAdapter extends BaseAdapter {

      private LayoutInflater mInflater_;
      public OpponentAdapter(Context c) { mInflater_ = LayoutInflater.from(c); }
      @Override
      public int getCount() { return GameContext.GetNVisible(); }
      @Override
      public Object getItem(int index) { return GameContext.Get(index); }
      @Override
      public long getItemId(int position) { return GameContext.Get(position).getID(); }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        Opponent op = GameContext.GetVisible(position);
        View view;

        if (convertView == null)
          view = mInflater_.inflate(R.layout.opponents, parent, false);
        else  view = convertView;
        
        ImageView nTries = (ImageView)view.findViewById(R.id.opp_times);
        TextView name = (TextView)view.findViewById(R.id.opp_name);
        TextView nDots = (TextView)view.findViewById(R.id.opp_balle);
        TextView time = (TextView)view.findViewById(R.id.opp_speed);
        
        nTries.setImageResource(kImgId[op.getNTimes()]);
        name.setText(op.getSenseiName());
        nDots.setText(Integer.toString(op.getNDots()));
        time.setText(Integer.toString(op.getLengthMs()));
        
        if (op.getIsDefeated())
          name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else
          name.setPaintFlags(name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        
        // Null tag means the view has the correct data
        view.setTag(null);
        return view;
      }
   }
   
   
   private class AppLoginListener implements DialogListener {
      
      private Facebook fb_;
      private Activity act_;

      public AppLoginListener(Facebook fb, Activity act) {
        fb_ = fb; act_ = act;
      }

      public void onCancel() {
         Log.d("app", "login canceled");
      }

      public void onComplete(Bundle values) {
         /**
          * We request the user's info so we can cache it locally and
          * use it to render the new html snippets
          * when the user updates her status or comments on a post. 
          */
         new AsyncFacebookRunner(fb_).request("/me", 
            new AsyncRequestListener() {
               public void onComplete(JSONObject obj, final Object state) {
                  // save the session data
                  String uid = obj.optString("id");
                  String name = obj.optString("name");
                  new Session(fb_, uid, name).save(act_);

                  // render the Stream page in the UI thread
                  Log.d("fb", "Nous sommes maintenant connecté.");
               }
            }, null
         );
         
         new AsyncFacebookRunner(fb_).request("/me/friends", 
             new AsyncRequestListener() {
                public void onComplete(JSONObject obj, final Object state) {
                   // save the session data
                   String uid = obj.optString("id");
                   String name = obj.optString("name");
                   new Session(fb_, uid, name).save(act_);

                   // render the Stream page in the UI thread
                   Log.d("fb", "Nous sommes maintenant connecté.");
                }
             }, null
          );
         
         Bundle params = new Bundle();
         params.putString("message", "Maxime Lavigne did an awesome job at beating Harend Muary in combat training.");
         
         try {
             Util.parseJson(fb_.request("/me/feed", params, "POST"));
         } catch (FacebookError e) {
             Log.d("Tests", "*" + e.getMessage() + "*");
             if (!e.getMessage().equals("(#200) The user hasn't " +
                 "authorized the application to perform this action")) {
             }
         } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (MalformedURLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      
   @Override
     public void onError(DialogError e) {
         Log.d("app", "dialog error: " + e);               
     }
   @Override
     public void onFacebookError(FacebookError e) {
         Log.d("app", "facebook error: " + e);
     }
  }
}
