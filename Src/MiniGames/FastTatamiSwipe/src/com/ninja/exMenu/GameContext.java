package com.ninja.exMenu;

import java.util.ArrayList;
import java.util.PriorityQueue;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;


public class GameContext implements Parcelable {
   
   private static final String kPrefFile = "preferences";
   
   private ArrayList<OpponentInfo> knownOpponent_;
  
   private Opponent mOpponent;
   
   private byte modeSolo;
  
   /**
    * Méthode static permettant à java de paquetter et dépaquetter mon paquet.
    * @see {@link Pacelable}
    */
   public static final Parcelable.Creator<GameContext> CREATOR
     = new Parcelable.Creator<GameContext>() {
     public GameContext createFromParcel(Parcel in) { return new GameContext(in); }
     public GameContext[] newArray(int size) { return new GameContext[size]; }
   };
   
   /**
    * Méthode permettant au parcel de savoir s'il y a des trucs spécial la dedans.
    * Il n'y en as pas pour l'instant.
    * @see {@link Pacelable}
    */
   public int describeContents() { return 0; }

   /**
    * Méthode static permettant à java de paquetter et dépaquetter mon paquet.
    * @see {@link Pacelable}
    */
   public void writeToParcel(Parcel out, int flags) {
     
     // Les stats
     out.writeInt(knownOpponent_.size());
     for (OpponentInfo info : knownOpponent_)
       info.Write(out);
     
     // L'opposant
     if (mOpponent != null) {
       out.writeByte((byte)0x1);
       mOpponent.Write(out);
     } else out.writeByte((byte)0x0);
     
     out.writeByte(modeSolo);
   }

   /**
    * Méthode static permettant à java de paquetter et dépaquetter mon paquet.
    * @see {@link Pacelable}
    */
   private GameContext(Parcel in) {
     
      // Les stats
      knownOpponent_ = new ArrayList<OpponentInfo>();
      final int arraySize = in.readInt();
      for (int i = 0; i < arraySize; i++)
        knownOpponent_.add(new OpponentInfo(in));
     
     // Opponent
     byte opponent = in.readByte();
     if (opponent == 1)  mOpponent = new Opponent(in);
     else mOpponent = null;
     
     modeSolo = in.readByte();
   }
  
  public GameContext() {
    mOpponent = null;
    knownOpponent_ = new ArrayList<OpponentInfo>();
    modeSolo = 0;
  }
  
  public void SetOpponent(Opponent opponent) {
    mOpponent = opponent;
  }
  
  public Opponent GetOpponent() { return mOpponent; }
  public ArrayList<OpponentInfo> GetKnownOpponents() { return knownOpponent_; }
  
  public boolean IsSingle() { return modeSolo == 0; }
  
  public void SetMultiplayer() { modeSolo = 1; }
  
  public void EndGame(boolean hasWon, long time) {
    if (knownOpponent_.contains(mOpponent.getID())) {
      OpponentInfo modOpp = null;
      for (OpponentInfo info : knownOpponent_) {
        if (info.Id == mOpponent.getID())  modOpp = info;
      }
      
      modOpp.Defeated = (hasWon && modOpp.Defeated == 0) ? 1 : modOpp.Defeated;
      
      PriorityQueue<Integer> highScores = new PriorityQueue<Integer>();
      for (int curTime : modOpp.Highscores)  highScores.add(curTime);
      highScores.add((int)time);
      
      int[] highScoresArray = new int[5];
      int max = (highScores.size() > 5) ? 5 : highScores.size();
      for (int i = 0; i < max; i++) highScoresArray[i] = highScores.poll();
      for (int i = max - 1; i < 5; i++)  highScoresArray[i] = 9999999;
      
    } else {
      int[] highscore = new int[5];
      highscore[0] = (int)time;
      for (int i = 1; i < 5; i++) highscore[i] = 9999999;
      knownOpponent_.add(new OpponentInfo((hasWon) ? (byte)1 : (byte)0, mOpponent.getID(), highscore));
    }
  }
  
  private String Implode(ArrayList<String> strs) {
    String result = "";
    for (String str : strs) result.concat(str + " ");
    return result.trim();
  }
  
  public void SaveInfos(Activity ac) {
    SharedPreferences settings = ac.getSharedPreferences(kPrefFile, 0);
    SharedPreferences.Editor editor = settings.edit();
    
    ArrayList<String> ids = new ArrayList<String>();
    for (OpponentInfo info : knownOpponent_)  ids.add(Integer.toString(info.Id));
    editor.putString("idList", Implode(ids));
    
    for (OpponentInfo info : knownOpponent_) {
      info.Save(editor);
    }
    
    editor.commit();
  }
  
  public static ArrayList<OpponentInfo> FetchKnownOpponents(Activity act) {
    SharedPreferences settings = act.getSharedPreferences(kPrefFile, 0);
    if (!settings.contains("idList"))  return new ArrayList<OpponentInfo>();
    
    ArrayList<OpponentInfo> known = new ArrayList<OpponentInfo>();
    String idList = settings.getString("idList", "");
    
    String[] ids = idList.split(" ");
    
    for (String id : ids) {
      String buffer = settings.getString(id, "");
      String[] tokens = buffer.split(" ");
      String[] highscores = tokens[1].split(" ");
      int[] highscoreArray = new int[highscores.length];
      for(int i = 0; i < highscores.length; i++)
        highscoreArray[i] = Integer.parseInt(settings.getString(highscores[i], ""));
      known.add(new OpponentInfo(Byte.parseByte(tokens[0]), Integer.parseInt(id), highscoreArray));
    }
    
    return known;
  }
  
  public void SetKnownOpponents(ArrayList<OpponentInfo> opponents) {
    knownOpponent_ = opponents;
  }
}
