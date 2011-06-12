package com.ninja.test.ballctrl;

import java.util.ArrayList;

public class Map {
	ArrayList<Collidable> mObstaclesList;
	
	ArrayList<Movable> mEnnemiesList;
	
	ArrayList<Collidable> mItemsList;

	/**
	 * @return the mObstaclesList
	 */
	public ArrayList<Collidable> getObstaclesList() {
		return mObstaclesList;
	}

	/**
	 * @return the mEnnemiesList
	 */
	public ArrayList<Movable> getEnnemiesList() {
		return mEnnemiesList;
	}

	/**
	 * @return the mItemsList
	 */
	public ArrayList<Collidable> getItemsList() {
		return mItemsList;
	}

	/**
	 * @param ObstaclesList the mObstaclesList to set
	 */
	public void setObstaclesList(ArrayList<Collidable> ObstaclesList) {
		this.mObstaclesList = ObstaclesList;
	}

	/**
	 * @param EnnemiesList the mEnnemiesList to set
	 */
	public void setEnnemiesList(ArrayList<Movable> EnnemiesList) {
		this.mEnnemiesList = EnnemiesList;
	}

	/**
	 * @param mItemsList the ItemsList to set
	 */
	public void setItemsList(ArrayList<Collidable> ItemsList) {
		this.mItemsList = ItemsList;
	}
}
