package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class questionBlock extends VanillaAARectangle{
	boolean dead;
	int frameDelay=200;
	boolean frameTimeSet;
	long frameTime;
	questionBlock(int x, int y) {
		super(smb.SPRITE_SHEET + "#questionBlock", 11);
		position = new Vector2D(x * smb.TILE_SIZE, y * smb.TILE_SIZE);
	}

	@Override
	public void update(long deltaMs) {
		if(dead){
			return;
		}
		
		if(frameTimeSet && System.currentTimeMillis()-frameTime>frameDelay){
			if(getFrame()==0){
				setFrame(1);
			}
			else if(getFrame()==1){
				setFrame(2);
			}
			else{
				setFrame(0);
			}
			frameTimeSet=false;
		}
		if(!frameTimeSet){
			frameTime=System.currentTimeMillis();
			frameTimeSet=true;
		}
	}

	public void setDead(){
		setFrame(3);
		
		//spawn mushroom or flower
	}
	
}