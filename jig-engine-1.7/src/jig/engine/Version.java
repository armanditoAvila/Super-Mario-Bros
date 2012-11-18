package jig.engine;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;


/**
 * Encapsulates a version number.
 * 
 * 
 *  TODO: Should be used & internalized or go away.
 *  
 * @author Scott Wallace
 * 
 */
public class Version {
	
	
	private static Properties jigprop;
	
	static {
		// Loads jig properties from the jig.properties file
		
		URL u = ResourceFactory.findResource("jig/resources/jig.properties");
		jigprop = new Properties();
		InputStream i = null;
		try {
			i = u.openStream();
			jigprop.load(i);
			i.close();
			
			ResourceFactory.jigLog.info("JIG Version: " + jigprop.get("jig.version"));
		} catch (IOException e) {
			ResourceFactory.jigLog.warning("Couldn't load jig properties...your jar file may be corrupted");
		}
		
		if (Package.getPackage("jig.engine").getImplementationVersion() != null) {
			jigprop.setProperty("jig.version", Package.getPackage("jig.engine").getImplementationVersion());
			
		}
		
		i = null;
	}
	
	public static final Version JIG_VERSION = Version.parse(jigprop.getProperty("jig.version"));
	
	private int major;

	private int minor;

	private int patch;

	private String subpatchNotes;

	static final int ANY = -1;

	/**
	 * Creates a new Version object with the normally number
	 * of fields.
	 * 
	 * @param major
	 *            The major number (the first number in 1.6.0_01-b6)
	 * @param minor
	 *            The minor number (the second number in 1.6.0)
	 * @param patch
	 *            The patch number (the third number in 1.6.0)
	 * 
	 */
	public Version(final int major, final int minor, final int patch) {
		this(major, minor, patch, "");
	}
	/**
	 * Creates a new Version object to encapsulate versioning information.
	 * 
	 * @param major
	 *            The major number (the first number in 1.6.0_01-b6)
	 * @param minor
	 *            The minor number (the second number in 1.6.0)
	 * @param patch
	 *            The patch number (the third number in 1.6.0)
	 * @param subpatchNotes
	 *            The 'other stuff' (the '_01' suffix in 1.6.0_01)
	 */
	public Version(final int major, final int minor, final int patch,
			final String subpatchNotes) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.subpatchNotes = subpatchNotes;
		
		
	}

	/**
	 * Returns the version of this Java VM by parsing the string value stored in
	 * the System property "java.runtime.version".
	 * 
	 * @return a Version object representing this Java VM.
	 */
	public static Version getJavaVersion() {
		return parse(System.getProperty("java.runtime.version"));
	}

	/**
	 * Parses a version string such as the one contained in the System propertiy
	 * "java.runtime.version".
	 * 
	 * @param versionString
	 *            a string representation of a version number (e.g.,
	 *            "1.6.0_01-b06")
	 * @return a Version object representing the verison number
	 */
	public static Version parse(final String versionString) {
		String[] words = versionString.split("\\s");
		String[] parts;
		int mj;
		int mn;
		int patch = 0;
		String subpatch = null;
		
		parts = words[0].split("\\.");

		mj = Integer.parseInt(parts[0]);
		mn = Integer.parseInt(parts[1]);
		
		if (words.length > 1) {
			StringBuffer sp = new StringBuffer(20);
			sp.append(words[1]);
			for(int i = 2; i < words.length; i++) {
				sp.append('-');
				sp.append(words[i]);
			}
			subpatch = sp.toString();
		}

		
		if (parts.length > 2) {
			int i, n;
			String newPatch = parts[2]; // .replace('_', '0');
			
			for (i = 0, n = parts[2].length(); i < n; i++) {
				if (!Character.isDigit(newPatch.charAt(i))) {
					break;
				}
			}
			//patch = up to first non digit
			patch = Integer.parseInt(newPatch.substring(0, i));
			if (i < n) {				
				//subpatch = everything after first non digit
				if (subpatch == null) {
					subpatch = parts[2].substring(i, n);
				} else {
					subpatch = parts[2].substring(i, n) + "-" + subpatch;
				}
			}
		}
		return new Version(mj, mn, patch, subpatch);
	}

	/**
	 * Returns <code>true</code> if the current java version is more recent
	 * than a specified release.
	 * 
	 * @param mjr
	 *            the major version number
	 * @param minr
	 *            the minor version number
	 * @return <code>true</code> if the current java version is greater than
	 *         the specified version.
	 */
	public boolean atLeast(final int mjr, final int minr) {
		if (major > mjr) {
			return true;
		}
		if (major < mjr) {
			return false;
		}
		if (minor >= minr) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns <code>true</code> if the current java version is more recent
	 * than a specified release.
	 * 
	 * @param mjr
	 *            the major version number
	 * @param minr
	 *            the minor version number
	 * @param ptch
	 *            the patch version number
	 * @return <code>true</code> if the current java version is greater than
	 *         the specified version.
	 */
	public boolean atLeast(final int mjr, final int minr, final int ptch) {
		if (major > mjr) {
			return true;
		}
		if (major < mjr) {
			return false;
		}
		if (minor > minr) {
			return true;
		}
		if (minor < minr) {
			return false;
		}
		if (patch >= ptch) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return a string representation of the version.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(30);
		sb.append(major);
		sb.append('.');
		sb.append(minor);
		sb.append('.');
		sb.append(patch);
		if (subpatchNotes != null) {
			sb.append('-');
			sb.append(subpatchNotes);
		}
		return sb.toString();
	}
	
	/**
	 * Returns <code>true</code> if the version's major and minor numbers
	 * match the specified values.
	 * 
	 * @param mjr
	 *            the major version number
	 * @param minr
	 *            the minor version number
	 * @return <code>true</code> if and only if the specified values match the
	 *         current java version.
	 */
	public boolean is(final int mjr, final int minr) {
		return (major == mjr && minor == minr);
	}
	
	/**
	 * Returns <code>true</code> if the version's major and minor numbers
	 * and patch match the specified values.
	 * 
	 * @param mjr
	 *            the major version number
	 * @param minr
	 *            the minor version number
	 * @param patch
	 * 			  the ptch version number
	 * @return <code>true</code> if and only if the specified values match the
	 *         current java version.
	 */
	public boolean is(final int mjr, final int minr, int ptch) {
		return (major == mjr && minor == minr && patch == ptch);
	}
	
	/**
	 * Checks if two version numbers are equal.
	 * @param o the other object ot check for equality
	 * @return <code>true</code> iff the version objects are equal
	 */
	@Override
	public boolean equals(final Object o) {
		if (o instanceof Version) {
			Version v = (Version) o;
			
			if (v.major == major && v.minor == minor 
					&& v.patch == patch 
					&& v.subpatchNotes.equals(subpatchNotes)) { 
				
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return a hash code for this Version
	 */
	public int hashCode() {
		return major * 7 + minor * 5 + patch * 3; 
	}
	public int getMajor() {
		return major;
	}
	public int getMinor() {
		return minor;
	}
	public int getPatch() {
		return patch;
	}
	public String getSubpatchNotes() {
		return subpatchNotes;
	}


	
}
