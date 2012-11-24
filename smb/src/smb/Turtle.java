package smb;

import jig.engine.hli.physics.SpriteUpdateRules;
import jig.engine.physics.vpe.VanillaSphere;
import jig.engine.util.Vector2D;

public class Turtle extends VanillaSphere {

	SpriteUpdateRules updateRule;
	smb smbObject;
	int speed = 30;
	int retractedShellSpeed=50;
	final long frameTime = 800;
	boolean retracted;
	long timeSinceLastUpdate = frameTime;

	public Turtle(int x, int y, String ghostName) {
		/*
		super(smb.SPRITE_SHEET + ghostName);
		position = new Vector2D(x, y);
		velocity = new Vector2D(20, 20);
		*/
		super(smb.SPRITE_SHEET + "#turtle", 7);
		Xdirection=3;
		position = new Vector2D(x * smb.TILE_SIZE, y * smb.TILE_SIZE + 14);
	}

	@Override
	public void update(long deltaMs) {
		if (!active) {
			return;
		}
		if (Xdirection == 1) {
			vSpeedX = speed;
		} else if (Xdirection == 3) {
			vSpeedX = -speed;
		} else {
			vSpeedX = 0;
		}

		vSpeedY += smb.gravity;

		if (vSpeedY > smb.gravity) {
			vSpeedY = smb.gravity;
		}
		
		//this.updateFrame(deltaMs);
		position = position.translate(velocity.scale(deltaMs / 100.0));
	}

	void updateFrame(long deltaMs) {
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
	
	public void setOppositeDirection(){
		if(Xdirection==1){
			Xdirection=3;
		}
		else if(Xdirection==3){
			Xdirection=1;
		}
	}
	
	public void stumped(){
		if(stumped){
		setFrame(5);
		velocity=new Vector2D(vSpeedX,0);
		stumped=true;
		}
		else{
		setFrame(5);
		velocity=new Vector2D(0,0);
		stumped=true;
		}
	}
	
	public void setDead(){
		this.dead=true;
		setFrame(2);
	}

}
