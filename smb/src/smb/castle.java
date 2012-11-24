package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class castle extends VanillaAARectangle{

	castle(int x, int y) {
		super(smb.SPRITE_SHEET + "#castle", 10);
		position = new Vector2D(x * smb.TILE_SIZE, y * smb.TILE_SIZE);
	}
	
	@Override
	public void update(long deltaMs) {
		return;
	}
}
