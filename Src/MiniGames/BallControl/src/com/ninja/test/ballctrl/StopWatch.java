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
	  
	  private static StopWatch stopWatch;
	  
	  private StopWatch() {
		  mStart = 0;
		  mPause = 0;
		  diffBrush = new Paint();
		  diffBrush.setColor(diffColor);
	  }
	  
	  /** Donne une instance du chronometre */
	  public static StopWatch Instance() {
		  if(stopWatch == null)
			  stopWatch = new StopWatch();
		  return stopWatch;
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
	  
	  /** retourne la différence entre le temps alloué de base et temps passé
	   *  depuis le départ dans une chaine de charactères */
	  public String DiffCountDownStr(double base) {
		  return Double.toString(Global.precision(base-Diff(), 3));
	  }
	  
	  /**
	   * Permet d'imprimer le Diff en haut à droite de l'écran.
	   * @param c Le canvas sur lequel imprimer le fps.
	   * @param position en x 
	   * @param position en y 
	   */
	  public void Draw(Canvas c, float x, float y) {
	    String s = "Time : " + DiffStr();
	    c.drawText(s, x, y, diffBrush);
	  }

	  /**
	   * Permet d'imprimer le Diff en haut à droite de l'écran.
	   * @param c Le canvas sur lequel imprimer le fps.
	   * @param position en x 
	   * @param position en y
	   * @param base de temps du compte à rebours 
	   */
	  public void DrawCountdown(Canvas c, float x, float y, double base) {
		    String s = "Time : " + DiffCountDownStr(base);
		    c.drawText(s, x, y, diffBrush);
	  }

}
