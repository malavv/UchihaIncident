package com.ninja.exMenu;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;


public class GameContext implements Parcelable {
   
   private static final String kPrefFile = "preferences";
   private static final String kDefault= "-1";
   
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
    OpponentInfo opponent = null;
    for (OpponentInfo info : knownOpponent_) {
      if (info.Id == mOpponent.getID())  opponent = info;
    }
    
    if (opponent == null) {
      CreateNewKnownOpponent(hasWon, time);
      return;
    }
    
    boolean needToChange = (opponent.Defeated == 0 && hasWon) || (time < opponent.Highscores[4]);
    if (!needToChange)  return;
    
    opponent.Defeated = (hasWon && opponent.Defeated == 0) ? 1 : opponent.Defeated;
    
    ArrayList<Integer> highScores = new ArrayList<Integer>();
    for (int i : opponent.Highscores)  highScores.add(i);
    for (int i = 0; i < highScores.size(); i++)
      if (time < highScores.get(i)) {
        highScores.add(i, (int)time); break;
      }

    for (int i = 0; i < 5; i++)
      opponent.Highscores[i] = highScores.get(i);
  }
  
  private void CreateNewKnownOpponent(boolean hasWon, long time) {
    OpponentInfo basic = new OpponentInfo();
    basic.Defeated = (hasWon) ? (byte)1 : (byte)0;
    basic.Id = mOpponent.getID();
    basic.Highscores[0] = (int)time;
    knownOpponent_.add(basic);
  }
  
  private String Implode(ArrayList<String> strs) {
    String result = new String();
    for (int i = 0; i < strs.size(); i++)
      result += (strs.get(i) + " ");
    return result.trim();
  }
  
  public void SaveInfos(Activity ac) {
    SharedPreferences settings = ac.getSharedPreferences(kPrefFile, 0);
    SharedPreferences.Editor editor = settings.edit();
    
    ArrayList<String> ids = new ArrayList<String>();
    for (OpponentInfo info : knownOpponent_)  ids.add(Integer.toString(info.Id));
    String fina = Implode(ids);
    editor.putString("idList", fina);
    
    for (OpponentInfo info : knownOpponent_)
      info.Save(editor);
    
    editor.commit();
  }
  
  public static ArrayList<OpponentInfo> FetchKnownOpponents(Activity act) {
    SharedPreferences settings = act.getSharedPreferences(kPrefFile, 0);
   
    String idList = settings.getString("idList", kDefault);
    if (idList.equals(kDefault))  return new ArrayList<OpponentInfo>();
    
    ArrayList<OpponentInfo> known = new ArrayList<OpponentInfo>();
    String[] ids = idList.split(" ");
    
    for (String id : ids) {
      if (id.length() == 0)  continue;
      known.add(FetchSingleOpponent(settings, id));
    }
    
    return known;
  }
  
  private static OpponentInfo FetchSingleOpponent(SharedPreferences set, String id) {
    OpponentInfo opp = new OpponentInfo();
    
    String buffer = set.getString("opp" + id, kDefault);
    if (buffer.equals(kDefault))  return opp;

    String[] tokens = buffer.split(":");
    String[] highscores = tokens[1].split("-");
    
    int[] highscoreArray = new int[5];
    for(int i = 0; i < highscores.length; i++)
      highscoreArray[i] = Integer.parseInt(highscores[i]);
    
    opp.Id = Integer.parseInt(id);
    opp.Defeated = Byte.parseByte(tokens[0]);
    opp.Highscores = highscoreArray;
    return opp;
  }
  
  public void SetKnownOpponents(ArrayList<OpponentInfo> opponents) {
    knownOpponent_ = opponents;
  }
}
