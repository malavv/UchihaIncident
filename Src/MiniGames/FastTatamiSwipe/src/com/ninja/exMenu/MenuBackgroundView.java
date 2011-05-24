package com.ninja.exMenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MenuBackgroundView extends LinearLayout {
  private Paint arcPaint;
  private long count;
  private Drawable mask;
  
  public MenuBackgroundView(Context context, AttributeSet attrs) {
    super(context, attrs);
    arcPaint = new Paint();
    arcPaint.setColor(0xfffff8c1);
    arcPaint.setAntiAlias(true);
    setWillNotDraw(false);
    count = 0;
    mask = context.getResources().getDrawable(R.drawable.menu_filter_5);
    mask.setBounds(0, 0, getWidth(), getHeight());
  }
  
  @Override 
  public void onDraw(Canvas canvas) {
    canvas.drawColor(Color.BLACK);
    
    if (count > 18)  count = -18;
    
    float w = getWidth();
    float h = getHeight();
    float side = (float)Math.sqrt(w*w/4 + h*h);
    RectF tmp = new RectF(-side + w/2, -side + h, side + w / 2, side + h);
    
    for (int i = 0; i < 22; i++) {
      if (i % 2 == 0)  canvas.drawArc(tmp, 180 + (count/2 + i * 9), 9, true, arcPaint); 
    }
    
    count++;
    mask.setBounds(0, 0, (int)w, (int)h);
    mask.draw(canvas);
    invalidate();
  }
}
