package com.ninja.exMenu;

import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

/**
 * Cette classe est utilisé dans le but de dessiner une ligne qui suit le doigt
 * de l'utilisateur.
 */
public class FollowupLine {
  /** La largeur de la ligne */
  private final static float kLineWidth = 4.5f;
  /** La couleur de la ligne */
  private final static int kLineColor = Color.GRAY;
  /** Le nombre de points à emmagasiner. */
  private final static int kLineLength = 30;
  /** 
   * Un petit incrément qui s'assure que la ligne ne soit pas discontinu
   * entre les points.
   */
  private final static float kLineDelta = 0.4f;
  /** 
   * Un point ne sera ajouter à la ligne que s'il est à plus de cette distance
   * de l'ancien point.
   */
  private final static float kDistFromLastPoint = 5.0f;
  /** La brosse utilisé pour peindre la ligne. */
  private static Paint kLineBrush;
  
  /** La liste des points de la ligne. */
  private LinkedList<PointF> pastTouchPoints;
  
  /**
   * Créé l'utilitaire de ligne.
   */
  public FollowupLine() {
    pastTouchPoints = new LinkedList<PointF>();
    kLineBrush = new Paint();
    kLineBrush.setAntiAlias(true);
    kLineBrush.setColor(kLineColor);
    kLineBrush.setStrokeWidth(kLineWidth);
  }
  
  /**
   * Dois être appeler à chaque événement touch que nous voulons ajouter à la
   * ligne
   * @param e L'événement touch.
   */
  public void DoTouch(MotionEvent e) {
    if ((pastTouchPoints.size() < 1)) {
      pastTouchPoints.addFirst(new PointF(e.getX(), e.getY()));
      return;
    }
    
    PointF touchPoint = new PointF(e.getX(), e.getY());
    if (DistanceBetweenTwoPoints(pastTouchPoints.getFirst(), touchPoint) > kDistFromLastPoint)
      pastTouchPoints.addFirst(touchPoint);
    if (pastTouchPoints.size() > kLineLength)  pastTouchPoints.removeLast();
  }
  
  /**
   * Fonction retournant la distance entre les deux points.
   * NOTE(malavv) : Devrais être mis dans une classe utilitaire.
   * @param p1 Le premier point.
   * @param p2 Le deuxième point.
   * @return La distance entre les deux points.
   */
  public static float DistanceBetweenTwoPoints(PointF p1, PointF p2) {
    return (float)Math.sqrt(((p2.x - p1.x) * (p2.x - p1.x)) + ((p2.y - p1.y) * (p2.y - p1.y)));
  }
  
  /** Dessiner la ligne sur le canvas. */
  public void Draw(Canvas c) {
    for (int i = 0; i < pastTouchPoints.size() - 1; i++) {
      PointF p1 = pastTouchPoints.get(i);
      PointF p2 = pastTouchPoints.get(i + 1);
      
      float dx = ((p2.x - p1.x) > 0) ? kLineDelta : -kLineDelta;
      float dy = ((p2.y - p1.y) > 0) ? kLineDelta : -kLineDelta;
      
      c.drawLine(pastTouchPoints.get(i).x, pastTouchPoints.get(i).y,
                 pastTouchPoints.get(i + 1).x + dx, pastTouchPoints.get(i + 1).y + dy, kLineBrush);
    }
  }
}
