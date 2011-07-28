package com.ninja.test.ballctrl;

import android.graphics.RectF;
import android.os.Bundle;

public class ModeNormal extends GameMode {
	
	private int limit;
	private int shurikenFound;

	ModeNormal(int shurikenLimit) {
		super(Global.MODE_NORMAL);
		limit = shurikenLimit;
		shurikenFound = 0;
	}

	@Override
	void foundShuriken(NinjaBall b, Coin c, RectF canvas) {
		c.setActive(false);	
		if(++shurikenFound >= limit)
		{
			finished = true;
		}
	}

	@Override
	void resetGame() {
		finished = false;
		shurikenFound = 0;
	}

	@Override
	void DoCollidedWall() {
		
	}
	
	@Override
	Bundle DoEndingMessage(int shuriken) {
		double time = Global.precision(StopWatch.Instance().Diff()-0.2, 3);
		Bundle b = new Bundle();
		b.putInt("mode", Global.MSG_SHOW_MENU);
		b.putDouble("time", time);
		b.putInt("shuriken", shuriken);
		return b;
	}

	@Override
	void DoCollideBounds() {
		
	}

}
