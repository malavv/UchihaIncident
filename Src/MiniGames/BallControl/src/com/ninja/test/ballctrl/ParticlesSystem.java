package com.ninja.test.ballctrl;

import java.util.ArrayList;

public class ParticlesSystem {
	
	private ArrayList<Collidable> mObstalcesList;
	
	public ParticlesSystem() {
		
		// Obstacles list later i'd want it to be loaded form an XML file
		mObstalcesList.add(new Wall(0, 0, 1));
		mObstalcesList.add(new Wall(1, 0, 1));
		mObstalcesList.add(new Wall(0, 1, 1));
		mObstalcesList.add(new Wall(1, 1, 1));
	}
}
