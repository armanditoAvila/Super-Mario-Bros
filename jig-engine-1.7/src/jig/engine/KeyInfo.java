package jig.engine;

// TODO: Documentation
public class KeyInfo {
	public static enum State { PRESSED, RELEASED };
	public static final char NO_CHAR = 0;
	public static final int NO_CODE = 0;
	
	private State state;

	private char character;
	private int code;
	
	// TODO: char c is redundant with code, and will not easily work with NoneResourceFactory
	public KeyInfo(char c, int code, State s) {
		character = c;
		this.code = code;
		state = s;
	}
	
	public KeyInfo(char c, State s) {
		this(c, NO_CODE, s);
	}
	
	public KeyInfo(int code, State s) {
		this(NO_CHAR, code, s);
	}
	
	public char getChar() {
		return character;
	}
	
	public int getCode() {
		return code;
	}
	
	public boolean wasPressed() {
		return (state == State.PRESSED);
	}
	
	public boolean wasReleased() {
		return (state == State.RELEASED);
	}
}
