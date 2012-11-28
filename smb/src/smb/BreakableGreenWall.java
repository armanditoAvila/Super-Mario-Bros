package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;
import jig.engine.ResourceFactory;
import jig.engine.audio.jsound.AudioClip;

public class BreakableGreenWall extends VanillaAARectangle {
private static AudioClip breakApart = ResourceFactory.getFactory().getAudioClip("resources/" + "audio/smb_breakblock.wav");
	BreakableGreenWall(int x, int y) {
		super(Smb.SPRITE_SHEET + "#breakableGreenWall", 1);
		position = new Vector2D(x * Smb.TILE_SIZE, y * Smb.TILE_SIZE);
	}

	@Override
	public void update(long deltaMs) {
		if(!active){
		return;
		}
	}

	public void breakApart(){
		active=false;
		breakApart.play();
		Smb.backGroundLayer.add(new BreakableGreenWallPieces(getCenterPosition().getX(), getCenterPosition().getY(), 1));
		Smb.backGroundLayer.add(new BreakableGreenWallPieces(getCenterPosition().getX(), getCenterPosition().getY(), 2));
		Smb.backGroundLayer.add(new BreakableGreenWallPieces(getCenterPosition().getX(), getCenterPosition().getY(), 3));
		Smb.backGroundLayer.add(new BreakableGreenWallPieces(getCenterPosition().getX(), getCenterPosition().getY(), 4));
	}
	
}