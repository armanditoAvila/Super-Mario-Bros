package jig.engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import jig.engine.internal.TerseLogFormatter;

/**
 * A device that can be used to display debugging or status information and
 * interact with the game's state in a limited fashion.
 * 
 * The Game Frame supports a Console device with this interface that can be
 * displayed on demand during play.
 * 
 * Handles the following commands by default:
 * <ul>
 * <li> <code>quit</code> quits the game </li>
 * <li> <code>log --[flag]</code> where <code>[flag]</code> is either a
 * level name (e.g., <code>warning</code>, <code>info</code>)
 * <code>file</code> (to redirect log information to a file) or
 * <code>console</code> (to redirect log information to stderr). If either
 * 'console' or 'file' are specified, all currently registered log handlers are
 * removed before the new console or file handler is added.</li>
 * <li> <codehelp</code> displays help info on all the commands </li>
 * </ul>
 * 
 * @see jig.engine.GameFrame
 * 
 * @author Scott Wallace
 * 
 * 
 */
public abstract class Console {

	/** <code>true</code> if and only if the console is visible. */
	protected boolean visible;

	/** The number of columns to display...currently 1. */
	protected int columns;
	
	/** A buffer to store lines of text that are displayed in the console. */
	private LinkedList<String> strings;

	/** A buffer to store the text being entered at the prompt. */
	private StringBuffer inputBuffer;

	/** The maximum number of buffered lines. */
	private int maxLines;

	/** The prompt text. */
	private String prompt;

	/** A map of commands to handlers to act on text entered at the prompt. */
	private HashMap<String, Command> commands;

	/** A number to keep track of if the console has changed and thus needs to be
	 * re-rendered. */
	private int version;
	
	/** The frame associated with this console */
	private GameFrame frame;
	
	/**
	 * This constructor is intended to be called by a sub class.
	 */
	protected Console(GameFrame frame) {
		strings = new LinkedList<String>();
		inputBuffer = new StringBuffer(80);
		maxLines = 50;
		prompt = ">>";
		
		this.frame = frame;

		inputBuffer.append(prompt);
		commands = new HashMap<String, Command>(10);

		visible = false;
		columns = 1;
		addPredefinedCommandHandlers();
	}
	

	/**
	 * Appends a string to the interactive prompt with a new line added to the end.
	 * 
	 * @param s
	 *            the string to append to the prompt.
	 *            
	 */
	public void appendLineToPrompt(final String s)
	{
		for(char c : s.toCharArray())
		{
			appendToPrompt(c);
		}
		appendToPrompt('\n');
	}

	/**
	 * Appends a character to the interactive prompt. The special characters
	 * '\n' and '\b' are supported.
	 * 
	 * @param c
	 *            the character to append to the prompt.
	 *            
	 */
	public void appendToPrompt(final char c) {
		if (c == '\b') {
			if (inputBuffer.length() > prompt.length()) {
				inputBuffer.setLength(inputBuffer.length() - 1);
				version++;
			}
		// TODO SW: LWJGL keyboard returns '\n' for enter key on Mac but '\r' on Windows
		// This is a workaround
		} else if (c == '\n' || c == '\r') {
			String line = inputBuffer.toString();
			int eoc, boc = prompt.length(); //end of command, beginning of command

			appendLine(line);
			inputBuffer.setLength(prompt.length());

			while (line.length() > boc && line.charAt(boc) == ' ') {
				boc++;
			}
			if (line.length() > boc) {
				eoc = line.indexOf(' ', boc);
				if (eoc == -1) {
					eoc = line.length();
				}
				String cmdName = line.substring(boc, eoc);
				Logger l = ResourceFactory.getJIGLogger();
				l.info("Processing console command '" + cmdName + "'");
				Command cmd = commands.get(cmdName);
				if (cmd != null) {
					l.fine("  -Calling handler");
					cmd.handler.handle(cmdName, line.substring(eoc));
				} else {
					l.info("  -No handler");
				}
			} 
			version++;
		} else if (c != '\t' && Character.isDefined(c) && isPrintableASCII(c)) {
			inputBuffer.append(c);
			version++;
		}
	}


	/**
	 * Appends a line of text to the buffer.
	 * @param s a line of text (usually command output) to display 
	 * in the buffer.
	 */
	private void appendLine(final String s) {
		strings.add(s);
		while (strings.size() > maxLines) {
			strings.remove(0);
		}

		version++;
	}
	
	/**
	 * 
	 * @return an iterator over all lines in the buffer.
	 */
	protected Iterator<String> lines() {
		return strings.iterator();
	}

	/**
	 * 
	 * @return the number of lines in the buffer.
	 */
	protected int size() {
		return strings.size();
	}

	/**
	 * 
	 * @return the working line of text (i.e., the line of text with
	 * the input prompt).
	 */
	protected String workingLine() {
		return inputBuffer.toString();
	}
	
	/**
	 * Sets the buffer to store a specified number of lines.
	 * @param sz the maximum number of lines to store
	 */
	protected void setBufferSize(final int sz) {
		maxLines = sz;

		while (strings.size() > maxLines) {
			strings.remove(0);
		}
	}


