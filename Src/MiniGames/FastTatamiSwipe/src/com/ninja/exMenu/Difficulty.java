package com.ninja.exMenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.R.integer;
import android.app.ListActivity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.widget.SimpleAdapter;

public class Difficulty extends ListActivity {

  private static final String[] IdTagXml =
     new String[] {"name", "dots", "speed", "times"};
  private static final int[] associatedIdLayout =
     new int[] { R.id.opp_name, R.id.opp_balle, R.id.opp_speed, R.id.opp_times };
  private static final int[] idImgNumber =
     new int[] { R.drawable.dif_n_1, R.drawable.dif_n_2, R.drawable.dif_n_3,
                 R.drawable.dif_n_4, R.drawable.dif_n_5, R.drawable.dif_n_6};
  private GameContext info;
  
	/**
	 * @param 
	 */
  @Override
  public void onCreate(Bundle savedInstanceState) {
  	super.onCreate(savedInstanceState);
  	
  	
  	// Trouver le context global.
  	info = getIntent().getParcelableExtra("com.ninja.ExMenu.GameContext");
    setContentView(R.layout.difficulty);
    
    setListAdapter(new SimpleAdapter(getApplicationContext(), GetOpponents(), R.layout.opponents, IdTagXml, associatedIdLayout));
  }
  
  private List<HashMap<String, String>> GetOpponents() {
    List<HashMap<String, String>> fillmap = new ArrayList<HashMap<String, String>>();
    XmlResourceParser parser = getResources().getXml(R.xml.opponents);
    ArrayList<Integer> unlockedIds = new ArrayList<Integer>();
    
    try {
      while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
        if (parser.getEventType() == XmlPullParser.START_TAG) {
          if ((parser.getName()).equalsIgnoreCase("Opponent"))
            processOpponent(parser, unlockedIds, fillmap);
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
        ArrayList<Integer> unlockedIDs, List<HashMap<String, String>> opponents) throws XmlPullParserException, IOException {
     int id = Integer.parseInt(parser.getAttributeValue(0));
     HashMap<String, String> opponent = makeOpponent(
        parser.getAttributeValue(1),
        parser.getAttributeValue(2),
        parser.getAttributeValue(3),
        parser.getAttributeValue(4));
      
     parser.next(); // Goto unlocked
     
     do {
       parser.next(); // Goto ID
       if (!parser.getName().equalsIgnoreCase("ID")) break;
       parser.next(); // Text
       unlockedIDs.add(Integer.parseInt(parser.getText())); // Enter text
       parser.next(); // EndID
     } while (true);
     
     if (unlockedIDs.contains(id))  opponents.add(opponent);
  }
  
  private HashMap<String, String> makeOpponent(String name, String dots, String speed, String time) {
    HashMap<String, String> opp = new HashMap<String, String>();
    opp.put(IdTagXml[0], name);
    opp.put(IdTagXml[1], dots);
    opp.put(IdTagXml[2], speed);
    opp.put(IdTagXml[3], Integer.toString(idImgNumber[Integer.parseInt(time) - 1]));
    return opp;
  }
}
