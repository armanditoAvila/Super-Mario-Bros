package smb;

import java.awt.geom.Rectangle2D;

import jig.engine.ResourceFactory;
import jig.engine.audio.jsound.AudioClip;
import jig.engine.hli.physics.SpriteUpdateRules;
import jig.engine.physics.vpe.VanillaSphere;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Turtle extends VanillaAARectangle {

	int Xdirection, Ydirection;
	int speed = 30;
	int frameDelay = 200;
	int deadDelay = 1000;
	long frameTime;
	long deadTime;
	boolean dead;
	boolean deadTimeSet;
	boolean frameTimeSet;
	boolean onGround;
	Rectangle2D boundingBox;
	private static AudioClip stomp = ResourceFactory.getFactory().getAudioClip(
			"resources/" + "audio/smb_stomp.wav");

	public Turtle(int x, int y, String turtleType) {

		super(Smb.SPRITE_SHEET + turtleType, 7);
		frameTime = System.currentTimeMillis();
		position = new Vector2D(x * Smb.TILE_SIZE, y * Smb.TILE_SIZE + 14);
		velocity = new Vector2D(-speed, speed);
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
				frameTime = System.currentTimeMillis();
			}
		}

		if(this.getPosition().getY() >= 353){
			this.setActivation(false);
		}
		/*
		 * if(outOfScreen()){ this.setActivation(false); }
		 */

		position = position.translate(velocity.scale(deltaMs / 1000.0));

	}

	public void setDead() {
		this.dead = true;
		deadTime = System.currentTimeMillis();
		setFrame(2);
		stomp.play();
	}

	boolean outOfScreen() {
		if (this.position.getX() + Smb.WORLD_WIDTH < Smb.currentCenter
				|| this.position.getY() > Smb.WORLD_HEIGHT + this.getHeight()) {
			return true;
		} else {
			return false;
		}
	}

}
