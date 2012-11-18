package jig.engine;


/**
 * The GameFrame is the window within which the game is drawn.
 * 
 * The implementation details of particular GameFrame classes are defined by the
 * graphics backend.
 * 
 * @author Scott Wallace
 * 
 */
public interface GameFrame {

    
    /**
     *
     * Gets a static reference to a pollable keyboard.
     *
  	 * DESIGN not static at the moment.
     *
     * @return a reference to the pollable keyboard
     */
    Keyboard getKeyboard();
    
    /**
     * Gets a static reference to a pollable mouse.
     *
     * @return a reference to the pollable mouse
     */
    Mouse getMouse();
    
    /**
     * Get a RenderingContext to perform a series of drawing operations to the
     * offscreen buffer. The same RenderingContext instance should be used for
     * each draw operation that occurs between two calls to GameFrame.render()
     *
     * @return a rendering context instance for drawing to the offscreen buffer
     */
    RenderingContext getRenderingContext();
    
    /**
     * Clears the back buffer so it can be redrawn from scratch.
     *
     */
    void clearBackBuffer();
    
    /**
     * @return a reference to the command console.
     */
    Console getConsole();
    
    /**
     * Renders the GameFrame by blitting (or whatnot) the offscreen buffer.
     */
    void displayBackBuffer();
    
    /**
     * Calls the exit handler (if a exit hander exists).
     * If there is no exit handler, or it returns true then the game frame
     * remembers that the game is ready to exit and any subsequent calls
     * to isExitAndCloseRequested() should return true.
     * 
     * @param forcedExit
     * @return
     */
    boolean requestExitAndClose(boolean forcedExit);
    
    /**
     * Should return true iff the game is ready to exit after this game loop.
     * @return
     */
    boolean isExitAndCloseRequested();
    
    /**
     * Closes the game frame and restores the screen's resolution
     * to its state prior to launching the game.
     * 
     * Note: this method should only be called outside of the game loop.
     * It is typically only used by abstract game.
     */
    void closeAndExit();
    
    /**
     * Registers an exit handler to handle when the user or system closes the game.
     * 
     * @see ExitHandler
     */
    void registerExitHandler(ExitHandler exitHandler);
    
    /**
     * Shows or hides the window.
     * @param visible <code>true</code> iff the frame should be made visible.
     */
    void setVisible(boolean visible);
    
    /**
     * Sets the Frame's title.
     * @param title the new title
     */
	void setTitle(final String title);

	/**
	 * Attempts to toggle full-screen mode
	 * @return if the attempt was successful.
	 */
	public boolean toggleFullscreenMode();

	public int getWidth();
	public int getHeight();
	public boolean getFullScreen();

    interface ExitHandler {

    	/**
    	 * If this method is called the game is about to exit.
    	 * You should use this method to do any saving or other
    	 * things that need to be done.
    	 * 
    	 */
    	void handleForcedExit();
    	
    	/**
    	 * If this method is called the user has requested to exit
    	 * the game. 
    	 * 
    	 * You can use this method to create a prompt to ensure the user truly wants
    	 * to exit. In this case, you would set a flag to prompt the user, and then
    	 * return false. If the user decided to proceed with the exit, you would call
    	 * requestExitAndClose(true) to force the exit of the game.
    	 * 
    	 * @return true if the game has decided to exit
    	 */
    	boolean handleRequestedExit();
    }

}


