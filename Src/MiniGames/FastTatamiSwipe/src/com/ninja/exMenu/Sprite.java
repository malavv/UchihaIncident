package com.ninja.exMenu;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class Sprite {
  private Drawable data;
  private int halfHeight;
  private int halfWidth;
  
  private double scale = 1.0;
  
  public Sprite(Drawable d) {
	  data = d;
	  halfHeight = d.getIntrinsicHeight() / 2;
	  halfWidth = d.getIntrinsicWidth() / 2;
  }
  
  public void Scale(double s) { scale = s; }
  public double getScale() { return scale; }
  
  public void CenterOn(Point p) {
	  data.setBounds(p.x - (int)(halfWidth * scale), p.y - (int)(halfHeight * scale), p.x + (int)(halfWidth * scale), p.y + (int)(halfHeight * scale));
  }
  
  public void Draw(Canvas c) {
	  data.draw(c);
  }
}
