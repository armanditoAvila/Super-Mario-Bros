package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class PowerUpFlower extends VanillaAARectangle {
	double appearSpeed=-20;
	int frameDelay=150;
	long frameTime;
	double stopPosition;
	PowerUpFlower(double x, double y) {
		super(Smb.SPRITE_SHEET + "#powerUpFlower", 17);
		position = new Vector2D(x, y-10);
		velocity = new Vector2D(0,appearSpeed);
		setFrame(0);
		frameTime=System.currentTimeMillis();
		stopPosition=y-Smb.TILE_SIZE;
	}

	@Override
	public void update(long deltaMs) {
		if(!active){
		return;
		}

		if(System.currentTimeMillis()-frameTime > frameDelay){
			if(getFrame()==0){
				setFrame(1);
				frameTime=System.currentTimeMillis();
			}else if(getFrame()==1){
				setFrame(2);
				frameTime=System.currentTimeMillis();
			}else if(getFrame()==2){
				setFrame(3);
				frameTime=System.currentTimeMillis();
			}else if(getFrame()==3){
				setFrame(0);
				frameTime=System.currentTimeMillis();
			}
		}
		
		if(getPosition().getY()>stopPosition){
		position = position.translate(velocity.scale(deltaMs / 1000.0));
		}
	}
	
	public void setDead(){
		active=false;
	}
}
