package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class GroundWall extends VanillaAARectangle{
	GroundWall(int x, int y, String levelWall) {
		super(Smb.SPRITE_SHEET + levelWall, 3);
		position = new Vector2D(x * Smb.TILE_SIZE, y * Smb.TILE_SIZE);
	}

	@Override
	public void update(long deltaMs) {
		return;
	}

	
}