	/**
	 * Determines if a character is a "printable" ascii character.
	 * For instance, escape has an ascii code of 27, Character.isDefined
	 * would return true, but we don't want to add this to the console buffer.
	 * 
	 * @param c the input ASCII character
	 * @return true iff the character is a printable ASCII character (in the
	 * range [32, 126])
	 */
	private boolean isPrintableASCII(char c) {
		if (c >= 32 && c <= 126) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return the 'version' of the text buffer. This value changes whenever the
	 *         text in the console changes. By tracking this value, one can
	 *         determine when the console's text needs to be redrawn.
	 */
	protected int getTextVersion() {
		return version;
	}





	
	/**
	 * Shows or hides the console, based on its current status.
	 * 
	 * @return the new status (true iff the console is now visible)
	 */
	public boolean toggleDisplay() {
		visible = !visible;
		return visible;
	}

	/**
	 * Explicitly shows or hides the console.
	 * 
	 * @param v
	 *            true iff the console should be made visible
	 */
	public void setVisible(final boolean v) {
		visible = v;
	}

	/**
	 * Adds the ability to handle a specific named function. The console treats
	 * lines of text as commands, the first word is the command name and all
	 * other words are treated as arguments. When a command is entered,
	 * registered handlers are examined to look for any matches against the
	 * command name. If one is found it is given the command string to process.
	 * 
	 * @param cmdname
	 *            the name of the commands that should be dispatched to the new
	 *            handler
	 * @param h
	 *            a reference to the new command handler instance
	 * @param help
	 * 			  a brief description of the commands functionality
	 * 
	 * @see jig.engine.ConsoleCommandHandler
	 */
	/**
	 * Adds a command handler.
	 * @param cmdname the command name to be handled
	 * @param handler the object which will handle the command
	 * @param help a short description of the command and its options
	 */
	public void addCommandHandler(final String cmdname, 
			final ConsoleCommandHandler handler, final String help) {
		
		commands.put(cmdname, new Command(handler, help));
	}
	
	/**
	 * Adds predefined command handlers.
	 * 
	 * Called from the constructor -- should remain private or final.
	 */
	private void addPredefinedCommandHandlers() {
		ConsoleCommandHandler defaultCommandHandler = new ConsoleCommandHandler() {
			/**
			 * Handles registered default commands.
			 *  
			 * @param cmd
			 *            the command name
			 * @param rest
			 *            the string containing all other arguments to the command
			 * @return <code>true</code> iff the command was handled
			 */
			public boolean handle(final String cmd, final String rest) {

				if (cmd.equals("help")) {
					appendLine("commands: ");
					for (Entry<String, Command> e : commands.entrySet()) {
						appendLine(e.getKey() + " -- " + e.getValue().help);
					}
					return true;
				} else if (cmd.equals("jigversion")) {
					appendLine(Version.JIG_VERSION.toString());
				} else if (cmd.equals("log")) {
					return handleLogConsoleCommand(rest);
				} else if (cmd.equals("quit")) {
					frame.requestExitAndClose(false);
				} 
				return false;
			}
		};
		
		addCommandHandler("help", defaultCommandHandler, "prints help about each command");	
		addCommandHandler("jigversion", defaultCommandHandler, "prints out the current jig version number");	
		addCommandHandler("log", defaultCommandHandler, "changes logging verbosity/destination");
		addCommandHandler("quit", defaultCommandHandler, "exits the game");
	}

	/**
	 * Removes user command handlers, reverting the set
	 * of known handlers to the same state as when this
	 * ConsoleBuffer was initialized.
	 */
	public void removeUserCommandHandlers() {
		commands.clear();
		addPredefinedCommandHandlers();
	}



	
	/**
	 * Handles the 'log' console command.
	 * 
	 * @param rest
	 *            the remaining arguments to the log command
	 * @return <code>true</code> if command was handled
	 */
	private boolean handleLogConsoleCommand(final String rest) {
		String[] args = rest.trim().replaceAll("\\s+", " ").split(" ");

		if (args.length == 1) {
			appendLine("Usage: log --[<level>|file|console|status]");
			return false;
		}
		
		if (!args[0].startsWith("--")) {
			appendLine("Usage: log --[<level>|file|console|status]");
			return false;
		}
		String flag = args[0].substring(2);
		if (!flag.equals("")) {
			Level level;
			try {
				level = Level.parse(flag);
			} catch (IllegalArgumentException e) {
				try {
					level = Level.parse(flag.toUpperCase());
				} catch (IllegalArgumentException e2) {
					level = null;
				}
			}
			if (level != null) {
				ResourceFactory.getJIGLogger().setLevel(level);
				appendLine(level.getName());
				return true;

			} else if (flag.startsWith("s")) {
				Logger jiglog = ResourceFactory.getJIGLogger();
				appendLine("Logging level: " 
						+ jiglog.getLevel().getName());
				return true;

			} else if (flag.startsWith("c")) {
				Logger jiglog = ResourceFactory.getJIGLogger();
				for (Handler h : jiglog.getHandlers()) {
					jiglog.removeHandler(h);
				}
				ConsoleHandler h = new ConsoleHandler();
				h.setFormatter(new TerseLogFormatter());
				jiglog.addHandler(h);
				appendLine("Log redirected to stderr");
				return true;

			} else if (flag.startsWith("f")) {
				Logger jiglog = ResourceFactory.getJIGLogger();
				for (Handler h : jiglog.getHandlers()) {
					jiglog.removeHandler(h);
				}
				try {
					jiglog.addHandler(new FileHandler("jig.engine-log.xml"));
					appendLine("Log redirected to jig.engine-log.xml");
					return true;
				} catch (IOException e) {
					appendLine("Couldn't open jig.engine-log.xml");
				}
			}
		}
		return false;

	}
	
	/**
	 * Renders the console to the GameFrame's back buffer.
	 * 
	 */
	public abstract void renderToBackBuffer();
	
	/**
	 * A container for command entries in the hash map.
	 *
	 * @author Scott Wallace
	 *
	 */
	static class Command {
		ConsoleCommandHandler handler;
		String help;
		
		/**
		 * Creates a new command container.
		 * @param handler the object that will handle these commands
		 * @param help a brief description of the command's function
		 */
		Command(final ConsoleCommandHandler handler, final String help) {
			this.handler = handler;
			this.help = help;
		}
	}
	

}
