package com.ninja.exMenu;

import android.content.SharedPreferences;
import android.os.Parcel;

public class OpponentInfo {
  OpponentInfo(byte defeated, int id, int[] highscore) {
     Defeated = defeated;
     Id = id;
     Highscores = highscore;
  }
  OpponentInfo(Parcel in) {
    Defeated = in.readByte();
    Id = in.readInt();
    Highscores = new int[5];
    in.readIntArray(Highscores);
  }
  public byte Defeated;
  public int Id;
  public int[] Highscores;
  void Write(Parcel out) {
    out.writeByte(Defeated);
    out.writeInt(Id);
    out.writeIntArray(Highscores);
  }
  void Save(SharedPreferences.Editor ed) {
    String id = Integer.toString(Id);
    String desc = (Defeated == 0) ? "0" : "1";
    desc.concat(" " + id + "high1 " + id + "high2 " + id + "high3 " + id + "high4 " + id + "high5");
    ed.putString("id", desc);
    
    for (int i = 0; i < 5; i++)
      ed.putInt(id + "high" + Integer.toString(i), Highscores[i]);
  }
}