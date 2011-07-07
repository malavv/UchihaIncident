package com.ninja.test.ballctrl;

/**
 * Global
 * @author leelio
 * Contiens les variables globales du programme
 */
public class Global {
	/**
	 * nombre de divisions qu'on consid�re avoir � la verticale
	 */
	public static final int yDiv = 13;

	/**
	 * nombre de divisions qu'on consid�re avoir � l'horizontal
	 */
	public static final int xDiv = yDiv*2;
	
	/** direction dans laquelle se d�place un objet */
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
