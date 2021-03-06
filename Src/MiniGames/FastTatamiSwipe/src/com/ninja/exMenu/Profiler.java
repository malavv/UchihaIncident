package com.ninja.exMenu;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Une classe permettant principalement de g�rer le fps de l'application et ses
 * implication.
 */
public class Profiler {
  /** Le temps du dernier tick. */
  private long lastTick;
  /** La diff�rence de temps avec le nouveau tick. */
  private long tickDiff;
  /** La brosse utilis� pour imprimer le fps. */
  private Paint fpsBrush;
  
  private long capMs;
  /** La couleur utilis� pour imprimer le fps. */
  private final static int fpsColor = Color.WHITE;
  
  public Profiler() {
    fpsBrush = new Paint();
    fpsBrush.setColor(fpsColor);
    capMs = -1;
  }
  
  /**
   * Permet d'imprimer le FPS count en bas � gauche de l'�cran.
   * @param c Le canvas sur lequel imprimer le fps.
   * @param bounds Les dimensions du canvas.
   */
  public void Draw(Canvas c, RectF bounds) {
    String s = "FPS : " + FpsStr();
    c.drawText(s, bounds.left + 5, bounds.bottom - 5, fpsBrush);
  }
  
  /** Retourne le fps actuel. */
  public long Fps() { return 1000 / tickDiff; }
  /** Retourne le fps actuel comme une string. */
  public String FpsStr() { return Long.toString(Fps()); }
  /** Retourne le temps en milisecondes avec le dernier draw. */
  public long Delta() { return tickDiff; }
  
  public void SetCapAt(long fps) {
    capMs = 1000 / fps;
  }
  
  /** Doit �tre appeler � chaque draw pour calculer les informations. */
  public void Tick() {
    long time = System.currentTimeMillis();
    tickDiff = time - lastTick;
    lastTick = time;
    if (capMs != -1) {
      if (capMs > tickDiff) {
        try {  Thread.sleep(capMs - tickDiff); } catch (Exception e) {}
      }
    }
  }
}
