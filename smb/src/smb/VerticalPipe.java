package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class VerticalPipe extends VanillaAARectangle{
	VerticalPipe(int x, int y, String pipeName, int pipeType) {
		super(Smb.SPRITE_SHEET + pipeName, pipeType);
		position = new Vector2D(x * Smb.TILE_SIZE, y * Smb.TILE_SIZE);
	}

	@Override
	public void update(long deltaMs) {
		return;
	}

	
}