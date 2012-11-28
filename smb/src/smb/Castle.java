package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Castle extends VanillaAARectangle{

	Castle(int x, int y) {
		super(Smb.SPRITE_SHEET + "#castle", 10);
		position = new Vector2D(x * Smb.TILE_SIZE, y * Smb.TILE_SIZE);
	}
	
	@Override
	public void update(long deltaMs) {
		return;
	}
}
