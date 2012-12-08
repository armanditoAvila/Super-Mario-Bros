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

import jig.engine.audio.AudioState;
import jig.engine.audio.jsound.AudioClip;
import jig.engine.audio.jsound.AudioStream;
import jig.engine.FontResource;
import jig.engine.ImageResource;
import jig.engine.Keyboard;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.ViewableLayer;
import jig.engine.hli.AbstractSimpleGame;
import jig.engine.hli.ImageBackgroundLayer;
import jig.engine.hli.ScrollingScreenGame;
import jig.engine.hli.StaticScreenGame;
import jig.engine.hli.physics.RectangleCollisionHandler;
import jig.engine.hli.physics.SpriteUpdateRules;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.CollisionHandler;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.physics.vpe.VanillaPhysicsEngine;
import jig.engine.physics.vpe.VanillaSphere;
import jig.engine.util.Vector2D;

@SuppressWarnings("unused")
public class Smb extends ScrollingScreenGame {

	static final int WORLD_WIDTH = 512;

	static final int WORLD_HEIGHT = 448;
	static final double gravity = 20;
	static double currentCenter;
	static final int TILE_SIZE = 32;
	static final int HALF_SCREEN_WIDTH = WORLD_WIDTH / 2;
	static final int HALF_SCREEN_HEIGHT = WORLD_HEIGHT / 2;
	int worldPixelLenght;
	int worldPixelHeight;
	static final String SPRITE_SHEET = "resources/mario-spritesheet.png";
	static final String SPRITE_SHEET2 = "resources/smb_mario_sheet.png";
	static final String audioSource = "resources/audio/";
	static final double deltaTime = 0.0001;
	static int mapWidth, mapHeight;
	int stopWatch=0;
	boolean gameover=false;
	Player p;
	int points;
	int coinNum;
	int world;
	int world_level;

	int gamelvl = 0;
	int questionBlockCount;
	static boolean restartLevel;
	long currentTime;
	long bumpPlayTime;
	int bumpPlayDelay=1000;
	private AudioClip bump,backMusic;
	// private ViewableLayer splashLayer;
	int leftWidthBreakPoint, rightWidthBreakPoint;
	FontResource scoreboardFont,gameOverFont,finalScoreFont;
	FontResource powerUpsFont;
	String maptext;
	// public List<walls> wallarray = new ArrayList<walls>();
	// public List<goomba> goombaarray = new ArrayList<goomba>();
	List<QuestionBlock> powerUpQuestionBlocksArray = new ArrayList<QuestionBlock>();
	long jumpTimer;

	// static double offset;
	private VanillaPhysicsEngine physics;
	static final SpriteUpdateRules UPDATE_RULE = new SpriteUpdateRules(WORLD_WIDTH, WORLD_HEIGHT);
	public static AudioStream music;
	public BodyLayer<VanillaAARectangle> unmovableLayer = new AbstractBodyLayer.NoUpdate<VanillaAARectangle>();
	public BodyLayer<VanillaAARectangle> movableLayer = new AbstractBodyLayer.NoUpdate<VanillaAARectangle>();
	public static BodyLayer<VanillaAARectangle> backGroundLayer = new AbstractBodyLayer.NoUpdate<VanillaAARectangle>();
    public static BodyLayer<VanillaAARectangle> powerUpLayer = new AbstractBodyLayer.NoUpdate<VanillaAARectangle>();

    public Keyboard getKeyboard() { 
    	return this.keyboard;
    }
    
    public List<ViewableLayer> getGameObjectLayers() {
    	return this.gameObjectLayers;
    }
    
