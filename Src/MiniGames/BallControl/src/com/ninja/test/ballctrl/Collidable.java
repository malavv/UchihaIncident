package com.ninja.test.ballctrl;

public class Collidable {
    private float mPosX;
    private float mPosY;
    private float mElasticity;
    private int collideDistance;
    private int collideDistance2;
    private float distX;
    private float distY;
    private float dist2;
    
	protected boolean active;
    
    private static int rayon;
    
    Collidable(float aPosX, float aPosY, float aElasticity) {
    	mPosX = aPosX;
    	mPosY = aPosY;
    	mElasticity = aElasticity;
    	
    	setSides();
    }
    
    @Override
    public boolean equals(Object o) {
    	Collidable tmp = (Collidable)o;
    	if(tmp.mPosX == mPosX && tmp.mPosY == mPosY)
    		return true;
    	return false;
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
    
    public static void setOffset(int offset) { rayon = offset/2; }    
    public static int getOffset() {	return rayon*2; }
	public boolean isActive() {return active;}
	public void setActive(boolean isActive) {active = isActive;}
	public float getX() {return mPosX;}
	public void setX(float x) {this.mPosX = x;}
	public float getY() {return mPosY;}
	public void setY(float mPosY) {this.mPosY = mPosY;}
	public float getElasticity() {return mElasticity;}
	public int getRayon() {return rayon;}

}
