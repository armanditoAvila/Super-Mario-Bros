package smb;

import java.awt.geom.Rectangle2D;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class goomba extends VanillaAARectangle {
	int Xdirection, Ydirection;
	int speed = 30;
	int frameDelay=200;
	int deadDelay=1000;
	long frameTime;
	long deadTime;
	double vSpeedX, vSpeedY, previousvSpeedX, previousvSpeedY;
	double tempPositionX, tempPositionY;
	boolean dead;
	boolean deadTimeSet;
	boolean frameTimeSet;
	boolean onGround;
	Rectangle2D boundingBox;

	goomba(int x, int y) {
		super(smb.SPRITE_SHEET + "#Goomba", 5);
		frameTime=System.currentTimeMillis();
		Xdirection = 1;
		Ydirection = 0;
		position = new Vector2D(x * smb.TILE_SIZE, y * smb.TILE_SIZE);
	}

	@Override
	public void update(long deltaMs) {
		if (!active) {
			return;
		}
		
		if(dead && !deadTimeSet){
			deadTime = System.currentTimeMillis();
			System.out.println("deadtime:" + deadTime);
			deadTimeSet=true;
			return;
			 
		}
		else if(dead && (System.currentTimeMillis()-deadTime)>deadDelay){
			this.active=false;
		}else if(dead){
			return;
		}

		if(!dead && frameTimeSet && System.currentTimeMillis()-frameTime>frameDelay){
			if(getFrame()==1){
				setFrame(0);
			}
			else{
				setFrame(1);
			}
			frameTimeSet=false;
		}
		if(!dead && !frameTimeSet){
			frameTime=System.currentTimeMillis();
			frameTimeSet=true;
		}
		
		
		/*
		if(outOfScreen()){
			this.setActivation(false);
		}
		*/
		if (Xdirection == 1) {
			vSpeedX = speed;
		} else if (Xdirection == 3) {
			vSpeedX = -speed;
		} else {
			vSpeedX = 0;
		}

		vSpeedY += smb.gravity;

		if (vSpeedY > smb.gravity) {
			vSpeedY = smb.gravity;
		}

		/*
		boundingBox = new Rectangle2D.Double(this.position.getX(), this.position.getY() + (vSpeedY * (deltaMs / 1000.0)), this.getWidth(), this.getHeight());
		if (checkVerticalCollision(boundingBox, this.position.getX(), this.position.getY(), vSpeedY)) {
			if(Xdirection==1){
			boundingBox = new Rectangle2D.Double(this.position.getX()-0.2, this.position.getY() + (vSpeedY * (deltaMs / 1000.0)), this.getWidth(), this.getHeight());
			}
			else{
				boundingBox = new Rectangle2D.Double(this.position.getX()+0.2, this.position.getY() + (vSpeedY * (deltaMs / 1000.0)), this.getWidth(), this.getHeight());
			}
			
			if (checkVerticalCollision(boundingBox, this.position.getX(), this.position.getY() , vSpeedY)) {
				vSpeedY = 0;
			}
			else{
				if(Xdirection==1){
					position = new Vector2D(this.position.getX()-0.2 , this.position.getY() + (vSpeedY * (deltaMs / 1000.0)));
				}
				else{
					position = new Vector2D(this.position.getX()+0.2 , this.position.getY() + (vSpeedY * (deltaMs / 1000.0)));
				}
				vSpeedX = 0;
			}
			
		}
		
		boundingBox = new Rectangle2D.Double(this.position.getX()+(vSpeedY * (deltaMs / 1000.0)), this.position.getY(), this.getWidth(), this.getHeight());
		if (checkHorizontalCollision(boundingBox, this.position.getX(), this.position.getY(), Xdirection)) {
			boundingBox = new Rectangle2D.Double(this.position.getX(), this.position.getY() + (vSpeedY * (deltaMs / 1000.0)) - 0.1, this.getWidth(), this.getHeight());
			if (checkHorizontalCollision(boundingBox, this.position.getX(), this.position.getY(), Xdirection)) {
				vSpeedX = 0;
				if (Xdirection == 3) {
					Xdirection = 1;
				} else {
					Xdirection = 3;
				}
			} else {
				position = new Vector2D(this.position.getX() + (vSpeedX * (deltaMs / 1000.0)), this.position.getY() + (vSpeedY * (deltaMs / 1000.0)) - 0.1);
				vSpeedX = 0;

			}

		}
*/
		velocity = new Vector2D(vSpeedX, vSpeedY);
		position = position.translate(velocity.scale(deltaMs / 1000.0));
		
		}
	
	public void setOppositeDirection(){
		if(Xdirection==1){
			Xdirection=3;
		}
		else if(Xdirection==3){
			Xdirection=1;
		}
	}
	
	
	public void setDead(){
		this.dead=true;
		setFrame(2);
	}
	
	boolean outOfScreen(){
		if(this.position.getX()+ smb.WORLD_WIDTH < smb.currentCenter || this.position.getY() > smb.WORLD_HEIGHT+this.getHeight()){
			return true;
		}
		else{
			return false;
		}
	}

	
	}
