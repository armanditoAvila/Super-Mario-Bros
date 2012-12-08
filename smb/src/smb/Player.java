package smb;

import java.awt.geom.Rectangle2D;


import jig.engine.ResourceFactory;
import jig.engine.audio.AudioState;
import jig.engine.audio.jsound.AudioClip;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Player extends VanillaAARectangle {
	final int jumpV = -80;
	int Xdirection, Ydirection;
	int speed = 100;
	int level;
	int live;
	long playerTimer = 100;
	int startingPositionX;
	int startingPositionY;
	double vSpeedX, vSpeedY;
	boolean jumped;
	boolean onGround;
	Rectangle2D boundingBox;
	Vector2D tempVelocity, tempPosition;
	final long frameTime = 180;
	
	long timeSinceLastUpdate = frameTime;
	boolean move = true;
	private static AudioClip die = ResourceFactory.getFactory().getAudioClip(Smb.audioSource + "smb_mariodie.wav");
	private static AudioClip powerup = ResourceFactory.getFactory().getAudioClip(Smb.audioSource + "smb_powerup.wav");
	Vector2D currentVelocity;
	Vector2D previousVelocity;
	
	/* Justin's Physics Variables */
	public double playerXvel;
	public double playerXacc;

	public double playerYvel;
	public double playerYacc;

	public double gravity;

	public double maxAvel;	// Mid-Air X
	public double maxXvel;
	public double maxYvel;
	public double wraYvel;
	public boolean MARIO = true;
	
	

	Player(int x, int y, String playerName) {
		super(Smb.SPRITE_SHEET2 + playerName, 4);
		
		position = new Vector2D(x * Smb.TILE_SIZE, y * Smb.TILE_SIZE);
		
		startingPositionX=x;
		startingPositionY=y;
		live=3;
		currentVelocity = Vector2D.ZERO;
		previousVelocity = Vector2D.ZERO;
		playerXvel = playerXacc = playerYvel = playerYacc = 0;
		if(MARIO) {
			maxXvel = Physics.mg_max_vel_walk;
		} else {
			maxXvel = Physics.lg_max_vel_walk;
		}
	}

	public void update(final long deltaMs) {
		if (!active) {
			return;
		}
				
		if(this.getPosition().getX() < 0){
			this.setPosition(new Vector2D(0,position.getY()));
		}
		
		if(position.getY() > Smb.WORLD_HEIGHT){
			this.playerTimer = 100;
			Smb.music.pause();
			restartPosition();
		}
		
		accelerate();
		velocity = new Vector2D(playerXvel, playerYvel);
		position = position.translate(velocity.scale(deltaMs / 1000.0));
	
	/**
		 * To Animate the player
		 * Uncomment this code once ready to animate the player
		 */
		
		this.updateVelocity();
		this.updateFrame(deltaMs);
	}
	

	public void updateVelocity() {
		this.previousVelocity = this.currentVelocity;
		this.currentVelocity = this.velocity;

	/*	if (this.previousVelocity.getX() > 0 && this.currentVelocity.getX() < 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					Smb.SPRITE_SHEET2 + "#marioleft");
		} else if (this.previousVelocity.getX() == 0
				&& this.currentVelocity.getX() < 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					Smb.SPRITE_SHEET2 + "#marioleft");
		} else */if (this.previousVelocity.getX() < 0
				&& this.currentVelocity.getX() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					Smb.SPRITE_SHEET2 + "#mario");
		} else if (this.previousVelocity.getX() == 0
				&& this.currentVelocity.getX() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					Smb.SPRITE_SHEET2 + "#mario");
		}
	}
	
	public void updateFrame(long deltaMs) {

		if (this.getVelocity().getX() == 0) {
			this.setFrame(0);
			this.timeSinceLastUpdate = this.frameTime;
		} else {
			this.timeSinceLastUpdate -= deltaMs;
			if (this.timeSinceLastUpdate <= 0) {

				if (this.getFrame() == this.getFrameCount() - 1) {
					move = false;
					this.setFrame(this.getFrame() - 1);
					this.timeSinceLastUpdate = this.frameTime;
				} else if (this.getFrame() == 0) {
					move = true;
					this.setFrame(this.getFrame() + 1);
					this.timeSinceLastUpdate = this.frameTime;
				} else if (move) {
					this.setFrame(this.getFrame() + 1);
				} else {
					this.setFrame(this.getFrame() - 1);
				}

			}
		}

	}
	
	void accelerate() {
		/* X Calculations And Checks */
		if(playerXvel > 0 && (playerXvel + playerXacc) <= 0) {
			playerXvel = 0;
			playerXacc = 0;
		} else if(playerXvel < 0 && (playerXvel + playerXacc) >= 0) {
			playerXvel = 0;
			playerXacc = 0;
		} else {
			playerXvel = playerXvel + playerXacc;
			if(Math.abs(playerXvel) > Math.abs(maxXvel)) playerXvel = maxXvel;
		}
		
		/* Y Calculations And Checks */
		if(playerYvel <= 0) playerYvel = playerYvel + playerYacc;
		if(playerYvel > 0) playerYvel = playerYvel + gravity;
		if(playerYvel > Physics.bf_max_vel_fall) playerYvel = Physics.bf_fall_vel_wrap;
	}
	
	public void marioDie(){
		//falls off the map animation
		die.play();
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			System.err.println("Error on sleeping");
			return;
		}
	}
	
	public void levelUp(){
		level++;
		powerup.play();
		//level up animation transition
	}
	
	public void restartPosition(){
		live--;
		marioDie();
		if(live>=0){
			
			Smb.music.resume();
			position=new Vector2D(startingPositionX*Smb.TILE_SIZE,startingPositionY*Smb.TILE_SIZE);
			playerXvel = playerXacc = playerYvel = playerYacc = 0;
			jumped = false;
		}
		else{
			Smb.restartLevel=true;
		}
		
	}
}