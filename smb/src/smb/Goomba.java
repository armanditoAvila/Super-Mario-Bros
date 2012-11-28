package smb;

import java.awt.geom.Rectangle2D;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;
import jig.engine.ResourceFactory;
import jig.engine.audio.jsound.AudioClip;

public class Goomba extends VanillaAARectangle {
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
	private static AudioClip stomp = ResourceFactory.getFactory().getAudioClip("resources/" + "audio/smb_stomp.wav");

	Goomba(int x, int y, String goombaType) {
		//super(smb.SPRITE_SHEET + "#Goomba", 5);
		super(Smb.SPRITE_SHEET + goombaType, 5);
		frameTime=System.currentTimeMillis();
		Xdirection = 1;
		Ydirection = 0;
		position = new Vector2D(x * Smb.TILE_SIZE, y * Smb.TILE_SIZE);
		setFrame(0);
	}

	@Override
	public void update(long deltaMs) {
		if (!active) {
			return;
		}
		
		if (dead && System.currentTimeMillis() - deadTime > deadDelay) {
			this.active = false;

		} else if (dead) {
			return;
		}

		if (!dead && System.currentTimeMillis() - frameTime > frameDelay) {
			if (getFrame() == 1) {
				setFrame(0);
				frameTime = System.currentTimeMillis();
			} else {
				setFrame(1);
				frameTime=System.currentTimeMillis();
			}
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

		vSpeedY += Smb.gravity;

		if (vSpeedY > Smb.gravity) {
			vSpeedY = Smb.gravity;
		}

		
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
	
	
	public void setDead() {
		this.dead = true;
		deadTime = System.currentTimeMillis();
		setFrame(2);
		stomp.play();
	}
	
	boolean outOfScreen(){
		if(this.position.getX()+ Smb.WORLD_WIDTH < Smb.currentCenter || this.position.getY() > Smb.WORLD_HEIGHT+this.getHeight()){
			return true;
		}
		else{
			return false;
		}
	}

	
	}
