package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class BreakableGreenWallPieces extends VanillaAARectangle {
	BreakableGreenWallPieces(double d, double e, int location) {
		super(Smb.SPRITE_SHEET + "#breakableGreenWallPieces", 0);
		if(location==1){
		position = new Vector2D(d-8, e-8);
		velocity = new Vector2D(-10,-50);
		}
		else if(location==2){
			position = new Vector2D(d-8, e+8);
			velocity = new Vector2D(-20,-50);
		}
		else if(location==3){
			position = new Vector2D(d+8, e-8);
			velocity = new Vector2D(20,-50);
		}
		else if(location==4){
			position = new Vector2D(d+8, e+8);
			velocity = new Vector2D(10,-50);
		}
	}

	@Override
	public void update(long deltaMs) {
		if(!active){
		return;
		}
		
		if(position.getY()>Smb.WORLD_HEIGHT){
			active=false;
		}
		velocity = new Vector2D(velocity.getX(), velocity.getY()+Smb.gravity) ;
		position = position.translate(velocity.scale(deltaMs / 1000.0));
	}
	
}
