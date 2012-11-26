package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;
import jig.engine.ResourceFactory;
import jig.engine.audio.jsound.AudioClip;

public class questionBlock extends VanillaAARectangle{
	boolean dead;
	int frameDelay=200;
	boolean frameTimeSet;
	long frameTime;
	private static AudioClip clip = ResourceFactory.getFactory().getAudioClip("resources/" + "audio/smb_powerup_appears.wav");
	questionBlock(int x, int y) {
		super(smb.SPRITE_SHEET + "#questionBlock", 11);
		position = new Vector2D(x * smb.TILE_SIZE, y * smb.TILE_SIZE);
		setFrame(0);
		frameTime=System.currentTimeMillis();
	}

	@Override
	public void update(long deltaMs) {
		if(dead){
			return;
		}
		
		if(System.currentTimeMillis()-frameTime>frameDelay){
			if(getFrame()==0){
				setFrame(1);
				frameTime=System.currentTimeMillis();
			}
			else if(getFrame()==1){
				setFrame(2);
				frameTime=System.currentTimeMillis();
			}
			else if(getFrame()==2){
				setFrame(0);
				frameTime=System.currentTimeMillis();
			}

		}
	}

	public void setDead(){
		dead=true;
		setFrame(3);
		clip.play();
	}
	
}