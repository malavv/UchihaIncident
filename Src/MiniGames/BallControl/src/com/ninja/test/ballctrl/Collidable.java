package com.ninja.test.ballctrl;

public class Collidable {
    private int mPosX;
    private int mPosY;
    private float mElasticity;
    
    private static int rayon;
    
    Collidable(int aPosX, int aPosY, float aElasticity) {
    	mPosX = aPosX;
    	mPosY = aPosY;
    	mElasticity = aElasticity;
    	
    	setSides();
    }
    
    public static void setOffset(int offset) {
    	rayon = offset/2;
    }
    
    public static boolean collided(Collidable a, Collidable b) {
    	int collideDistance = a.getRayon() + b.getRayon();
    	int collideDistance2 = collideDistance*collideDistance - 30;
    	
    	int distX = a.getX() - b.getX();
    	int distY = a.getY() - b.getY();
    	
    	int dist2 = (int) distX*distX + distY*distY;
    	
    	if(dist2 <= collideDistance2)
    		return true;
    	else 
    		return false;
    	
    	// distance entre le centre du mur et la droite de déplacement de l'objet dois être
    	// inférieur à a.rayon + b.rayon
    	//int collideDistance = a.getRayon() + b.getRayon();
    	
    	
    	//return true;
    }
    
    /*
     * à l'initialisation, les coordonnées sont celles du coin suppérieur gauche
     * de l'objet, on va modifier ces coordonnées pour qu'elle correspondent
     * au centre de l'objet
     */
    public void setSides() {
    	mPosX += rayon;
    	mPosY += rayon;
    }

	public int getX() {
		return mPosX;
	}

	public void setX(int mPosX) {
		this.mPosX = mPosX;
	}

	public int getY() {
		return mPosY;
	}

	public void setY(int mPosY) {
		this.mPosY = mPosY;
	}

	public float getElasticity() {
		return mElasticity;
	}

	public int getRayon() {
		return rayon;
	}

}
