package gamescreens;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import smb.Smb;

import jig.engine.FontResource;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;


public class SplashScreen {
	Smb smb;
    public FontResource font;
    public BodyLayer<Body> backgroundLayer;
    
   public SplashScreen(Smb smb)
   {
       this.smb = smb;
       
       backgroundLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
    // add background images here.
       this.smb.getGameObjectLayers().add(backgroundLayer);
       
       font = ResourceFactory.getFactory().getFontResource(new Font("Sans Serif", Font.PLAIN, 20), Color.white, null);
   }
   
   public void update(long deltaMs)
   {
       boolean begin = smb.getKeyboard().isPressed(KeyEvent.VK_ENTER);
       //put somecode to start level 1 of the game.
   }
   
   public void render(RenderingContext rc)
   {
       font.render("Super Mario!", rc, AffineTransform.getTranslateInstance(32, 50));
       font.render("By:  Jenis Modi, Justin Shelton, Xin Tang", rc, AffineTransform.getTranslateInstance(32, 100));
       font.render("Press Enter To Begin", rc, AffineTransform.getTranslateInstance(32, 150));
       
       font.render("Keys:", rc, AffineTransform.getTranslateInstance(320, 50));
       font.render("Move:  Arrow Keys", rc, AffineTransform.getTranslateInstance(320, 100));
       
       font.render("Pause:  Escape Key", rc, AffineTransform.getTranslateInstance(320, 300));
       font.render("Unpause:  Enter Key", rc, AffineTransform.getTranslateInstance(320, 350));
   }
}
