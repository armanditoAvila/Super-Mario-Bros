package smb;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class BackGround extends VanillaAARectangle {
	BackGround(int gamelvl) {
		super(Smb.BACKGROUND_SHEET+"#map"+gamelvl, 13);
		position = new Vector2D(0,0);
	}

	@Override
	public void update(long deltaMs) {
		return;
	}

	
}