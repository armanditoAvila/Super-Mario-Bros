package smb;

import jig.engine.ResourceFactory;
import jig.engine.audio.jsound.AudioClip;
import jig.engine.hli.physics.SpriteUpdateRules;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Mushroom extends VanillaAARectangle{

	SpriteUpdateRules updateRule;
	Smb smbObject;
	double appearSpeed=-20;
	double speed=20;
	boolean popedUp;
	double stopPosition;
	Vector2D appearingVelocity=new Vector2D(0,appearSpeed);
	private static AudioClip clip = ResourceFactory.getFactory().getAudioClip(Smb.audioSource + "smb_powerup_appears.wav");
	public Mushroom(double x, double y) {
		super(Smb.SPRITE_SHEET + "#levelUpMushroom",22);
		position = new Vector2D(x, y-10);
		velocity = new Vector2D(speed, speed);
		setFrame(0);
		stopPosition=y-Smb.TILE_SIZE;
		clip.play();
	}

	@Override
	public void update(long deltaMs) {
		if(!active){
			return;
		}
		if(!popedUp && getPosition().getY()>stopPosition){
			position = position.translate(appearingVelocity.scale(deltaMs / 100.0));

		}
		else{
			popedUp=true;
			position = position.translate(velocity.scale(deltaMs / 100.0));
		}

	}
	
	public void setDead(){
		active=false;
	}

}
