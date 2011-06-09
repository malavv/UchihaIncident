package com.ninja.exMenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Difficulty extends ListActivity {

   /** Cette valeure est le numéro d'id à unlocker pour les défaults.*/
   private static final int kDefaultOpponents = -1;
   /** Cette définition contient les fields du hashmap décrivant l'opponent. */
   private static final String[] IdTagXml =
       new String[] {"name", "dots", "speed", "nImgId", "nTimes"};
   /** Ceci contient, en ordre les associations entre les strings et les view */
   private static final int[] associatedIdLayout = new int[] {R.id.opp_name,
         R.id.opp_balle, R.id.opp_speed, R.id.opp_times};
   /** 
    * Ceci est une énumération des id des image pour faire l'association rapide
    * entre le nombre d'essaie et son image correspondante.
    */
   private static final int[] idImgNumber = new int[] { R.drawable.dif_n_1,
         R.drawable.dif_n_2, R.drawable.dif_n_3, R.drawable.dif_n_4,
         R.drawable.dif_n_5, R.drawable.dif_n_6};
  
   private GameContext infos;
   
   private List<HashMap<String, String>> mOpponents;
   
  
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
  	  setContentView(R.layout.difficulty);
  	
      infos = getIntent().getParcelableExtra("com.ninja.ExMenu.GameContext");
   }
  
   @Override
   public void onResume() { 
     super.onResume();
     SoundManager.PlayBGMusic();
     ArrayList<OpponentInfo> opponents = GameContext.FetchKnownOpponents(this);
     infos.SetKnownOpponents(opponents);

     mOpponents = fetchOpponentsFromFile(infos.IsSingle());
     setListAdapter(new SimpleAdapter(getApplicationContext(), mOpponents,
         R.layout.opponents, IdTagXml, associatedIdLayout));
   }
   
   @Override
   public void onPause() {
     super.onPause();
     SoundManager.PauseBGMusic();
   }
   
   
   @Override
   protected void onListItemClick (ListView l, View v, int position, long id) {
      HashMap<String, String> clickedOpponent = mOpponents.get((int)id);
      
      int oId = Integer.parseInt(clickedOpponent.get("id"));
      String name = clickedOpponent.get("name");
      int nTime = Integer.parseInt(clickedOpponent.get("nTimes"));
      int nDot = Integer.parseInt(clickedOpponent.get("dots"));
      int speed = (clickedOpponent.get("speed").equals("N/A"))
            ? -1 : Integer.parseInt(clickedOpponent.get("speed"));

      infos.SetOpponent(new Opponent(oId, name, nDot, speed, nTime));
      final Intent toPlay = new Intent(this, PlayContent.class);
      toPlay.putExtra("com.ninja.ExMenu.GameContext", infos);
      try {
         startActivity(toPlay);
      } catch(ActivityNotFoundException e) {
        Log.wtf("Play content activity from difficulty failed.", "Activity not found.");
      }
   }
 
  
  private List<HashMap<String, String>> fetchOpponentsFromFile(boolean isSingle) {
    List<HashMap<String, String>> fillmap = new ArrayList<HashMap<String, String>>();
    XmlResourceParser parser = getResources().getXml(R.xml.opponents);
    
    try {
      while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
        if (parser.getEventType() == XmlPullParser.START_TAG) {
          if ((parser.getName()).equalsIgnoreCase("Opponent"))
            processOpponent(parser, fillmap, isSingle);
          else parser.next();
        } else parser.next();
      }     
      
    } catch (XmlPullParserException e) {
      throw new RuntimeException("Could not parse opponent xml");
    } catch (IOException e) {
      throw new RuntimeException("Could not parse opponent xml");
    } finally {
      parser.close();
    }
    return fillmap;
  }
  
   private void processOpponent(XmlResourceParser parser,
         List<HashMap<String, String>> opponents, boolean isSingle)
         throws XmlPullParserException, IOException {
    
      ArrayList<Integer> unlockedList = new ArrayList<Integer>();
      int id = Integer.parseInt(parser.getAttributeValue(0));
      HashMap<String, String> opponent = makeOpponent(id,
         parser.getAttributeValue(1),
         parser.getAttributeValue(2),
         (isSingle) ? parser.getAttributeValue(3) : "N/A",
         parser.getAttributeValue(4));
      
     parser.next(); // Goto unlocked
     
     do {
       parser.next(); // Goto ID
       if (!parser.getName().equalsIgnoreCase("ID")) break;
       parser.next(); // Text
       unlockedList.add(Integer.parseInt(parser.getText())); // Enter text
       parser.next(); // EndID
     } while (true);
     
     if (unlockedList.contains(kDefaultOpponents)) {
       opponents.add(opponent);
       return;
     }
     
     for (OpponentInfo info : infos.GetKnownOpponents()) {
       if (unlockedList.contains(info.Id))  unlockedList.remove(info.Id);
     }
     
     if (unlockedList.size() < 1)
       opponents.add(opponent);
  }
  
  private HashMap<String, String> makeOpponent(int id, String name, String dots, String speed, String time) {
    HashMap<String, String> opp = new HashMap<String, String>();
    opp.put(IdTagXml[0], name);
    opp.put(IdTagXml[1], dots);
    opp.put(IdTagXml[2], speed);
    opp.put(IdTagXml[3], Integer.toString(idImgNumber[Integer.parseInt(time) - 1]));
    opp.put(IdTagXml[4], time);
    opp.put("id", Integer.toString(id));
    return opp;
  }
}
