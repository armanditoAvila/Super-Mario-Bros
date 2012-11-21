package smb;

public class gamemap{
	int mapwidth;
	int mapheight;
	walls[][] map;
	
	gamemap(int x, int y){
		map= new walls[x][y];
	}
	
	public void setMapWall(int x, int y, walls w){
		map[x][y]=w;
	}
	
	public walls getMapPosition(int x, int y){
		return map[x][y];
	}
}