package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class UnbreakableWall extends VanillaAARectangle{
	UnbreakableWall(int x, int y, String wallType) {
		super(Smb.SPRITE_SHEET + wallType, 2);
		position = new Vector2D(x * Smb.TILE_SIZE, y * Smb.TILE_SIZE);
	}

	@Override
	public void update(long deltaMs) {
		return;
	}

	
}