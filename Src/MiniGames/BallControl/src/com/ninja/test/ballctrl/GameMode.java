package com.ninja.test.ballctrl;

import android.graphics.RectF;
import android.os.Bundle;

public abstract class GameMode {
	private int mode;
	protected boolean finished;
	
	GameMode(int mode) {
		this.mode = mode;
		finished = false;
	}
	
	// Concrete functions
	int getMode() { return mode; }
	boolean isGameFinished() {return finished;}
	
	// Hook functions
	void DoCollidedWall() {}
	void DoCollideBounds() {}
	void resetGame() {}
	
	// Abstract functions
	abstract void foundShuriken(NinjaBall b, Coin c, RectF mCanvasDim);
	abstract Bundle DoEndingMessage(int shuriken);


}
