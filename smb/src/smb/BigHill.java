package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class BigHill extends VanillaAARectangle {
	BigHill(int x, int y) {
		super(Smb.SPRITE_SHEET + "#bigHill", 15);
		position = new Vector2D(x * Smb.TILE_SIZE, y * Smb.TILE_SIZE);
	}

	@Override
	public void update(long deltaMs) {
		return;
	}

	
}