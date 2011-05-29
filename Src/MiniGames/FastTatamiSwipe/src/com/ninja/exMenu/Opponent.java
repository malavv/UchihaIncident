package com.ninja.exMenu;

import android.os.Parcel;

public class Opponent {
  private String mSenseiName;
  private int mNDots;
  
  public int getNDots() { return mNDots; }
  public int getLengthMs() { return mLengthMs; }
  public int getNTimes() { return mNTimes; }
  public String getSenseiName() { return mSenseiName; }

  public void setNDots(int NDots) { mNDots = NDots; }
  public void setLengthMs(int LengthMs) { mLengthMs = LengthMs; }
  public void setNTimes(int NTimes) { mNTimes = NTimes; }
  public void setSenseiName(String SenseiName) { mSenseiName = SenseiName; }

  private int mLengthMs;
  private int mNTimes;
  
  public Opponent(String senseiName, int nDots, int lengthMs, int nTimes) {
    mNTimes = nTimes;
    mSenseiName = senseiName;
    mNDots = nDots;
    mLengthMs = lengthMs;
  }
  
  public Opponent(Parcel in) {
    mSenseiName = in.readString();
    mNDots = in.readInt();
    mLengthMs = in.readInt();
    mNTimes = in.readInt();
  }
  
  public void Write(Parcel out) {
    out.writeString(mSenseiName);
    out.writeInt(mNDots);
    out.writeInt(mLengthMs);
    out.writeInt(mNTimes);
  }
}