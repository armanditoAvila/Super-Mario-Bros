package smb;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;


import jig.engine.FontResource;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.hli.StaticScreenGame;
import jig.engine.hli.ScrollingScreenGame;
import jig.engine.hli.physics.SpriteUpdateRules;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.physics.vpe.VanillaSphere;
import jig.engine.util.Vector2D;

public class smb extends ScrollingScreenGame {
	
	/* Original NES Effective Resolution */
	static final int WORLD_WIDTH = 256;
	static final int WORLD_HEIGHT = 224;
	
	static final String SPRITE_SHEET = "resources/spritesheet.png";
	
	FontResource scoreboardFont;
	
	public smb() {
		super(WORLD_WIDTH, WORLD_HEIGHT, false);
		
		ResourceFactory.getFactory().loadResources("resources/", "resources.xml");

		scoreboardFont = ResourceFactory.getFactory().getFontResource(new Font("Sans Serif", Font.PLAIN, 12), Color.black, null);
	}
	
	public void render(RenderingContext rc) {
		super.render(rc);
		scoreboardFont.render("Score: ", rc, AffineTransform.getTranslateInstance(30, 570));
	}

	public void update(long deltaMs) {
		super.update(deltaMs);
		
		boolean left = keyboard.isPressed(KeyEvent.VK_LEFT);
		boolean right = keyboard.isPressed(KeyEvent.VK_RIGHT);
		
		if (left && !right) {
			//moving left
		} else if (right && !left) {
			//moving right
		} else {
			//not moving
		}
	}
	
	public static void main(String[] args) {
		smb s = new smb();
		s.run();		
	}
	
	class Player extends VanillaSphere {
		Player(int num) {
			super(smb.SPRITE_SHEET + "#player" + num);
		}
		public void update(long deltaMs) {
			
		}
	}
	
}
