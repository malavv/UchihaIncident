package com.ninja.test.ballctrl;

import android.graphics.RectF;
import android.os.Bundle;

public class ModeTimed extends GameMode {
	
	private double limit;
	private int collisions;
	
	public ModeTimed(double timeLimit) {
		super(Global.MODE_TIMED);
		limit = timeLimit;
		collisions = 0;
	}

	@Override
	void foundShuriken(NinjaBall b, Coin c, RectF canvas) {
		c.replace((int)canvas.right, (int)canvas.bottom);
	}

	@Override
	void resetGame() {
		collisions = 0;
	}

	@Override
	void DoCollidedWall() {
		collisions++;
	}
	
	@Override
	Bundle DoEndingMessage(int shuriken) {
		double time = 1;//Global.precision(StopWatch.Instance().Diff()-0.2, 3);
		Bundle b = new Bundle();
		b.putInt("mode", Global.MSG_SHOW_MENU);
		b.putDouble("time", time);
		b.putInt("collisions", collisions);
		return b;
	}

	@Override
	void DoCollideBounds() {
		collisions++;
	}
	
	@Override
	boolean isGameFinished() {
		// ici la précision des décimale n'est pas nécessaire, c'est seulement pour l'affichage
		return false;//StopWatch.Instance().Diff()-0.2 >= limit;
	}

}
