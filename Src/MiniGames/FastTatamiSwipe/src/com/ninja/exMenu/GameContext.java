package com.ninja.exMenu;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.util.Log;

/**
 * Cette classe représente le contexte général de l'application. Elle est
 * maintenu durant tout le jeu.
 */
public class GameContext {
   private static final String kSaveFile = "Tameshigiri";
   private static final String kDefaultString = "-1";
   private static final String kIdKey = "idList";
   private static final String kIdSeparator = " ";
  
   private static ArrayList<Opponent> mOpponents_;
   private static boolean isSinglePlayer_;
   private static int mCurrent_;
   private static SharedPreferences mFile_;
   private static SharedPreferences.Editor mWriteFile_;
   private static Context mCtx;
   
   public static void Init(Context ctx) {
     mCtx = ctx;
     mOpponents_ = new ArrayList<Opponent>();
     isSinglePlayer_ = true;
     mCurrent_ = -1;
     
     mFile_ = ctx.getSharedPreferences(kSaveFile, 0);
     mWriteFile_ = mFile_.edit();
   }
   
   public static ArrayList<Opponent> GetOpponents() { return mOpponents_; }
   public static Opponent Get(int index) { return mOpponents_.get(index); }
   public static Opponent GetVisible(int index) {
     final int max = mOpponents_.size();
     int realIndex = 0;
     for (int i = 0; i < max; i++) {
       if (IsVisible(Get(i))) {
         if (realIndex == index)  return Get(realIndex);
         else realIndex++;
       }
     }
     return null;
   }
   public static boolean IsSinglePlayer() { return isSinglePlayer_; }
   public static Opponent GetCurrent() { return mOpponents_.get(mCurrent_); }
   public static boolean IsVisible(Opponent op) {
     boolean isVisible = true;
     for (Integer id : op.GetNeedToUnlock())  if (!isDefeated(id))  isVisible = false;
     return isVisible;
   }
   public static int GetNVisible() {
     int n = 0;
     for (Opponent op : mOpponents_) if (IsVisible(op)) n++;
     return n;
   }
   private static boolean isDefeated(int id) {
     for (Opponent op : mOpponents_)  if (op.getID() == id && op.getIsDefeated())  return true;
     return false;
   }
   
   public static void SetIsSingle(boolean isSingle) { isSinglePlayer_ = isSingle; }
   public static void SetCurrentPlayer(int index) { mCurrent_ = index; }

   public static void SetEndGameInfo(boolean hasWon, int time) {
     Opponent op = mOpponents_.get(mCurrent_);
     if (hasWon)  op.setDefeated(true);
     op.addNewTime(time);
   }
  
   public static void Save() {
     mOpponents_.get(mCurrent_).Save(mWriteFile_);
     mWriteFile_.commit();
   }
   
   public static void Load() {
     LoadFromXml();
     LoadUserData();
   }
   
   private static void LoadFromXml() {
     XmlResourceParser parser = mCtx.getResources().getXml(R.xml.opponents);
     try {
       parser.next();parser.next();parser.next();
     } catch (XmlPullParserException e) {
       Log.wtf("Load from xml", "parser.next trew an exception.");
       e.printStackTrace();
     } catch (IOException e) {
       Log.wtf("Load from xml", "parser.next trew an exception.");
     }
     
     while (LoadOneOpponent(parser));   
   }
   
   private static boolean LoadOneOpponent(XmlResourceParser xml) {
      if (!xml.getName().equals("Opponent"))  return false;
      mOpponents_.add(Opponent.Create(xml));
      return true;
   }
   
   private static String[] getSavedID() {
     String ids = mFile_.getString(kIdKey, kDefaultString);
     if (ids.contentEquals(kDefaultString))  return new String[0];
     
     String[] tokens = ids.split(kIdSeparator);
     if (tokens[0].length() == 0)  return new String[0];
     else return tokens;
   }
   
   private static void LoadUserData() {
     String[] ids = getSavedID();
     
     if (ids.length == 0) {
       String[] names = new String[mOpponents_.size()];
       for (int i = 0; i < mOpponents_.size(); i++)
         names[i] = Integer.toString(mOpponents_.get(i).getID());
       mWriteFile_.putString(kIdKey, Implode(names));
       
       for (Opponent op : mOpponents_)  op.Save(mWriteFile_);
       mWriteFile_.commit();
     } else {
       for (String id : ids) {
         if (id.length() == 0)  continue;
         Get(Integer.parseInt(id)).LoadPref(mFile_);
       }
     }
   }
   
   public static String Implode(String[] sstr) {
     String ret = sstr[0];
     for (int i = 1; i < sstr.length; i++)
       ret += " " + sstr[i];
     return ret;
   }
}
