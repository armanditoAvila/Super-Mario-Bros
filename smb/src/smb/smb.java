package smb;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import jig.engine.audio.jsound.AudioClip;
import jig.engine.FontResource;
import jig.engine.ImageResource;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.ViewableLayer;
import jig.engine.hli.AbstractSimpleGame;
import jig.engine.hli.ImageBackgroundLayer;
import jig.engine.hli.StaticScreenGame;
import jig.engine.hli.physics.SpriteUpdateRules;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.physics.vpe.VanillaPhysicsEngine;
import jig.engine.physics.vpe.VanillaSphere;
import jig.engine.util.Vector2D;




@SuppressWarnings("unused")
public class smb extends AbstractSimpleGame {

	static final int WORLD_WIDTH = 512;

	static final int WORLD_HEIGHT = 448;
	static final double gravity = 20;
	static double currentCenter;
	static final int TILE_SIZE = 32;
	static final int HALF_SCREEN_WIDTH = WORLD_WIDTH/2; 
	static final int HALF_SCREEN_HEIGHT = WORLD_HEIGHT/2;
	int worldPixelLenght;
	int worldPixelHeight;
	static final String SPRITE_SHEET = "resources/mario-spritesheet.png";
	static final double deltaTime = 0.0001;
	static int mapWidth, mapHeight;
	static gamemap map;
	player p;

	// private ViewableLayer splashLayer;
	int leftWidthBreakPoint,rightWidthBreakPoint;
	FontResource scoreboardFont;
	FontResource powerUpsFont;
	String maptext;
	public List<walls> wallarray = new ArrayList<walls>();
	public List<goomba> goombaarray = new ArrayList<goomba>();
	
	static double offset;
	
	public smb() {
		super(WORLD_WIDTH, WORLD_HEIGHT, false);
		ResourceFactory.getFactory().loadResources("resources/", "mario-resources.xml");

		loadLevel("1");
	}

	
	

	private void loadLevel(String level) {
		String ud = System.getProperty("user.dir");
		try {
			String line;
			FileInputStream fstream = new FileInputStream("src/resources/map" +level+".txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			line = br.readLine();
           mapWidth = Integer.parseInt(line);
            this.worldPixelLenght = mapWidth * TILE_SIZE;
            line = br.readLine();
            mapHeight = Integer.parseInt(line);
            this.worldPixelHeight = mapHeight * TILE_SIZE;
            this.leftWidthBreakPoint=HALF_SCREEN_WIDTH;
            this.rightWidthBreakPoint=worldPixelLenght-HALF_SCREEN_WIDTH;
            map = new gamemap(mapWidth, mapHeight);
			int y = 0;
			while((line=br.readLine())!=null){
			
	
				buildMap(line, y);
				y++;
			
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}


	private void buildMap(String s, int y) throws InterruptedException {
		try{
		String line = s;
		for (int x = 0; x < mapWidth; x++) {

			char ch = line.charAt(x);

			if (ch == 'w') {
				walls w = new walls(x,y);
				wallarray.add(w);
				map.setMapWall(x, y, w);
			} else if (ch == 'p') {
				p=new player(x,y);
			} else if (ch == 'g') {
				goombaarray.add(new goomba(x,y));
			} else if (ch == 's') {


			} else if (ch == 't') {


			} else if (ch == 'd') {


			} else if (ch == 'u') {

			} else if (ch == 'v') {

			}
		}
	}
		catch (Exception e) {
			return;
	}
	}

	
	public void render(RenderingContext rc) {
		super.render(rc);
	}


		
	
	

	@Override
	public void update(long deltaMs) {
		
		RenderingContext rc = gameframe.getRenderingContext();
	
		
		/* explicitly draw stuffs */
		Iterator<walls> it = wallarray.iterator();
		while(it.hasNext()){
			walls w=it.next();
			if(w.isActive())
			w.render(rc,-offset,0);
		}

		Iterator<goomba> gt = goombaarray.iterator();
		while(gt.hasNext()){
			goomba w=gt.next();
			if(w.isActive()){
				w.render(rc,-offset,0);
			}
		}

		if(this.p.getPosition().getX() > HALF_SCREEN_WIDTH && this.p.getPosition().getX() < this.rightWidthBreakPoint){
			this.p.render(rc,-this.p.getPosition().getX()+HALF_SCREEN_WIDTH,0);			
				offset=this.p.getPosition().getX()-HALF_SCREEN_WIDTH;
				
				
		}
		else if(this.p.getPosition().getX() < HALF_SCREEN_WIDTH){
			this.p.render(rc,0,0);
			offset=0;
			
		}
		else if(this.p.getPosition().getX() > this.rightWidthBreakPoint){
			this.p.render(rc,-this.p.getPosition().getX() + HALF_SCREEN_WIDTH + this.p.getPosition().getX()-this.rightWidthBreakPoint,0);
		}

		


		keyboard.poll();
		boolean left = keyboard.isPressed(KeyEvent.VK_LEFT);
		boolean right = keyboard.isPressed(KeyEvent.VK_RIGHT);
		boolean up = keyboard.isPressed(KeyEvent.VK_UP);
		boolean down = keyboard.isPressed(KeyEvent.VK_DOWN);
		boolean space = keyboard.isPressed(KeyEvent.VK_SPACE);
		boolean r = keyboard.isPressed(KeyEvent.VK_R);

		

		if (left && !right) {
			this.p.Xdirection=3;

		} else if (right && !left) {
			this.p.Xdirection=1;
		} 
		else{
			this.p.Xdirection=5;
		}
		
		if(space){
			this.p.jumped=true;
		}


		/*manual update call */
		p.update(deltaMs);
		
		
		Object ga[] = goombaarray.toArray();
		for(int i=0;i<ga.length;i++){
			if(((goomba)ga[i]).isActive()){
				if(p.getBoundingBox().intersects(((goomba)ga[i]).getBoundingBox())){
					//System.out.println("Player: " +p.getPosition().getX() +" " +p.getPosition().getY() + " goomba: " + ((goomba)ga[i]).getPosition().getX() + " " +((goomba)ga[i]).getPosition().getY());
					if(!((goomba)ga[i]).dead && p.getPosition().getY()-p.getHeight() < ((goomba)ga[i]).getPosition().getY()-1){
						((goomba)ga[i]).setDead();
						
					}
						
				}
				((goomba)ga[i]).update(deltaMs);
			}
			
		}


	}
	


	public static void main(String[] args) {

		smb p = new smb();
		p.run();

	}

}


	



