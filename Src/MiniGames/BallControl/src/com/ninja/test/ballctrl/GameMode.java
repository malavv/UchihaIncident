package com.ninja.test.ballctrl;

import java.util.Iterator;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;

public abstract class GameMode {
	private int mode;
	protected boolean finished;
	
	GameMode(int mode) {
		this.mode = mode;
		finished = false;
		MapsManager.GetMapFromFile("");
	}
	
	// Concrete functions
	public int getMode() { return mode; }
	public boolean isGameFinished() {return finished;}
	public void DrawTime(Canvas c, float x, float y){StopWatch.Instance().Draw(c, x, y);}
	
	// Hook functions
	public void DoCollidedWall() {}
	public void DoCollideBounds() {}
	public void resetGame() {}
	
	// Abstract functions
	public abstract void foundShuriken(NinjaBall b, Coin c, RectF mCanvasDim, 
			Iterator<Collidable> obstacles);
	public abstract Bundle DoEndingMessage(int shuriken);


}
