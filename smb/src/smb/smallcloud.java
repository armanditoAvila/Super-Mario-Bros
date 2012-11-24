package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class smallcloud extends VanillaAARectangle {
	smallcloud(int x, int y) {
		super(smb.SPRITE_SHEET + "#smallCloud", 12);
		position = new Vector2D(x * smb.TILE_SIZE, y * smb.TILE_SIZE);
	}

	@Override
	public void update(long deltaMs) {
		return;
	}

	
}