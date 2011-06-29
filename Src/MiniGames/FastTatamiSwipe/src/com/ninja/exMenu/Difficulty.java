package com.ninja.exMenu;

import android.app.Dialog;
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

public class Difficulty extends ListActivity {

   private static final int[] kImgId = new int[] { 0, R.drawable.dif_n_1,
     R.drawable.dif_n_2, R.drawable.dif_n_3, R.drawable.dif_n_4,
     R.drawable.dif_n_5, R.drawable.dif_n_6 };
   
   private int facebookPrompt_ = -1;
   private int statsPrompt_ = -1;
   private static final int kStatsID = 0;
  
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
  	  setContentView(R.layout.difficulty);
  	  
  	  RegisterDialogs();
   }
  
   @Override
   public void onResume() { 
     super.onResume();
     SoundManager.PlayBGMusic();
     setListAdapter(new OpponentAdapter(getApplicationContext()));
     DialogManager.SetNewParent(this, findViewById(R.layout.difficulty));
   }
   
   @Override
   public void onPause() {
     super.onPause();
     SoundManager.PauseBGMusic();
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
     menu.add(R.string.diag_stats_title);
     return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
     DialogManager.Get(statsPrompt_).show();
     return true;
   }
      
   @Override
   protected void onListItemClick (ListView l, View v, int position, long id) {
     GameContext.SetCurrentPlayer((int)id);
     
     try { startActivity(new Intent(this, PlayContent.class)); }
     catch(ActivityNotFoundException e) {
       Log.wtf("Play content activity from difficulty failed.", "Activity not found.");
     }
   }
  
   private void RegisterDialogs() {
     if (DialogManager.Get(facebookPrompt_) == null)  RegisterFB();
     if (DialogManager.Get(statsPrompt_) == null)  RegisterStats();
   }
   
   private void RegisterStats() {
     Dialog d = new Dialog(this);
     d.setTitle(R.string.diag_stats_title);
     d.setContentView(R.layout.dialog_stats);
     statsPrompt_ = DialogManager.Push(d);
   }
   
   private void RegisterFB() {
     Dialog d = new Dialog(this);
     d.setTitle("Facebook");
     facebookPrompt_ = DialogManager.Push(d);
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
}
