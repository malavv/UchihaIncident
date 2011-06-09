package com.ninja.exMenu;

import android.os.Parcel;

public class Opponent {
  private int mId;
  private String mSenseiName;
  private int mNDots;
  private int mLengthMs;
  private int mNTimes;
  
  public int getNDots() { return mNDots; }
  public int getLengthMs() { return mLengthMs; }
  public int getNTimes() { return mNTimes; }
  public String getSenseiName() { return mSenseiName; }
  public int getID() { return mId; }
  
  public void setNDots(int NDots) { mNDots = NDots; }
  public void setLengthMs(int LengthMs) { mLengthMs = LengthMs; }
  public void setNTimes(int NTimes) { mNTimes = NTimes; }
  public void setSenseiName(String SenseiName) { mSenseiName = SenseiName; }
  public void setID(int id) { mId = id; }
  
  public Opponent(int id, String senseiName, int nDots, int lengthMs, int nTimes) {
    mId = id;
    mNTimes = nTimes;
    mSenseiName = senseiName;
    mNDots = nDots;
    mLengthMs = lengthMs;
  }
  
  public Opponent(Parcel in) {
    mId = in.readInt();
    mSenseiName = in.readString();
    mNDots = in.readInt();
    mLengthMs = in.readInt();
    mNTimes = in.readInt();
  }
  
  public void Write(Parcel out) {
    out.writeInt(mId);
    out.writeString(mSenseiName);
    out.writeInt(mNDots);
    out.writeInt(mLengthMs);
    out.writeInt(mNTimes);
  }
}