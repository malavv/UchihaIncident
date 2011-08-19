package com.ninja.test.ballctrl;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;

public class ModeTimed extends GameMode {
	
	public static double limit;
	private int collisions;
	
	public ModeTimed() {
		super(Global.MODE_TIMED);
		collisions = 0;
	}

	@Override
	public void foundShuriken(NinjaBall b, Coin c, RectF canvas) {
		c.replace((int)canvas.right, (int)canvas.bottom);
	}

	@Override
	public void resetGame() {
		collisions = 0;
	}

	@Override
	public void DoCollidedWall() {
		collisions++;
	}

	@Override
	public void DoCollideBounds() {
		collisions++;
	}
	
	@Override
	public Bundle DoEndingMessage(int shuriken) {
		double time = Global.precision(StopWatch.Instance().Diff()-0.2, 3);
		Bundle b = new Bundle();
		b.putInt("mode", Global.MSG_TIMED);
		b.putDouble("time", time);
		b.putInt("collisions", collisions);
		b.putInt("shuriken", shuriken);
		return b;
	}
	
	@Override
	public boolean isGameFinished() {
		// ici la précision des décimale n'est pas nécessaire, c'est seulement pour l'affichage
		return StopWatch.Instance().Diff()-0.2 >= limit;
	}
	
	@Override
	public void DrawTime(Canvas c, float x, float y) {
		StopWatch.Instance().DrawCountdown(c, x, y, limit);
	}

}
