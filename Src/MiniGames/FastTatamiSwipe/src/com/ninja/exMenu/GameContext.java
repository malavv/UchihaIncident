package com.ninja.exMenu;

import android.os.Parcel;
import android.os.Parcelable;


public class GameContext implements Parcelable {
  
   private Opponent mOpponent;
  
   private int[] defeatedOpponents;
   
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
     
     // L'opposant
     if (mOpponent != null) {
       out.writeByte((byte)0x1);
       mOpponent.Write(out);
     } else out.writeByte((byte)0x0);
     
     out.writeInt(defeatedOpponents.length);
     out.writeIntArray(defeatedOpponents);
     
     out.writeByte(modeSolo);
   }


   /**
    * Méthode static permettant à java de paquetter et dépaquetter mon paquet.
    * @see {@link Pacelable}
    */
   private GameContext(Parcel in) {
     byte opponent = in.readByte();
     if (opponent == 1)  mOpponent = new Opponent(in);
     else mOpponent = null;
     
     int size = in.readInt();
     defeatedOpponents = new int[size];
     in.readIntArray(defeatedOpponents);
     
     modeSolo = in.readByte();
   }
  
  public GameContext() {
    mOpponent = null;
    defeatedOpponents = new int[0];
    modeSolo = 0;
  }
  
  public void SetOpponent(Opponent opponent) {
    mOpponent = opponent;
  }
  
  public Opponent GetOpponent() { return mOpponent; }
  
  public boolean IsSingle() { return modeSolo == 0; }
  
  public void SetMultiplayer() { modeSolo = 1; }
}