	// public AbstractBodyLayer<VanillaAARectangle> playerLayer = new
	// AbstractBodyLayer.NoUpdate<VanillaAARectangle>();
	public Smb() {
		super(WORLD_WIDTH, WORLD_HEIGHT, false);

		physics = new VanillaPhysicsEngine();
		scoreboardFont = ResourceFactory.getFactory().getFontResource(new Font("Sans Serif", Font.BOLD, 15), Color.WHITE, null);
		finalScoreFont = ResourceFactory.getFactory().getFontResource(new Font("Sans Serif", Font.BOLD, 30), Color.red, null);
		gameOverFont = ResourceFactory.getFactory().getFontResource(new Font("Sans Serif", Font.BOLD, 36), Color.red, null);
		ResourceFactory.getFactory().loadResources("resources/", "mario-resources.xml");
		//backMusic  = ResourceFactory.getFactory().getAudioClip(audioSource + "mario1.mp3");
		bump = ResourceFactory.getFactory().getAudioClip(audioSource + "smb_bump.wav");
		
		music = new AudioStream(audioSource + "mario1.mp3");
		
		music.loop(0.35,275);
		
		gameObjectLayers.add(backGroundLayer);
		physics.manageViewableSet(backGroundLayer);
		
		gameObjectLayers.add(powerUpLayer);
		physics.manageViewableSet(powerUpLayer);
		
		gameObjectLayers.add(unmovableLayer);
		physics.manageViewableSet(unmovableLayer);
		
		gameObjectLayers.add(movableLayer);
		physics.manageViewableSet(movableLayer);
		
		RectangleCollisionHandler<VanillaAARectangle, VanillaAARectangle> d = new RectangleCollisionHandler<VanillaAARectangle, VanillaAARectangle>(movableLayer, unmovableLayer) {
			@Override
			public void collide(final VanillaAARectangle a, final VanillaAARectangle b) {
				/*
				 * Most objects other than mario have a different collision
				 * condition because they don't activate other objects.
				 */
				if (a.type != 4) {
					if (a.type == 22) {
						if(!((Mushroom)a).poppedUp){
							return;
						}
					}
					if (a.isOnTopSide(b)) {
						a.setPosition(new Vector2D(a.getPosition().getX(), a.getPosition().getY() - a.topCollidingDistance(b)));
					} else if (a.isOnBottomSide(b)) {
						a.setPosition(new Vector2D(a.getPosition().getX(), a.getPosition().getY() + a.bottomCollidingDistance(b)));
					}

					if (a.getBoundingBox().intersects(b.getBoundingBox()) && a.isOnLeftSide(b)) {

						a.setPosition(new Vector2D(a.getPosition().getX() - a.leftCollidingDistance(b), a.getPosition().getY()));
						a.setOppositeXVelocity();

					} else if (a.getBoundingBox().intersects(b.getBoundingBox()) && a.isOnRightSide(b)) {
						a.setOppositeXVelocity();
					}

					/*
					 * Mario collision handling
					 */
				} else {
					if (a.isOnTopSide(b)) {
						((Player) a).jumped = false;
						((Player) a).playerYvel = 0;
						((Player) a).setPosition(new Vector2D(a.getPosition().getX(), a.getPosition().getY() - a.topCollidingDistance(b)));
					} else if (a.isOnBottomSide(b)) {
						((Player) a).setPosition(new Vector2D(a.getPosition().getX(), a.getPosition().getY() + a.bottomCollidingDistance(b)));
						((Player) a).playerYvel = 0;
						((Player) a).jumped = true;
					switch (b.type) {
						case 1:
							if(gamelvl == 1)
								((BreakableBrownWall) b).breakApart();
							else if(gamelvl == 2)
								((BreakableGreenWall) b).breakApart();
							
							
							break;
						case 11:
							if (!((QuestionBlock) b).dead) {
								if (powerUpInTheBlock((QuestionBlock) b)) {
									if (p.level == 0) {
										movableLayer.add(new Mushroom(b.getPosition().getX(), b.getPosition().getY()));
										((QuestionBlock) b).setDead();
										p.level++;
									} else {
										powerUpLayer.add(new PowerUpFlower(b.getPosition().getX(), b.getPosition().getY()));
										((QuestionBlock) b).setDead();
									}
								} else {
									powerUpLayer.add(new Coin(b.getPosition().getX(), b.getPosition().getY()));
									((QuestionBlock) b).setDead();
									points+=100;
								}
							}
							else{
								if(System.currentTimeMillis()-bumpPlayTime>bumpPlayDelay){
								bump.play();
								bumpPlayTime=System.currentTimeMillis();
								}
								
							}
							break;

						}
					}else{
					switch(b.type){	
					case 6:
						System.out.println("End of Level 1");
						backGroundLayer.clear();
						gamelvl = 2;
						loadGameLevel(Integer.toString(gamelvl));
						
						break;
					}
					}
					if (a.getBoundingBox().intersects(b.getBoundingBox()) && a.isOnLeftSide(b)) {
						((Player) a).setPosition(new Vector2D(a.getPosition().getX() - a.leftCollidingDistance(b), a.getPosition().getY()));
						// ((player) a).playerXvel = 0;
						// ((player) a).playerXacc = 0;
					} else if (a.getBoundingBox().intersects(b.getBoundingBox()) && a.isOnRightSide(b)) {
						((Player) a).setPosition(new Vector2D(a.getPosition().getX() + a.rightCollidingDistance(b), a.getPosition().getY()));
						// ((player) a).playerXvel = 0;
						// ((player) a).playerXacc = 0;
					}
				}
			}
		};

		physics.registerCollisionHandler(d);
		gamelvl =1;
		loadGameLevel(Integer.toString(gamelvl));
		setWorldBounds(0, 0, mapWidth * TILE_SIZE, mapHeight * TILE_SIZE);
	}

