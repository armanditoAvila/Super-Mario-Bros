package smb;

import java.awt.geom.Rectangle2D;


import jig.engine.ResourceFactory;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class player extends VanillaAARectangle {
	final int jumpV = -80;
	int Xdirection, Ydirection;
	int speed = 100;
	int level;
	double vSpeedX, vSpeedY;
	boolean jumped;
	boolean onGround;
	Rectangle2D boundingBox;
	Vector2D tempVelocity, tempPosition;
	final long frameTime = 800;
	
	long timeSinceLastUpdate = frameTime;
	boolean move = true;
	
	Vector2D currentVelocity;
	Vector2D previousVelocity;

	player(int x, int y) {
		super(smb.SPRITE_SHEET + "#mario", 4);
		position = new Vector2D(x * smb.TILE_SIZE, y * smb.TILE_SIZE);
		currentVelocity = Vector2D.ZERO;
		previousVelocity = Vector2D.ZERO;

	}

	public void update(final long deltaMs) {
		if (!active) {
			return;
		}
		

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

	
	
	
	/**
		 * To Animate the player
		 * Uncomment this code once ready to animate the player
		 */
		
	//	this.updateVelocity();
	//	this.updateFrame(deltaMs);
	}
	

	public void updateVelocity() {
		this.previousVelocity = this.currentVelocity;
		this.currentVelocity = this.velocity;

		if (this.previousVelocity.getX() > 0 && this.currentVelocity.getX() < 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#marioleft");
		} /* else if (this.previousVelocity.getX() == 0
				&& this.currentVelocity.getX() < 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#marioleft");
		} else if (this.previousVelocity.getX() < 0
				&& this.currentVelocity.getX() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#mario");
		} else if (this.previousVelocity.getX() == 0
				&& this.currentVelocity.getX() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#mario");
		}

		else if (this.previousVelocity.getY() < 0
				&& this.currentVelocity.getY() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#mariodown");
		} else if (this.previousVelocity.getY() == 0
				&& this.currentVelocity.getY() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#mariodown");
		}

		else if (this.previousVelocity.getY() > 0
				&& this.currentVelocity.getY() < 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#marioup");
		} else if (this.previousVelocity.getY() == 0
				&& this.currentVelocity.getY() < 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#marioup");
		}*/
	}
	
	public void updateFrame(long deltaMs) {

		if (this.getVelocity().getX() == 0 && this.getVelocity().getY() == 0) {
			this.setFrame(1);
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
}