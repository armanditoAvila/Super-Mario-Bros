
package smb;


import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Score100 extends VanillaAARectangle{


	double speed=-10;
	final long frameInterval = 2000;
	long currentFrameTime;
	double stopPosition;
	public Score100(double x, double y) {
		super(Smb.SPRITE_SHEET + "#score100",24);
		position = new Vector2D(x, y-20);
		currentFrameTime=System.currentTimeMillis();
	}

	@Override
	public void update(long deltaMs) {
		if(!active){
			return;
		}
		if(System.currentTimeMillis()-currentFrameTime > frameInterval){
			active=false;
		}

	}
}
