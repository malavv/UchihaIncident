package com.ninja.test.ballctrl;

import android.graphics.RectF;
import android.os.Bundle;

public class ModeSurvival extends GameMode {
	
	public ModeSurvival() {
		super(Global.MODE_SURVIVAL);
	}

	@Override
	void foundShuriken(NinjaBall b, Coin c, RectF canvas) {
		b.increaseSpeed(.5f);
		c.replace((int)canvas.right, (int)canvas.bottom);
	}

	@Override
	void resetGame() {
		finished = false;
	}

	@Override
	void DoCollidedWall() {
		finished = true;
	}
	
	@Override
	Bundle DoEndingMessage(int shuriken) {
		double time = Global.precision(StopWatch.Instance().Diff()-0.2, 3);
		Bundle b = new Bundle();
		b.putInt("mode", Global.MSG_SURVIVAL);
		b.putDouble("time", time);
		b.putInt("shuriken", shuriken);
		return b;
	}

	@Override
	void DoCollideBounds() {
		
	}
}
