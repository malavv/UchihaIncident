package com.ninja.test.ballctrl;

// Interface for movable objects. Those who implements this interface will be refresh every single frame
// for a matter of implementation, we assume that anything movable is also collidable.
public class Movable extends Collidable{
	
    private float mCoefX;
    private float mCoefY;
    private float mLastPosX;
    private float mLastPosY;
    
    private final float MAX_SPEED = 5;
    private final float FRICTION = 0.1f;
	
    // public pour le débogage
    private float speedX = 0;
	private float speedY = 0;
	
	private float lastDelta = .8f;
	private float drag;
	private float sensitivity = 1;
	
	protected int orientation = 0;

	Movable(float aPosX, float aPosY, float aElasticity) {
		super(aPosX, aPosY, aElasticity);
		mLastPosX = aPosX;
		mLastPosY = aPosY;
		// forttement
		drag =  1f - FRICTION;
	}
	
	public void increaseSpeed(float increment) {
		sensitivity += increment;
	}
	
	public void resetSpeed() {
		sensitivity = 1;
	}


	/**
	 * Calcule de la position et de la vitesse
	 * @param delta 
	 */
	public void computePhysics(float delta) {
		
		// on calcule la vitesse à l'instant présent
		speedX = (getX() - mLastPosX) / lastDelta;
		speedY = (getY() - mLastPosY) / lastDelta;
		lastDelta = delta;
		
		float scaleSpeed = Math.abs(speedX) + Math.abs(speedY);
		
		// permet de limiter la vitesse
		if( scaleSpeed > MAX_SPEED) {
			speedX *= MAX_SPEED / scaleSpeed;
			speedY *= MAX_SPEED / scaleSpeed;
		}
		
//		float scaleAcceleration = Math.abs(mCoefX) + Math.abs(mCoefY);
		
		// limiter l'accélération
//		if(scaleAcceleration > MAX_SPEED) {
//			mCoefX *= MAX_SPEED / scaleAcceleration;
//			mCoefY *= MAX_SPEED / scaleAcceleration;
//		}
				
		float dtdt = delta*delta;
		
		// La vitesse est constante, ce qui change c'est uniquement la direction
		// dans laquelle on se déplace.
		float x = getX() + speedX * delta * drag + mCoefX * dtdt * sensitivity;
		float y = getY() + speedY * delta * drag + mCoefY * dtdt * sensitivity;
		
		// on récupère l'ancienne position en x,y avant de la mettre à jours
		mLastPosX = getX();
		mLastPosY = getY();
		
		setX((int)x);
		setY((int)y);
		
		// vitesse entre la position précédente et l'actuelle
		float dx = getX() - mLastPosX;
		float dy = getY() - mLastPosY;
		
		// booléen pour savoir si notre direction est verticale ou horizontale
		boolean horizontal = Math.abs(dx) > Math.abs(dy);
		
		// puis on trouve la direction exacte
		// ceci permet d'orienter le dessin du ninja
		if(horizontal) {
			if(dx > 0) {
				orientation = Global.RIGHT;
			} else {
				orientation = Global.LEFT;
			}
		} else {
			if(dy > 0) {
				orientation = Global.FRONT;
			} else {
				orientation = Global.BACK;
			}
		}
	}
	
	/**
	 * met a jours la position courrante avec celle passée en parametre et
	 * l'ancienne position avec celle qu'on veut updater
	 * @param x
	 * @param y
	 */
	public void updatePosition(float x, float y) {
		mLastPosX = getX();
		mLastPosY = getY();
		setX(x);
		setY(y);
	}
	
	/**
	 * 
	 * @param x - vitesse sur l'ave des x
	 * @param y - vitesse sur l'axe des y
	 */
	public void updateSpeed(float x, float y) {
//		mCoefX = x;
//		mCoefY = y;
	}

	public float getCoefX() {
		return mCoefX;
	}

	public void setCoefX(float mCoefX) {
		this.mCoefX = mCoefX;
	}
	
	public void invCoefX() {
		mCoefX *= -1;
	}

	public float getCoefY() {
		return mCoefY;
	}

	public void setCoefY(float mCoefY) {
		this.mCoefY = mCoefY;
	}
	
	public void invCoefY() {
		mCoefY *= -1;
	}

	public float getLastPosX() {
		return mLastPosX;
	}

	public void setLastPosX(float f) {
		this.mLastPosX = f;
	}

	public float getLastPosY() {
		return mLastPosY;
	}

	public void setLastPosY(float f) {
		this.mLastPosY = f;
	}

}
