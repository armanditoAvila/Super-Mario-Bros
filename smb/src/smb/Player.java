package smb;

import java.awt.geom.Rectangle2D;

import java.util.*;
import jig.engine.ImageResource;
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
	long playerTimer = 300;
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
	final double smallNum = 0.00001;
	double levelZeroLeft;
	double levelZeroRight;
	double levelOneLeft;
	double levelOneRight;
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
		playerXvel = playerXacc = playerYvel = 0;
		gravity = playerYacc = Physics.mj_le_fall_gra;
		if(MARIO) {
			maxXvel = Physics.mg_max_vel_walk;
		} else {
			maxXvel = Physics.lg_max_vel_walk;
		}
		setFrame(4);
		//appendImage();
	}
	
	/*testing stage: the current frame set collection is an immuntable so it gives error when append more images*/
	private void appendImage(){
		try{
			List<ImageResource> smallmario = ResourceFactory.getFactory().getFrames(Smb.SPRITE_SHEET2 + "#mario");
		List<ImageResource> bigmario = ResourceFactory.getFactory().getFrames(Smb.SPRITE_SHEET2 + "#leveledMario");
		smallmario.addAll(bigmario);
		
		this.frames=smallmario;
		
		//this.frames.addAll(bigMario);
		}
		catch(Exception e){
			System.err.println("Error: append images");
			e.printStackTrace();
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
			this.playerTimer = 300;
			Smb.music.pause();
			marioDie();
		}
		
		accelerate();
		velocity = new Vector2D(playerXvel, playerYvel);
		position = position.translate(velocity.scale(deltaMs / 1000.0));
		//System.out.println(getPosition().getY() + "," + getHeight() + "," + (Smb.WORLD_HEIGHT-64));
	
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
		} else if (this.previousVelocity.getX() < 0
				&& this.currentVelocity.getX() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					Smb.SPRITE_SHEET2 + "#mario");
		} else if (this.previousVelocity.getX() == 0
				&& this.currentVelocity.getX() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					Smb.SPRITE_SHEET2 + "#mario");
		}
		*/
	}
	
	public void updateFrame(long deltaMs) {
if(level==0){
			//System.out.println(playerXvel);
			if(playerXvel>0){
				if(System.currentTimeMillis()-levelZeroRight>frameTime){
					if(getFrame()==6){
						setFrame(5);
						levelZeroRight=System.currentTimeMillis();
					}
					else{
						setFrame(6);
						levelZeroRight=System.currentTimeMillis();
					}
				}
			}
			else if(playerXvel<0){
				if(System.currentTimeMillis()-levelZeroLeft>frameTime){
					if(getFrame()==3){
						setFrame(2);
						levelZeroLeft=System.currentTimeMillis();
					}
					else{
						setFrame(3);
						levelZeroLeft=System.currentTimeMillis();
					}
				}
			}
		}
		else if(level==2){
			if(playerXvel>smallNum){
				if(System.currentTimeMillis()-levelOneRight>frameTime){
					if(getFrame()==17){
						setFrame(18);
						levelOneRight=System.currentTimeMillis();
					}
					else if(getFrame()==18){
						setFrame(16);
						levelOneRight=System.currentTimeMillis();
					}
					else{
						setFrame(16);
						levelOneRight=System.currentTimeMillis();
					}
				}
			}
			else if(playerXvel<-smallNum){
				if(System.currentTimeMillis()-levelZeroLeft>frameTime){
					if(getFrame()==12){
						setFrame(11);
						levelOneLeft=System.currentTimeMillis();
					}
					else if(getFrame()==11){
						setFrame(13);
						levelOneLeft=System.currentTimeMillis();
					}
					else{
						setFrame(13);
						levelOneLeft=System.currentTimeMillis();
					}
				}
			}
		}
	
	/*
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
*/
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
		if(playerYvel > 0 || !jumped) playerYvel = playerYvel + gravity;
		if(playerYvel > Physics.bf_max_vel_fall) playerYvel = Physics.bf_fall_vel_wrap;
	}
	
	public void marioDie(){
		//falls off the map animation
	//	setFrame(0);
		Smb.music.pause();
		die.play();
		Smb.deathDelay=true;
		Smb.deathDelayTime=System.currentTimeMillis();
		//Smb.oneGameCycle=true;
		/*
		try {
			Thread.sleep(3300);
		} catch (InterruptedException e) {
			System.err.println("Error on sleeping");
			return;
		}
		*/
	}
	
	public void levelUp(){
		level++;
		powerup.play();
		//level up animation transition
	}
	
	public void restartPosition(){
	Smb.deathDelay=false;
		live--;
		if(live>=0){
			
			Smb.music.resume();
			setFrame(4);
			position=new Vector2D(startingPositionX*Smb.TILE_SIZE,startingPositionY*Smb.TILE_SIZE);
			playerXvel = playerXacc = playerYvel = 0;
			playerYacc = Physics.mj_le_fall_gra;
			jumped = false;
		}
		else{
			Smb.restartLevel=true;
		}
		
	}
}