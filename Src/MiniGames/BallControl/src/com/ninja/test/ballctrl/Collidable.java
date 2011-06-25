package com.ninja.test.ballctrl;

public class Collidable {
    private int mPosX;
    private int mPosY;
    private float mElasticity;
    private int collideDistance;
    private int collideDistance2;
    private int distX;
    private int distY;
    private int dist2;
    
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
    
    public static int getOffset() {
    	return rayon*2;
    }
    
    public boolean collided( Collidable c) {
    	collideDistance = this.getRayon() + c.getRayon();
    	collideDistance2 = collideDistance*collideDistance;
    	
    	distX = this.getX() - c.getX();
    	distY = this.getY() - c.getY();
    	
    	dist2 = (int) distX*distX + distY*distY;
    	
    	if(dist2 <= collideDistance2)
    		return true;
    	else 
    		return false;
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
