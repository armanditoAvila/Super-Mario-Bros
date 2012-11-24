package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class verticalHalfPipe extends VanillaAARectangle{

	verticalHalfPipe(int x, int y) {
		super(smb.SPRITE_SHEET + "#verticalHalfPipe", 9);
		position = new Vector2D(x * smb.TILE_SIZE, y * smb.TILE_SIZE);
	}
	
	@Override
	public void update(long deltaMs) {
		return;
	}
}
