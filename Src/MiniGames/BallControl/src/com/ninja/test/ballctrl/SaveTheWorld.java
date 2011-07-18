package com.ninja.test.ballctrl;

import android.content.Context;


public class SaveTheWorld {
	private static ParticlesSystem savedState;
	
	public static void saveGame(ParticlesSystem world) {
		savedState = world;
	}
	
	public static ParticlesSystem loadGame(Context context) {
		ParticlesSystem valToRet;
		if(savedState != null) {
			
			valToRet = savedState;
			savedState = null;
		}
		valToRet = new ParticlesSystem(context);
		return valToRet;
	}

}
