package com.ninja.test.ballctrl;

/**
 * Global
 * @author leelio
 * Contiens les variables globales du programme
 */
public class Global {
	/**
	 * nombre de divisions qu'on considère avoir à la verticale
	 */
	public static final int yDiv = 13;

	/**
	 * nombre de divisions qu'on considère avoir à l'horizontal
	 */
	public static final int xDiv = yDiv*2;
	
	/** direction dans laquelle se déplace un objet */
	public static final int FRONT = 0;
	public static final int BACK = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	/** Options du menu */
	public static final int MODE_NORMAL = 0;
	public static final int MODE_TIMED = 1;
	public static final int MODE_SURVIVAL = 2;
	public static final int MODE_CREDITS = -1;

}
