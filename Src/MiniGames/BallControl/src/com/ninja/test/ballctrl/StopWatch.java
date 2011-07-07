package com.ninja.test.ballctrl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @description Chronometre avec les fonctions principales : start et diff
 */
public class StopWatch {
	  /** Le temps du depart. */
	  private long mStart;
	  /** La brosse utilisé pour imprimer le diff. */
	  private Paint diffBrush;
	private long mPause;
	  /** La couleur utilisé pour imprimer le diff. */
	  private final static int diffColor = Color.WHITE;
	  
	  public StopWatch() {
		  diffBrush = new Paint();
		  diffBrush.setColor(diffColor);
	  }
	  
	  /** Démarre le chronomètre */
	  public void Start() {
		  mStart = System.currentTimeMillis();
	  }
	  
	  /**
	   * @description sauvegarde le temps passé depuis le départ
	   * @return le temps passé depuis le départ
	   */
	  public double Diff() {
		  return ((double)System.currentTimeMillis() - mStart)/1000;
	  }
	  
	  /** met le chonometre en pause */
	  public void Pause() {
		  mPause = System.currentTimeMillis() - mStart;
	  }
	  
	  /** reprend le chronometre où il en était */
	  public void Resume() {
		  Start();
		  mStart -= mPause;
		  mPause = 0;
	  }
	  
	  /** retourne le temps passé depuis le départ dans une chaine de charactères */
	  public String DiffStr() {
		  return Double.toString(Diff());
	  }
	  
	  /**
	   * Permet d'imprimer le Diff en bas à gauche de l'écran.
	   * @param c Le canvas sur lequel imprimer le fps.
	   * @param position en x 
	   * @param position en y 
	   */
	  public void Draw(Canvas c, float x, float y) {
	    String s = "Time : " + DiffStr();
	    c.drawText(s, x, y, diffBrush);
	  }

}
