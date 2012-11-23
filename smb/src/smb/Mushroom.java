package smb;

import jig.engine.hli.physics.SpriteUpdateRules;
import jig.engine.physics.vpe.VanillaSphere;
import jig.engine.util.Vector2D;

public class Mushroom extends VanillaSphere {

	SpriteUpdateRules updateRule;
	SMB smbObject;
	final long frameTime = 800;
	long timeSinceLastUpdate = frameTime;

	public Mushroom(int x, int y, String ghostName) {
		super(SMB.SPRITE_SHEET + ghostName);
		position = new Vector2D(x, y);
		velocity = new Vector2D(20, 20);
	}

	@Override
	public void update(long deltaMs) {

		position = position.translate(velocity.scale(deltaMs / 100.0));
		this.updateFrame(deltaMs);

	}

	public void updateFrame(long deltaMs) {
		this.timeSinceLastUpdate -= deltaMs;

		if (this.timeSinceLastUpdate <= 0) {
			if (this.getFrame() == (this.getFrameCount() - 1)) {
				this.setFrame(0);
			} else {
				this.setFrame(this.getFrame() + 1);
			}

			this.timeSinceLastUpdate = this.frameTime;
		}
	}

}
