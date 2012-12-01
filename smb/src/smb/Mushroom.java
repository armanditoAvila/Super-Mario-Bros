package smb;

import jig.engine.ResourceFactory;
import jig.engine.audio.jsound.AudioClip;
import jig.engine.hli.physics.SpriteUpdateRules;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Mushroom extends VanillaAARectangle{

	SpriteUpdateRules updateRule;
	Smb smbObject;
	double speed=100;
	boolean poppedUp;
	double stopPosition;
	Vector2D appearingVelocity=new Vector2D(0,-speed);
	private static AudioClip clip = ResourceFactory.getFactory().getAudioClip(Smb.audioSource + "smb_powerup_appears.wav");
	public Mushroom(double x, double y) {
		super(Smb.SPRITE_SHEET + "#levelUpMushroom",22);
		position = new Vector2D(x, y-10);
		velocity = new Vector2D(speed, speed);
		stopPosition=y-getHeight();
		clip.play();
	}

	@Override
	public void update(long deltaMs) {
		if(!active){
			return;
		}
		if(!poppedUp){
			if(position.getY()<stopPosition){
				poppedUp=true;
				return;
			}
			position = position.translate(appearingVelocity.scale(deltaMs / 1000.0));

		}
		else{
			position = position.translate(velocity.scale(deltaMs / 1000.0));
		}

	}
	
	public void setDead(){
		active=false;
	}

}