	private boolean powerUpInTheBlock(QuestionBlock b) {
		for (int i = 0; i < powerUpQuestionBlocksArray.size(); i++) {
			if (b.hashCode() == powerUpQuestionBlocksArray.get(i).hashCode()) {
				return true;
			}
		}
		return false;
	}
	
	private void loadGameLevel(String level) {
		
		currentTime = System.currentTimeMillis();
		points=0;
		world = 1;
		questionBlockCount = 0;
		world_level = Integer.valueOf(level);
		String ud = System.getProperty("user.dir");
		try {
			String line;
			FileInputStream fstream = new FileInputStream("src/resources/map" + level + ".txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			line = br.readLine();
			mapWidth = Integer.parseInt(line);
			// this.worldPixelLenght = mapWidth * TILE_SIZE;
			line = br.readLine();
			mapHeight = Integer.parseInt(line);
			worldPixelHeight = mapHeight * TILE_SIZE;
			worldPixelLenght = mapWidth * TILE_SIZE;
			leftWidthBreakPoint = HALF_SCREEN_WIDTH;
			rightWidthBreakPoint = worldPixelLenght - HALF_SCREEN_WIDTH;

			// map = new gamemap(mapWidth, mapHeight);
			int y = 0;
			while ((line = br.readLine()) != null) {

				buildMap(line, y);
				y++;

			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	/*
	 * type, map character, name
	 * 1  a breakablebrownwall
	 * 2  b unbreakablewall
	 * 3  c groundwall
	 * 4  d mario
	 * 5  e goomba
	 * 6  f flagpole
	 * 7  g turtle
	 * 8  h verticalpipe
	 * 9  i verticalhalfpipe
	 * 10 j castle
	 * 11 k questionblock
	 * 12 l smallcloud
	 * 13 m bigcloud
	 * 14 n smallcloud
	 * 15 0 bighill
	 * 16 p shrub
	 * 17 q green goomba (level 2 goomba)
	 * 18 r green turtle
	 * 19 s green step
	 * 20 w green breakable wall
	 * 21   flower
	 * 22   mushrrom
	 * 23   smallCoin
	 * 24   score100
	 */
	private void buildMap(String s, int y) throws InterruptedException {
		try {
			String line = s;
			for (int x = 0; x < mapWidth; x++) {

				char ch = line.charAt(x);
				if (ch == 'a') {
					unmovableLayer.add(new BreakableBrownWall(x, y));
				} else if (ch == 'b') {
					unmovableLayer.add(new UnbreakableWall(x, y,"#unbreakableWall"));
				} else if (ch == 'c') {
					unmovableLayer.add(new GroundWall(x, y,"#level1Ground"));
				} else if (ch == 'd') {
					p = new Player(x, y, "#mario");
					movableLayer.add(p);
				} else if (ch == 'e') {
					movableLayer.add(new Goomba(x, y,"#Goomba"));
				} else if (ch == 'f') {
					unmovableLayer.add(new FlagPole(x, y));
				} else if (ch == 'g') {
					movableLayer.add(new Turtle(x, y,"#turtle"));
				} else if (ch == 'h') {
					unmovableLayer.add(new VerticalPipe(x, y, "#verticalPipe", 8));
				} else if (ch == 'i') {
					unmovableLayer.add(new VerticalPipe(x, y,"#verticalHalfPipe", 9));
				} else if (ch == 'j') {
					unmovableLayer.add(new Castle(x, y));
				} else if (ch == 'k') {
					QuestionBlock qb = new QuestionBlock(x, y);
					switch (powerUpBlockType(questionBlockCount)) {
					case (1):
						powerUpQuestionBlocksArray.add(qb);
					}
					unmovableLayer.add(qb);
					questionBlockCount++;
				} else if (ch == 'l') {
					backGroundLayer.add(new SmallCloud(x,y));
				} else if (ch == 'm') {
					backGroundLayer.add(new BigCloud(x,y));
				} else if (ch == 'n') {
					backGroundLayer.add(new SmallHill(x,y));
				} else if (ch == 'o') {
					backGroundLayer.add(new BigHill(x,y));
				} else if (ch == 'p') {
					backGroundLayer.add(new Shrub(x,y));
				} else if (ch == 'q') {
					movableLayer.add(new Goomba(x, y,"#level2Goomba"));
				} else if (ch == 'r') {
					movableLayer.add(new Turtle(x, y,"#level2turtle"));
				} else if (ch == 's') {
					unmovableLayer.add(new UnbreakableWall(x, y,"#greenUnbreakableWall"));
				} else if (ch == 't') {
					unmovableLayer.add(new GroundWall(x, y,"#level2Ground"));
				} else if (ch == 'u') {

				} else if (ch == 'v') {

				} else if (ch == 'w') {
					unmovableLayer.add(new BreakableGreenWall(x,y));
				} else if (ch == 'x') {

				} else if (ch == 'y') {

				} else if (ch == 'z') {

				}

			}
		} catch (Exception e) {
			return;
		}
	}
	
	/*
	 * level 1: block# 3 and 7 are power ups , #9 is invinciblility
	 * 
	 * return type: 1=power up, 2=invinciblilty
	 */
	private int powerUpBlockType(int count) {
		if (world_level == 1) {
			switch (count) {
			case (3):
				return 1;
			case (7):
				return 1;
			case (9):
				return 2;
			}
		}
		return 0;
	}

	public void render(RenderingContext rc) {
		super.render(rc);
		scoreboardFont.render("MARIO x" + p.live, rc, AffineTransform.getTranslateInstance(40, 20));
		scoreboardFont.render("WORLD", rc, AffineTransform.getTranslateInstance(300, 20));
		scoreboardFont.render("TIME", rc, AffineTransform.getTranslateInstance(430, 20));
		scoreboardFont.render(points + "", rc, AffineTransform.getTranslateInstance(50, 40));
		scoreboardFont.render(world + "" + "-" + world_level, rc, AffineTransform.getTranslateInstance(310, 40));
		scoreboardFont.render((int) p.playerTimer + "", rc, AffineTransform.getTranslateInstance(440, 40));
		
		if(p.live <= 0 || p.playerTimer <=0){
			finalScoreFont.render("Final Score:"+points,rc, AffineTransform.getTranslateInstance(180, 180));
			gameOverFont.render("Game Over",rc, AffineTransform.getTranslateInstance(180, 240));
			gameover=true;
			gameObjectLayers.clear();
		}
	}
	
	private void resetLevel(String level){
		unmovableLayer.clear();
		movableLayer.clear();
		backGroundLayer.clear();
		powerUpLayer.clear();
		powerUpQuestionBlocksArray.clear();
	    loadGameLevel(level);
	    restartLevel=false;
	}

	@Override
	public void update(long deltaMs) {
		/*
		 * time-=((System.currentTimeMillis()-currentTime)/1000.0);
		 * currentTime=System.currentTimeMillis();
		 */
		 
		 if(restartLevel){
			resetLevel(world_level+"");
		}
		
		currentCenter=p.getPosition().getX();
		
		/**
		 * Jenis: Timer update for time-based game
		 */
		if(!gameover){
			 stopWatch++;
			 
			 if(stopWatch >= 80){
				 p.playerTimer--;
				 stopWatch=0;
			 }
		}
		
		jumpTimer += deltaMs;
		
		if (p.getPosition().getX() < leftWidthBreakPoint) {
			centerOnPoint(leftWidthBreakPoint, HALF_SCREEN_HEIGHT);
		} else if (p.getPosition().getX() > rightWidthBreakPoint) {
			centerOnPoint(rightWidthBreakPoint, HALF_SCREEN_HEIGHT);
		} else {
			centerOnPoint((int) p.getPosition().getX(), HALF_SCREEN_HEIGHT);
		}

		keyboard.poll();
		boolean left = keyboard.isPressed(KeyEvent.VK_LEFT);
		boolean right = keyboard.isPressed(KeyEvent.VK_RIGHT);
		boolean up = keyboard.isPressed(KeyEvent.VK_UP);
		boolean down = keyboard.isPressed(KeyEvent.VK_DOWN);
		boolean space = keyboard.isPressed(KeyEvent.VK_SPACE);
		boolean r = keyboard.isPressed(KeyEvent.VK_R);
		boolean run = keyboard.isPressed(KeyEvent.VK_SHIFT);
/*
		if(p.getPosition().getY() >=353){
			p.live--;
			p.marioDie();
			p.setActivation(false);
				
			if (p.live >= 0) {
				p = new Player(p.startingPositionX, p.startingPositionY);
				movableLayer.add(p);
			} else {
				restartLevel = true;
				return;
			}
		
		}
	*/	
		
		if((left || right) && !p.jumped) {
			p.playerYacc = p.gravity;
		}
		if (left && !right) {
			if(p.MARIO) {
				if(!run){
					p.playerXacc = -Physics.mg_acceler_walk;
					p.maxXvel = -Physics.mg_max_vel_walk;
				} else {
					p.playerXacc = -Physics.mg_acceler_runn;
					p.maxXvel = -Physics.mg_max_vel_runn;
				}
			} else {
				if(!run){
					p.playerXacc = -Physics.lg_acceler_walk;
					p.maxXvel = -Physics.lg_max_vel_walk;
				} else {
					p.playerXacc = -Physics.lg_acceler_runn;
					p.maxXvel = -Physics.lg_max_vel_runn;
				}
			}
		} else if (right && !left) {
			if(p.MARIO) {
				if(!run){
					p.playerXacc = Physics.mg_acceler_walk;
					p.maxXvel = Physics.mg_max_vel_walk;
				} else {
					p.playerXacc = Physics.mg_acceler_runn;
					p.maxXvel = Physics.mg_max_vel_runn;
				}
			} else {
				if(!run){
					p.playerXacc = Physics.lg_acceler_walk;
					p.maxXvel = Physics.lg_max_vel_walk;
				} else {
					p.playerXacc = Physics.lg_acceler_runn;
					p.maxXvel = Physics.lg_max_vel_runn;
				}
			}
		} else {
			if(p.playerXvel < 0) {
				if(p.MARIO) {
					p.playerXacc = Physics.mg_deceler_rele;
				} else {
					p.playerXacc = Physics.lg_deceler_rele;
				}
			} else if(p.playerXvel > 0) {
				if(p.MARIO) {
					p.playerXacc = -Physics.lg_deceler_rele;
				} else {
					p.playerXacc = -Physics.lg_deceler_rele;
				}
			}
		}

		if (space) {
			if(jumpTimer > Physics.keyPoll){
				if(p.MARIO){
					if(Math.abs(p.playerXvel) < Physics.lt_jump) {
						if(p.playerYvel == 0) p.playerYvel = -Physics.mj_lt_init_vel;
						if(!p.jumped) p.playerYacc = Physics.mj_lt_fall_gra;
						if(p.jumped && p.playerYvel < 0) p.playerYacc = Physics.mj_lt_hold_gra;
						p.jumped = true;
						p.gravity = Physics.mj_lt_fall_gra;
					} else if(Math.abs(p.playerXvel) < Physics.gt_jump) {
						if(p.playerYvel == 0) p.playerYvel = -Physics.mj_bt_init_vel;
						if(!p.jumped) p.playerYacc = Physics.mj_bt_fall_gra;
						if(p.jumped && p.playerYvel < 0) p.playerYacc = Physics.mj_bt_hold_gra;
						p.jumped = true;
						p.gravity = Physics.mj_bt_fall_gra;
					} else {
						if(p.playerYvel == 0) p.playerYvel = -Physics.mj_gt_init_vel;
						if(!p.jumped) p.playerYacc = Physics.mj_gt_fall_gra;
						if(p.jumped && p.playerYvel < 0) p.playerYacc = Physics.mj_gt_hold_gra;
						p.jumped = true;
						p.gravity = Physics.mj_gt_fall_gra;
					}
				} else {
					if(Math.abs(p.playerXvel) < Physics.lt_jump) {
						if(p.playerYvel == 0) p.playerYvel = -Physics.lj_lt_init_vel;
						if(!p.jumped) p.playerYacc = Physics.lj_lt_fall_gra;
						if(p.jumped && p.playerYvel < 0) p.playerYacc = Physics.lj_lt_hold_gra;
						p.jumped = true;
						p.gravity = Physics.lj_lt_fall_gra;
					} else if(Math.abs(p.playerXvel) < Physics.gt_jump) {
						if(p.playerYvel == 0) p.playerYvel = -Physics.lj_bt_init_vel;
						if(!p.jumped) p.playerYacc = Physics.lj_bt_fall_gra;
						if(p.jumped && p.playerYvel < 0) p.playerYacc = Physics.lj_bt_hold_gra;
						p.jumped = true;
						p.gravity = Physics.lj_bt_fall_gra;
					} else {
						if(p.playerYvel == 0) p.playerYvel = -Physics.lj_gt_init_vel;
						if(!p.jumped) p.playerYacc = Physics.lj_gt_fall_gra;
						if(p.jumped && p.playerYvel < 0) p.playerYacc = Physics.lj_gt_hold_gra;
						p.jumped = true;
						p.gravity = Physics.lj_gt_fall_gra;
					}
				}
				jumpTimer = 0;
			}
		} else if(p.jumped == true) {
			p.playerYacc = p.gravity;
		}
		
		/* collision between mario and interactable objects */
		for(int i=0; i<movableLayer.size();i++){
			if(movableLayer.get(i).isActive() && movableLayer.get(i).type!=4 && movableLayer.get(i).getBoundingBox().intersects(p.getBoundingBox())){
				//System.out.println("colliding");
				double playerFoot = p.getPosition().getY()+ p.getHeight();
				double enemyHead = movableLayer.get(i).getPosition().getY();
				switch(movableLayer.get(i).type){
				
				/**
				 * collision between mario and Goomba
				 */
				case 5:
					
					if(((Goomba)movableLayer.get(i)).dead){
						continue;
					}
					if(playerFoot > enemyHead && playerFoot < enemyHead+10){
						((Goomba)movableLayer.get(i)).setDead();
						backGroundLayer.add(new Score100(((Goomba)movableLayer.get(i)).getPosition().getX(),((Goomba)movableLayer.get(i)).getPosition().getY()));
						points+=100;
						p.playerYvel = -Physics.enemy_stomp_vel;
						p.jumped=true;
					}
					else{
						music.pause();
						p.restartPosition();
						p.playerTimer = 100;
					}
				break;
				
				/**
				 * Collision between mario and turtle
				 */
				case 7:
					
					if(((Turtle)movableLayer.get(i)).dead){
						continue;
					}
					if(playerFoot > enemyHead && playerFoot < enemyHead+10){						
						((Turtle)movableLayer.get(i)).setDead();
						backGroundLayer.add(new Score100(((Turtle)movableLayer.get(i)).getPosition().getX(),((Turtle)movableLayer.get(i)).getPosition().getY()));
						points+=100;
						p.playerYvel = -Physics.enemy_stomp_vel;
						p.jumped=true;
					}
					else{
						music.pause();
						p.restartPosition();
						p.playerTimer = 100;
					}
					break;
				case 22:
					if(((Mushroom)movableLayer.get(i)).poppedUp){
					((Mushroom)movableLayer.get(i)).setActivation(false);
					}
					p.levelUp();
					break;
				}
			}
		}
		
		
		/* collision between mario and interactable objects */
		for(int i=0; i<powerUpLayer.size();i++){
			if(powerUpLayer.get(i).isActive() && powerUpLayer.get(i).type!=4 && powerUpLayer.get(i).getBoundingBox().intersects(p.getBoundingBox())){
				switch(powerUpLayer.get(i).type){
				case 17:
					((PowerUpFlower)powerUpLayer.get(i)).setDead();
					break;
				}
			}
		}

		physics.applyLawsOfPhysics(deltaMs);

	}

	public static void main(String[] args) {

		Smb p = new Smb();
		p.run();

	}

}