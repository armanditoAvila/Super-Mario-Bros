package smb;

import java.awt.geom.Rectangle2D;

import jig.engine.physics.vpe.VanillaAARectangle;


public class movable extends VanillaAARectangle {
	movable(String s) {
		super(s);
	}

	@Override
	public void update(long deltaMs) {
		return;
	}

	boolean checkHorizontalCollision(Rectangle2D boundingBox, double X, double Y, int Xdirection) {
		int mapX = (int) X / smb.TILE_SIZE;
		int mapY = (int) Y / smb.TILE_SIZE;
		if (Xdirection == 1) {
			if (checkTopRight(boundingBox, mapX, mapY) || checkRight(boundingBox, mapX, mapY) || checkLowerRight(boundingBox, mapX, mapY)) {
				return true;
			}
		} else if (Xdirection == 3) {
			if (checkTopLeft(boundingBox, mapX, mapY) || checkLeft(boundingBox, mapX, mapY) || checkLowerLeft(boundingBox, mapX, mapY)) {
				return true;
			}
		}
		return false;
	}

	boolean checkVerticalCollision(Rectangle2D boundingBox, double d, double e, double vSpeedY) {
		int mapX = (int) d / smb.TILE_SIZE;
		int mapY = (int) e / smb.TILE_SIZE;
		if (vSpeedY < 0) {
			if (checkTopLeft(boundingBox, mapX, mapY) || checkTopMid(boundingBox, mapX, mapY) || checkTopRight(boundingBox, mapX, mapY)) {
				return true;
			}
		} else {
			if (checkLowerLeft(boundingBox, mapX, mapY) || checkLowerMid(boundingBox, mapX, mapY) || checkLowerRight(boundingBox, mapX, mapY)) {
				return true;
			}
		}
		return false;
	}

	boolean checkLowerLeft(Rectangle2D boundingBox, int mapX, int mapY) {

		if ((mapX - 1) >= 0 && (mapY + 1) < smb.mapHeight && smb.map.getMapPosition(mapX - 1, mapY + 1) != null && boundingBox.intersects(smb.map.getMapPosition(mapX - 1, mapY + 1).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}

	boolean checkLowerMid(Rectangle2D boundingBox, int mapX, int mapY) {
		if (mapY + 1 < smb.mapHeight && smb.map.getMapPosition(mapX, mapY + 1) != null && boundingBox.intersects(smb.map.getMapPosition(mapX, mapY + 1).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}

	boolean checkLowerRight(Rectangle2D boundingBox, int mapX, int mapY) {
		if ((mapX + 1) < smb.mapWidth && (mapY + 1) < smb.mapHeight && smb.map.getMapPosition(mapX + 1, mapY + 1) != null && boundingBox.intersects(smb.map.getMapPosition(mapX + 1, mapY + 1).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}

	boolean checkLeft(Rectangle2D boundingBox, int mapX, int mapY) {
		if ((mapX - 1) >= 0 && smb.map.getMapPosition(mapX - 1, mapY) != null && boundingBox.intersects(smb.map.getMapPosition(mapX - 1, mapY).getBoundingBox())) {
			return true;
		} else {
			return false;
		}

	}

	boolean checkRight(Rectangle2D boundingBox, int mapX, int mapY) {
		if ((mapX + 1) < smb.mapWidth && smb.map.getMapPosition(mapX + 1, mapY) != null && boundingBox.intersects(smb.map.getMapPosition(mapX + 1, mapY).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}

	boolean checkTopLeft(Rectangle2D boundingBox, int mapX, int mapY) {
		if ((mapX - 1) >= 0 && (mapY - 1) < smb.mapHeight && smb.map.getMapPosition(mapX - 1, mapY - 1) != null && boundingBox.intersects(smb.map.getMapPosition(mapX - 1, mapY - 1).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}

	boolean checkTopMid(Rectangle2D boundingBox, int mapX, int mapY) {
		if ((mapY - 1) >= 0 && (mapY - 1) < smb.mapHeight && smb.map.getMapPosition(mapX, mapY - 1) != null && boundingBox.intersects(smb.map.getMapPosition(mapX, mapY - 1).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}

	boolean checkTopRight(Rectangle2D boundingBox, int mapX, int mapY) {

		if ((mapX + 1) < smb.mapWidth && (mapY - 1) < smb.mapHeight && smb.map.getMapPosition(mapX + 1, mapY - 1) != null && boundingBox.intersects(smb.map.getMapPosition(mapX + 1, mapY - 1).getBoundingBox())) {
			return true;
		} else {
			return false;
		}
	}
	
}