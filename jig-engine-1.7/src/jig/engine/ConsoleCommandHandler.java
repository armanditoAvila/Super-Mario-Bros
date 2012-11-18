package jig.engine;

/**
 * An interface to manage commands from the GameFrame Console.
 * 
 * @author Scott Wallace
 * @see GameFrame
 * @see Console
 */
public interface ConsoleCommandHandler {

	/**
	 * Attempts to handle a specified command given a set of arguments.
	 * For any given call, this method may or may not be able to 
	 * appropriately handle to command. It should be robust in the face
	 * of bad arguments and even unexpected command names. The method
	 * should return <code>true</code> if the command is successfully 
	 * handled, and <code>false</code> otherwise.
	 * 
	 * @param cmd the name of the issued command 
	 * @param rest the string that follows the command name (unparsed arguments)
	 * @return <code>true</code> iff the command was handled
	 */
	boolean handle(String cmd, String rest);
}
