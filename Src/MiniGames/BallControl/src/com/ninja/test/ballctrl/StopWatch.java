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
	  /** La brosse utilis� pour imprimer le diff. */
	  private Paint diffBrush;
	  private long mPause;
	  /** La couleur utilis� pour imprimer le diff. */
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
	  
	  /** D�marre le chronom�tre */
	  public void Start() {
		  mStart = System.currentTimeMillis();
	  }
	  
	  /**
	   * @description sauvegarde le temps pass� depuis le d�part
	   * @return le temps pass� depuis le d�part
	   */
	  public double Diff() {
		  return ((double)System.currentTimeMillis() - mStart)/1000;
	  }
	  
	  /** met le chonometre en pause */
	  public void Pause() {
		  mPause = System.currentTimeMillis() - mStart;
	  }
	  
	  /** reprend le chronometre o� il en �tait */
	  public void Resume() {
		  Start();
		  mStart -= mPause;
		  mPause = 0;
	  }
	  
	  /** retourne le temps pass� depuis le d�part dans une chaine de charact�res */
	  public String DiffStr() {
		  return Double.toString(Diff());
	  }
	  
	  /** retourne la diff�rence entre le temps allou� de base et temps pass�
	   *  depuis le d�part dans une chaine de charact�res */
	  public String DiffCountDownStr(double base) {
		  return Double.toString(Global.precision(base-Diff(), 3));
	  }
	  
	  /**
	   * Permet d'imprimer le Diff en haut � droite de l'�cran.
	   * @param c Le canvas sur lequel imprimer le fps.
	   * @param position en x 
	   * @param position en y 
	   */
	  public void Draw(Canvas c, float x, float y) {
	    String s = "Time : " + DiffStr();
	    c.drawText(s, x, y, diffBrush);
	  }

	  /**
	   * Permet d'imprimer le Diff en haut � droite de l'�cran.
	   * @param c Le canvas sur lequel imprimer le fps.
	   * @param position en x 
	   * @param position en y
	   * @param base de temps du compte � rebours 
	   */
	  public void DrawCountdown(Canvas c, float x, float y, double base) {
		    String s = "Time : " + DiffCountDownStr(base);
		    c.drawText(s, x, y, diffBrush);
	  }

}
