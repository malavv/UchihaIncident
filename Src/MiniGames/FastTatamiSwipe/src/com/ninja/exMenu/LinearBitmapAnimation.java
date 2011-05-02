package com.ninja.exMenu;

import android.graphics.Point;
import android.graphics.PointF;

/**
 * Classe permettant de gérer les transitions linéaire d'objets sur le canvas.
 */
public class LinearBitmapAnimation {
  // Temps en milisecondes avant le départ de l'application.
  private long mOffsetMs;
  // Durée en milisecondes de l'application.
  private long mLengthMs;
  // Le moment auquel nous sommes rendu dans l'application.
  private long mCountMs;
  // Point de départ de l'animation.
  private Point mStart;
  // Point de fin de l'animation.
  private Point mFinish;
  // Le sprite sous-jacent à afficher.
  private Sprite mSprite;
  // Détermine si l'animation est présentement actif.
  public boolean isActive;
  
  // Constructeur pour un sprite et une durée.
  public LinearBitmapAnimation(Sprite sprite, long length, Point start,
      Point finish) {
    mOffsetMs = 0;
    mLengthMs = length;
    mSprite = sprite;
    mStart = start;
    mFinish = finish;
    isActive = true;
    mCountMs = 0;
  }
  
  /**
   * Dessine un incrément de l'animation.
   * @param dTime Le delta temps depuis le dernier draw.
   */
  public void Animate(long dTime) {
    mCountMs += dTime;
    
    if (mCountMs <= mOffsetMs || !isActive)  return;
    if (mCountMs > mLengthMs + mOffsetMs) {
      isActive = false;
      return;
    }
    
    float pourcentage = mCountMs / (float)(mOffsetMs + mLengthMs);
    
    Point delta = new Point(mFinish.x - mStart.x, mFinish.y - mStart.y);
    mSprite.SetPosition(new PointF(mStart.x + delta.x * pourcentage,
        mStart.y + delta.y * pourcentage));
  }
  
  // Retourne le sprite sous-jacent.
  public Sprite Sprite() { return mSprite; }
}
