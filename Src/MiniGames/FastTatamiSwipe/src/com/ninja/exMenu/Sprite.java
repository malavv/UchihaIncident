package com.ninja.exMenu;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

/**
 * Classe représentant un dessins bitmap à afficher à l'écran.
 */
public class Sprite {
  // Les données à afficher sur le canvas.
  private Drawable data;
  // L'échelle de l'image à afficher.
  private double scale = 1.0;
  // La position de l'image.
  private Point mCenter;
  
  private float degreeRotation = 0.0f;
  private PointF mRotationCenter = new PointF();
  
  /**
   * Construit un sprite à partir de données images.
   * @param d L'image sous-jacente.
   */
  public Sprite(Drawable d) {
	  data = d;
	  mCenter = new Point();
  }
  
  /**
   * L'échelle de l'image.
   * @param s L'échelle à tranférer l'image.
   */
  public void Scale(double s) { scale = s; }
  
  public void PlaceRotationCenter(PointF center) { mRotationCenter = center; }
  
  public void SetRotation(float nDegree) { degreeRotation = nDegree; }
  public void Rotate(float nDegree) { degreeRotation += nDegree; }
  
  /**
   * Place l'image à la nouvelle position.
   * @param p Position
   */
  public void SetPosition(PointF p) {
    mCenter.x = (int)p.x;
    mCenter.y = (int)p.y;
  }
  
  /**
   * Retourne l'échelle présente de l'image.
   * @return
   */
  public double getScale() { return scale; }
  
  /**
   * Méthode permettant d'afficher l'image à la position actuelle.
   * @param c Le canvas sur lequel dessiner.
   */
  public void Draw(Canvas c) {
    int sideBound = (int)(data.getIntrinsicWidth() * scale);
    int heightBound = (int)(data.getIntrinsicHeight() * scale);
      
    if (degreeRotation != 0.0f) {
      c.save();
      c.rotate(degreeRotation, mRotationCenter.x, mRotationCenter.y);
      data.setBounds(mCenter.x - sideBound, mCenter.y - heightBound,
          mCenter.x + sideBound, mCenter.y + heightBound);
  	  data.draw(c);
  	  c.restore();
    } else {
      data.setBounds(mCenter.x - sideBound, mCenter.y - heightBound,
          mCenter.x + sideBound, mCenter.y + heightBound);
      data.draw(c);
    }
  }
}

