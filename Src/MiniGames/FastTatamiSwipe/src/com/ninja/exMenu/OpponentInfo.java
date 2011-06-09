package com.ninja.exMenu;

import android.content.SharedPreferences;
import android.os.Parcel;

public class OpponentInfo {
  OpponentInfo() {
     Defeated = 0;
     Id = -1;
     Highscores = new int[5];
     Highscores[0] = Highscores[1] = Highscores[2] = Highscores[3] = Highscores[4] = 999999;
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
    String desc = (Defeated == 0) ? "0:" : "1:";
    
    for (int i = 0; i < 5; i++) {
      if (i == 4)  desc += Integer.toString(Highscores[i]);
      else desc += Integer.toString(Highscores[i]) + "-";
    }
    ed.putString("opp" + id, desc);
  }
}