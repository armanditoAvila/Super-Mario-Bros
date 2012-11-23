package smb;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;


import jig.engine.ResourceFactory;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class player extends VanillaAARectangle {
	final int jumpV = -80;
	int Xdirection, Ydirection;
	int speed = 100;
	double vSpeedX, vSpeedY;
	boolean jumped;
	boolean onGround;
	Rectangle2D boundingBox;
	Vector2D tempVelocity, tempPosition;
	
	Vector2D currentVelocity;
	Vector2D previousVelocity;

	player(int x, int y) {
		super(smb.SPRITE_SHEET + "#mario");
		position = new Vector2D(x * smb.TILE_SIZE, y * smb.TILE_SIZE);

	}

	public void update(final long deltaMs) {
		if (!active) {
			return;
		}

		smb.currentCenter = this.getPosition().getX();

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

		boundingBox = new Rectangle2D.Double(this.position.getX(),
				this.position.getY() + (vSpeedY * (deltaMs / 1000.0)),
				this.getWidth(), this.getHeight());
		if (checkVerticalCollision(boundingBox, this.position.getX(),
				this.position.getY())) {
			if (Xdirection == 1) {
				boundingBox = new Rectangle2D.Double(this.position.getX() - 1,
						this.position.getY() + (vSpeedY * (deltaMs / 1000.0)),
						this.getWidth(), this.getHeight());
			} else {
				boundingBox = new Rectangle2D.Double(this.position.getX() + 1,
						this.position.getY() + (vSpeedY * (deltaMs / 1000.0)),
						this.getWidth(), this.getHeight());
			}

			if (checkVerticalCollision(boundingBox, this.position.getX(),
					this.position.getY())) {
				vSpeedY = 0;
			} else {
				if (Xdirection == 1) {
					position = new Vector2D(this.position.getX() - 1,
							this.position.getY()
									+ (vSpeedY * (deltaMs / 1000.0)));
				} else {
					position = new Vector2D(this.position.getX() + 1,
							this.position.getY()
									+ (vSpeedY * (deltaMs / 1000.0)));
				}
				vSpeedX = 0;
			}

		}

		boundingBox = new Rectangle2D.Double(this.position.getX()
				+ (vSpeedY * (deltaMs / 1000.0)), this.position.getY(),
				this.getWidth(), this.getHeight());
		if (checkHorizontalCollision(boundingBox, this.position.getX(),
				this.position.getY())) {
			if (Xdirection == 1) {
				boundingBox = new Rectangle2D.Double(this.position.getX() - 1,
						this.position.getY() + (vSpeedY * (deltaMs / 1000.0))
								- 0.1, this.getWidth(), this.getHeight());
			} else {
				boundingBox = new Rectangle2D.Double(this.position.getX() + 1,
						this.position.getY() + (vSpeedY * (deltaMs / 1000.0))
								- 0.1, this.getWidth(), this.getHeight());
			}
			if (checkHorizontalCollision(boundingBox, this.position.getX(),
					this.position.getY())) {
				vSpeedX = 0;
				if (Xdirection == 3) {
					Xdirection = 1;
				} else {
					Xdirection = 3;
				}
			} else {
				if (Xdirection == 1) {
					position = new Vector2D(this.position.getX()
							+ (vSpeedX * (deltaMs / 1000.0)) - 1,
							this.position.getY()
									+ (vSpeedY * (deltaMs / 1000.0)) - 0.1);
				} else {
					position = new Vector2D(this.position.getX()
							+ (vSpeedX * (deltaMs / 1000.0)) + 1,
							this.position.getY()
									+ (vSpeedY * (deltaMs / 1000.0)) - 0.1);
				}
				vSpeedX = 0;

			}

		}

		velocity = new Vector2D(vSpeedX, vSpeedY);
		position = position.translate(velocity.scale(deltaMs / 1000.0));
		
	/**
	 * To Animate the player
	 * Uncomment this code once ready to animate the player
	 */
	//	this.updateVelocity();
		

	}

	public void updateVelocity() {
		this.previousVelocity = this.currentVelocity;
		this.currentVelocity = this.velocity;

		if (this.previousVelocity.getX() > 0 && this.currentVelocity.getX() < 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#marioleft");
		} else if (this.previousVelocity.getX() == 0
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
		}
	}
	
	
	
	
	boolean checkHorizontalCollision(Rectangle2D boundingBox, double X, double Y) {
		int mapX = (int) X / smb.TILE_SIZE;
		int mapY = (int) Y / smb.TILE_SIZE;
		if (Xdirection == 1) {
			if (checkTopRight(boundingBox, mapX, mapY)
					|| checkRight(boundingBox, mapX, mapY)
					|| checkLowerRight(boundingBox, mapX, mapY)) {
				return true;
			}
		} else if (Xdirection == 3) {
			if (checkTopLeft(boundingBox, mapX, mapY)
					|| checkLeft(boundingBox, mapX, mapY)
					|| checkLowerLeft(boundingBox, mapX, mapY)) {
				return true;
			}
		}
		return false;
	}

	boolean checkVerticalCollision(Rectangle2D boundingBox, double d, double e) {
		int mapX = (int) d / smb.TILE_SIZE;
		int mapY = (int) e / smb.TILE_SIZE;
		if (vSpeedY < 0) {
			if (checkTopLeft(boundingBox, mapX, mapY)
					|| checkTopMid(boundingBox, mapX, mapY)
					|| checkTopRight(boundingBox, mapX, mapY)) {
				return true;
			}
		} else {
			if (checkLowerLeft(boundingBox, mapX, mapY)
					|| checkLowerMid(boundingBox, mapX, mapY)
					|| checkLowerRight(boundingBox, mapX, mapY)) {
				return true;
			}
		}
		return false;
	}

	boolean checkLowerLeft(Rectangle2D boundingBox, int mapX, int mapY) {

		if ((mapX - 1) >= 0
				&& (mapY + 1) < smb.mapHeight
				&& smb.map.getMapPosition(mapX - 1, mapY + 1) != null
				&& boundingBox.intersects(smb.map.getMapPosition(mapX - 1,
						mapY + 1).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}

	boolean checkLowerMid(Rectangle2D boundingBox, int mapX, int mapY) {
		if (mapY + 1 < smb.mapHeight
				&& smb.map.getMapPosition(mapX, mapY + 1) != null
				&& boundingBox.intersects(smb.map
						.getMapPosition(mapX, mapY + 1).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}

	boolean checkLowerRight(Rectangle2D boundingBox, int mapX, int mapY) {
		if ((mapX + 1) < smb.mapWidth
				&& (mapY + 1) < smb.mapHeight
				&& smb.map.getMapPosition(mapX + 1, mapY + 1) != null
				&& boundingBox.intersects(smb.map.getMapPosition(mapX + 1,
						mapY + 1).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}

	boolean checkLeft(Rectangle2D boundingBox, int mapX, int mapY) {
		if ((mapX - 1) >= 0
				&& smb.map.getMapPosition(mapX - 1, mapY) != null
				&& boundingBox.intersects(smb.map
						.getMapPosition(mapX - 1, mapY).getBoundingBox())) {
			return true;
		} else {
			return false;
		}

	}

	boolean checkRight(Rectangle2D boundingBox, int mapX, int mapY) {
		if ((mapX + 1) < smb.mapWidth
				&& smb.map.getMapPosition(mapX + 1, mapY) != null
				&& boundingBox.intersects(smb.map
						.getMapPosition(mapX + 1, mapY).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}

	boolean checkTopLeft(Rectangle2D boundingBox, int mapX, int mapY) {
		if ((mapX - 1) >= 0
				&& (mapY - 1) >= 0
				&& smb.map.getMapPosition(mapX - 1, mapY - 1) != null
				&& boundingBox.intersects(smb.map.getMapPosition(mapX - 1,
						mapY - 1).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}

	boolean checkTopMid(Rectangle2D boundingBox, int mapX, int mapY) {
		if ((mapY - 1) >= 0
				&& (mapY - 1) >= 0
				&& smb.map.getMapPosition(mapX, mapY - 1) != null
				&& boundingBox.intersects(smb.map
						.getMapPosition(mapX, mapY - 1).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}

	boolean checkTopRight(Rectangle2D boundingBox, int mapX, int mapY) {

		if ((mapX + 1) < smb.mapWidth
				&& (mapY - 1) >= 0
				&& smb.map.getMapPosition(mapX + 1, mapY - 1) != null
				&& boundingBox.intersects(smb.map.getMapPosition(mapX + 1,
						mapY - 1).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}

}