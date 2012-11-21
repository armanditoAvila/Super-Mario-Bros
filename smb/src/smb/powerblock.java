package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class powerblock extends VanillaAARectangle {
	long frameTime;
	int frameDisplayTime=100;
	boolean frameTimeSet;
	boolean dead;
	powerblock(int x, int y) {
		super(smb.SPRITE_SHEET + "#breakableBrownWall");
		position = new Vector2D(x * smb.TILE_SIZE, y * smb.TILE_SIZE);
	}

	@Override
	public void update(long deltaMs) {
		if(dead){
			if(getFrame()!=3){
			setFrame(3);
			}
			return;
		}
		if(!frameTimeSet){
			frameTime=System.currentTimeMillis();
			if(getFrame()==2){
				setFrame(0);
			}
			else if(getFrame()==1){
				setFrame(2);
			}
			else{
				setFrame(0);
			}
			frameTimeSet=true;
		}
		else if(frameTimeSet && System.currentTimeMillis()-frameTime>frameDisplayTime){
			frameTimeSet=false;
		}

	}
	
	public void setDead(){
		this.dead=true;
	}

	
}