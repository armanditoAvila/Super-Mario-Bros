package jig.engine.internal;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A terse, single line, format for <code>LogRecord</code>s.
 * This is the default handler used for in the JIG Engine.
 * 
 * 
 * @author Scott Wallace
 *
 */
public class TerseLogFormatter extends Formatter {

	/**
	 * Formats a log record.
	 * @param r the log record to format.
	 * @return a string suitable for printing, etc.
	 */
	@Override
	public synchronized String format(final LogRecord r) {
		StringBuffer sb = new StringBuffer();

		String message = formatMessage(r);
		sb.append(r.getLevel().getLocalizedName());
		sb.append(": ");
		sb.append(message);
		sb.append('\n');
		return sb.toString();

	}

}
