package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class SmallHill extends VanillaAARectangle {
	SmallHill(int x, int y) {
		super(Smb.SPRITE_SHEET + "#smallHill", 14);
		position = new Vector2D(x * Smb.TILE_SIZE, y * Smb.TILE_SIZE);
	}

	@Override
	public void update(long deltaMs) {
		return;
	}

	
}