package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;
import jig.engine.ResourceFactory;
import jig.engine.audio.jsound.AudioClip;

public class breakableBrownWall extends VanillaAARectangle {
private static AudioClip breakApart = ResourceFactory.getFactory().getAudioClip("resources/" + "smb_breakblock.wav");
	breakableBrownWall(int x, int y) {
		super(smb.SPRITE_SHEET + "#breakableBrownWall", 1);
		position = new Vector2D(x * smb.TILE_SIZE, y * smb.TILE_SIZE);
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
		smb.backGroundLayer.add(new breakableBrownWallPieces(getCenterPosition().getX(), getCenterPosition().getY(), 1));
		smb.backGroundLayer.add(new breakableBrownWallPieces(getCenterPosition().getX(), getCenterPosition().getY(), 2));
		smb.backGroundLayer.add(new breakableBrownWallPieces(getCenterPosition().getX(), getCenterPosition().getY(), 3));
		smb.backGroundLayer.add(new breakableBrownWallPieces(getCenterPosition().getX(), getCenterPosition().getY(), 4));
	}
	
}