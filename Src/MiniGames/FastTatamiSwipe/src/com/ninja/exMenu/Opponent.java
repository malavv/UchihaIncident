package com.ninja.exMenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.util.Log;

public class Opponent {
  private static final String kOpponentFileKey = "opp";
  private static final String kDefaultString = "-1";
  private static final int kNHighScore = 5;
  private static final int kMaxTime = 99999;
  
  private int mId;
  private String mSenseiName;
  private int mNDots;
  private int mLengthMs;
  private int mNTimes;
  private ArrayList<Integer> mNeedToUnlock;
  private boolean mDefeated;
  private PriorityQueue<Integer> mHighScores;
  
  private Opponent() {
    mNeedToUnlock = new ArrayList<Integer>();
    mDefeated = false;
    mHighScores = new PriorityQueue<Integer>();
    for (int i = 0; i < kNHighScore; i++)  mHighScores.add(kMaxTime);
  }
  
  public static Opponent Create(XmlResourceParser xml) {
    Opponent op = new Opponent();
    
    op.mId = Integer.parseInt(xml.getAttributeValue(0));
    op.mSenseiName = xml.getAttributeValue(1);
    op.mNDots = Integer.parseInt(xml.getAttributeValue(2));
    op.mLengthMs = Integer.parseInt(xml.getAttributeValue(3));
    op.mNTimes = Integer.parseInt(xml.getAttributeValue(4));

    try {
      xml.next(); // Goto unlocked
      int id = LoadXmlId(xml);
      while (id != -2) {
        if (id != -1)
          op.mNeedToUnlock.add(id);
        id = LoadXmlId(xml);
      }
      xml.next(); // End Unlocked
      xml.next(); // End Opponent
    } catch (IOException e) {
      Log.wtf("Xml Conversion", "Xml parsing failed.");
    } catch (XmlPullParserException e) {
      Log.wtf("Xml Conversion", "Xml parsing failed.");
    }
    
    return op;
  }
  
  private static int LoadXmlId(XmlResourceParser xml) throws XmlPullParserException, IOException {
    xml.next();
    if (!xml.getName().contentEquals("ID")) return -2;
    xml.next();
    int res = Integer.parseInt(xml.getText());
    xml.next();
    return res;
  }
  
  public void LoadPref(SharedPreferences file) {
    
    String buffer = file.getString(kOpponentFileKey + getID(), kDefaultString);
    if (buffer.equals(kDefaultString))  return;

    String[] tokens = buffer.split(":");
    String[] highscores = tokens[1].split("-");
    
    mDefeated = (Integer.parseInt(tokens[0]) == 1) ? true: false;
    for (String highscore : highscores)
      mHighScores.offer(Integer.parseInt(highscore));
  }
  
  public void Save(SharedPreferences.Editor file) {
    String id = Integer.toString(mId);
    String desc = (mDefeated) ? "1:" : "0:";
    PriorityQueue<Integer> highscores = new PriorityQueue<Integer>(mHighScores);
    
    desc += highscores.poll();
    while (highscores.size() > 0)  desc += "-" + highscores.poll();
    file.putString(kOpponentFileKey + id, desc);
  }
  
  public int getID() { return mId; }
  public String getSenseiName() { return mSenseiName; }
  public int getNDots() { return mNDots; }
  public int getLengthMs() { return mLengthMs; }
  public int getNTimes() { return mNTimes; }
  public boolean getIsDefeated() { return mDefeated; }
  public Integer[] getHighscores() { return (Integer[])mHighScores.toArray(); }
  public ArrayList<Integer> GetNeedToUnlock() { return mNeedToUnlock; }

  public void setNDots(int NDots) { mNDots = NDots; }
  public void setLengthMs(int LengthMs) { mLengthMs = LengthMs; }
  public void setNTimes(int NTimes) { mNTimes = NTimes; }
  public void setSenseiName(String SenseiName) { mSenseiName = SenseiName; }
  public void setID(int id) { mId = id; }
  public void setDefeated(boolean isDefeated) { mDefeated = isDefeated; }
  public void addNewTime(int time) {
    mHighScores.offer(time);
    Iterator<Integer> itr = mHighScores.iterator();
    int i = 0;
    while(itr.hasNext()) {
      if (i > kNHighScore)  itr.remove();
      itr.next();
    }
  }
}