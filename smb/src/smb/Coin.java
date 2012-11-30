package smb;


import jig.engine.ResourceFactory;
import jig.engine.audio.jsound.AudioClip;
import jig.engine.physics.vpe.VanillaAARectangle;

import jig.engine.util.Vector2D;

public class Coin extends VanillaAARectangle {



	final long frameDelay = 100;
	long timeSinceLastUpdate;
	private static AudioClip clip = ResourceFactory.getFactory().getAudioClip(Smb.audioSource + "smb_coin.wav");

	public Coin(double x, double y) {
		super(Smb.SPRITE_SHEET + "#smallCoin",23);
		position = new Vector2D(x+Smb.TILE_SIZE/2, y-10);
		velocity = new Vector2D(0, -20);
		timeSinceLastUpdate=System.currentTimeMillis();
		setFrame(0);
		clip.play();
	}

	@Override
	public void update(long deltaMs) {
		if(!active){
			return;
		}
		position = position.translate(velocity.scale(deltaMs / 100.0));
	    this.updateFrame();

	}

	public void updateFrame() {
		if (System.currentTimeMillis()-timeSinceLastUpdate>frameDelay) {
			if (getFrame()==0) {
				setFrame(1);
				timeSinceLastUpdate=System.currentTimeMillis();
			} else if(getFrame()==1){
				setFrame(2);
				timeSinceLastUpdate=System.currentTimeMillis();
			}else if(getFrame()==2){
				active=false;
			}
		}
	}

}
