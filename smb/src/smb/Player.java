package smb;

import java.awt.geom.Rectangle2D;


import jig.engine.ResourceFactory;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Player extends VanillaAARectangle {
	final int jumpV = -80;
	int Xdirection, Ydirection;
	int speed = 100;
	int level;
	double vSpeedX, vSpeedY;
	boolean jumped;
	boolean onGround;
	Rectangle2D boundingBox;
	Vector2D tempVelocity, tempPosition;
	final long frameTime = 180;
	
	long timeSinceLastUpdate = frameTime;
	boolean move = true;
	
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

	Player(int x, int y) {
		super(Smb.SPRITE_SHEET2 + "#mario", 4);
		position = new Vector2D(x * Smb.TILE_SIZE, y * Smb.TILE_SIZE);
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
		
		/* Commenting Out To Test Mine
		if (Xdirection == 1) {
			vSpeedX = speed;
		} else if (Xdirection == 3) {
			vSpeedX = -speed;
		} else {
			vSpeedX = 0;
		}

		if (jumped) {
			vSpeedY = jumpV;
			jumped = false;
		}

		vSpeedY += smb.gravity;

		if (vSpeedY > smb.gravity) {
			vSpeedY = smb.gravity;
		}

		velocity = new Vector2D(vSpeedX, vSpeedY);
		position = position.translate(velocity.scale(deltaMs / 1000.0));
		*/
		accelerate();
		velocity = new Vector2D(playerXvel, playerYvel);
		position = position.translate(velocity.scale(deltaMs / 1000.0));
		//jumped = false;
	
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
					smb.SPRITE_SHEET2 + "#marioleft");
		} else if (this.previousVelocity.getX() == 0
				&& this.currentVelocity.getX() < 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET2 + "#marioleft");
		} else */if (this.previousVelocity.getX() < 0
				&& this.currentVelocity.getX() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET2 + "#mario");
		} else if (this.previousVelocity.getX() == 0
				&& this.currentVelocity.getX() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET2 + "#mario");
		}
	}
	
	public void updateFrame(long deltaMs) {

		if (this.getVelocity().getX() == 0 && this.getVelocity().getY() == 0) {
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
	
	/* Justin's Physics Handler */
	void accelerate() {
		/* X Calculations And Checks */
		if(playerXvel > 0 && (playerXvel + playerXacc) <= 0) {
			playerXvel = 0;
		} else if(playerXvel < 0 && (playerXvel + playerXacc) >= 0) {
			playerXvel = 0;
		} else {
			playerXvel = playerXvel + playerXacc;
			if(Math.abs(playerXvel) > Math.abs(maxXvel)) playerXvel = maxXvel;
		}
		
		/* Y Calculations And Checks */
		playerYvel = playerYvel + playerYacc;
		//if(playerYvel > maxYvel) playerYvel = wraYvel;
		//System.out.println(playerYvel);
	}
}