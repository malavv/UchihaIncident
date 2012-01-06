package com.ninja.test.ballctrl;

/**
 * Global
 * @author leelio
 * Contiens les variables et constantes globales du programme
 */
public class Global {
	/** nombre de divisions qu'on considère avoir à la verticale */
	public static final int yDiv = 13;

	/** nombre de divisions qu'on considère avoir à l'horizontal */
	public static final int xDiv = yDiv*2;
	
	/** direction dans laquelle se déplace un objet */
	public static final int FRONT = 0;
	public static final int BACK = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	/** coté de l'écran */
	public static final int BOUND_NONE = -1;
	public static final int BOUND_TOP = 0;
	public static final int BOUND_BOTTOM = 1;
	public static final int BOUND_RIGHT = 2;
	public static final int BOUND_LEFT = 3;
	
	/** Options du menu */
	public static final int MODE_NORMAL = 0;
	public static final int MODE_TIMED = 1;
	public static final int MODE_SURVIVAL = 2;
	public static final int MODE_CREDITS = -1;
	
	/** Etats du message */
	public static final int MSG_NORMAL = 0;
	public static final int MSG_TIMED = 1;
	public static final int MSG_SURVIVAL = 2;
	
	/** Fin du jeu */
	public static final int END_NOT_YET = 0;
	public static final int END_WIN = 1;
	public static final int END_LOSE = 2;
	
	
	// Fonctions statiques
	
	/** retourne un double avec le nombre de décimales indiqué dans precision */
	public static double precision(double number, int precision) {
		double coef = Math.pow(10, precision);
		number *= coef;
		return Math.rint(number)/coef;
	}

}
