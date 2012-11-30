package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;
import jig.engine.ResourceFactory;
import jig.engine.audio.jsound.AudioClip;

public class QuestionBlock extends VanillaAARectangle{
	boolean dead;
	int frameDelay=200;
	boolean frameTimeSet;
	long frameTime;
	QuestionBlock(int x, int y) {
		super(Smb.SPRITE_SHEET + "#questionBlock", 11);
		position = new Vector2D(x * Smb.TILE_SIZE, y * Smb.TILE_SIZE);
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

	}
	
}