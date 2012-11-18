package jig.engine;

/**
 * 
 * The GameClock provides a single point of access for information about
 * how time progresses during a game.
 * 
 * The GameClock has three main advantages over a standard system timers such as
 * System.nanoTime():
 * 
 *  1) The GameClock does not continuously increment time, rather time is incremented
 *     in quanta using the tick() method. This means that two observers of the clock
 *     will see the same time so long as tick() has not been called in between the 
 *     observations.  This is very desirable when many game objects must be updated 
 *     during the inner loop of a running game. Typically, it is important that ALL 
 *     objects see the same passage of time on that particular iteration so that, 
 *     for example, two objects with the same velocity move the same distance; this is 
 *     exactly the functionality provided by this clock.
 *     
 *  2) The GameClock distinguishes <i>wall time</i> from <i>game time</i>. While both
 *     times increase monotonically, the game time can be paused and resumed arbitrarily
 *     by calling the built in methods.  This makes it simple to add a pause and resume
 *     feature to a new game. Alarms are set based on game time, so an event that is planned
 *     to occur 2 seconds in the future will be delayed appropriately if the game is 
 *     paused in the meantime.
 * 
 *  3) The GameClock allows users to create purpose built time management schemes for
 *     the flow of game time. In most cases, when the game is running (not paused), game
 *     time will pass at the same rate as wall time.  However, in some situations, this
 *     may not be desirable. For example some physics simulations work best if time passes
 *     at a fixed interval. The GameClock can provide this feature as well through
 *     purpose built TimeManager instances.
 * 
 * @author Scott Wallace
 *
 * @see GameClock.TimeManager
 */
public final class GameClock {

	public static final long NANOS_PER_SECOND = 1000000000;

	public static final long US_PER_SECOND = 1000000;

	public static final long MS_PER_SECOND = 1000;
	
	public static final long NANOS_PER_MS = NANOS_PER_SECOND / MS_PER_SECOND;

	/**
	 * These private state attributes should never be externally visible.
	 * 
	 * Outside observers should only see two possible meta states:
	 *   running 
	 *   paused
	 */
	private static final int STATE_NOT_YET_STARTED = 0x0000;
	private static final int STATE_RUNNING = 0x0100;
	private static final int STATE_PAUSING = 0x0101;
	private static final int STATE_PAUSED = 0x0010;
	private static final int STATE_RESUMING = 0x0011;
	
	public static final long NO_NANO_TIME = -1;
	
	private static GameClock theClock = null;
	private TimeManager theTicker = null;
	private int state = STATE_NOT_YET_STARTED;
	private long wallNanoTime;
	private long gameNanoTime;
	private long deltaWallNanoTime;
	private long deltaGameTime;
	private long deltaTimeOnPauseBegin;
	
	private GameClock() {
		state = STATE_NOT_YET_STARTED;
		wallNanoTime = System.nanoTime();
		gameNanoTime = 0; //System.nanoTime();
		deltaGameTime = 0;
		deltaWallNanoTime = 0;
	}
	
	public static GameClock getClock() {
		if (theClock == null) {
			theClock = new GameClock();
		}
		return theClock;
	}
	
	public static long frameRateFromNanos(final long nanos) {
		return NANOS_PER_SECOND / nanos;
	}
	
	public boolean timeManagerDefined() {
		return (theTicker != null);
	}
	public final void setTimeManager(TimeManager m) {
		if (state != STATE_NOT_YET_STARTED )
			throw new IllegalStateException("Cannot reset TimeManager after clock is started");

		
		theTicker = m;

		long t = theTicker.getAbsoluteWallTime(NO_NANO_TIME);
		wallNanoTime = t;
		gameNanoTime = 0; //coercedWallNanoTime;

		
	}
	
	
	/**
	 * This debugging command should only be used for unit testing and similar needs.
	 * Typically, a clock should maintain an arrow of time. This violates that
	 * primary assumption.
	 * 
	 * @param c
	 */
	final static void dbgReset(GameClock c) {
		c.state = STATE_NOT_YET_STARTED;
		c.wallNanoTime = System.nanoTime();
		c.gameNanoTime = 0;
		c.deltaGameTime = 0;
		c.deltaWallNanoTime = 0;
		
	}
	public final void begin() {
		if (theTicker == null)
			throw new IllegalStateException("Cannot begin clock until the TimerManager is set");

		state = STATE_RUNNING;
		long t = theTicker.getAbsoluteWallTime(NO_NANO_TIME);
		wallNanoTime = t;
		gameNanoTime = 0; //coercedWallNanoTime;
	}
	public final void pause() {
		switch (state) {
		case STATE_RUNNING:
			state = STATE_PAUSING;
			return;
		case STATE_RESUMING:
			state = STATE_PAUSED;
			return;
		case STATE_NOT_YET_STARTED:
			throw new IllegalStateException("Cannot pause until clock is running");
		
		}
	} 
	public final void resume() {
		switch (state) {
		case STATE_PAUSING:
			state = STATE_RUNNING;
			return;
		case STATE_PAUSED:
			state = STATE_RESUMING;
			return;
		case STATE_NOT_YET_STARTED:
			throw new IllegalStateException("Cannot resume clock until after it has started running");
		
		}
	}
	/**
	 * Sets an alarm for a future time.
	 * 
	 * @param delayFromNow the delay into the future (in nanoseconds) at which the alarm will expire.
	 * @return an Alarm object representing this alarm.
	 */
	public final Alarm setAlarm(long nanosFromNow) {
		return new Alarm(nanosFromNow);
		
	}
	
	/**
	 * Gets a sentinel alarm that is either always expired, or never expired.
	 * @param expired
	 * @return
	 */
	public final Alarm getSentinelAlarm(boolean expired) {
		if (expired) return new Alarm(0);
		else return new Alarm( Long.MAX_VALUE - gameNanoTime - 1);
		
	}
	public final void tick() {
		long t = theTicker.getAbsoluteWallTime(wallNanoTime);

		// deltaWallTime and wallTime are always updated regardless of the pause state
		deltaWallNanoTime = (t-wallNanoTime);
		wallNanoTime = t;
		
		
		long deltaCoerced = theTicker.coerceDeltaTime(deltaWallNanoTime);
		
		
		switch (state) {
		case STATE_RUNNING:
			deltaGameTime = deltaCoerced;
			break;
		case STATE_PAUSING:
			// we keep track of the delta time on this cycle, it will be
			// the deltaGame time when resuming
			// game time does not change
			state = STATE_PAUSED;
			deltaTimeOnPauseBegin = deltaCoerced;
			deltaGameTime = 0;
			break;
		case STATE_RESUMING:
			state = STATE_RUNNING;
			deltaGameTime = deltaTimeOnPauseBegin;
			break;
		case STATE_PAUSED:
			deltaGameTime = 0;
			break;
		case STATE_NOT_YET_STARTED:
			throw new IllegalStateException("Cannot tick clok before calling begin()");
			
		}
		gameNanoTime += deltaGameTime;
		
	}
	public boolean isPaused() { 
		if (state == STATE_PAUSED || state == STATE_RESUMING) return true; 
		return false;
	}
	public long getGameTime() {return gameNanoTime; }
	public long getWallTime() {return wallNanoTime; }
	public long getDeltaGameTime() { return deltaGameTime; }
	public long getDeltaWallTime() { return deltaWallNanoTime; }
	
	
	/**
	 * The TimeManager interface specifies methods for the GameClock
	 * to interact with a standard system timer such as System.nanoTime()
	 * 
	 * The interface facilitates reading the system timer and coercing the passage
	 * of time to make the game "more stable", "more playable", or simply 
	 * "mo' better".
	 * 
	 * @author Scott Wallace
	 *
	 */
	public interface TimeManager {
		/**
		 * Gets the absolute time, as read from System.nanoTime(),  
		 * System.currentTimeMillis(), or a related function.
		 * 
		 * @return a monotonically increasing value representing the instantaneous
		 * time at the moment this method is called in nanoseconds.
		 */
		public long getAbsoluteWallTime(long lastAbsoluteTime);
		
		/**
		 * Coerces the passage of time so as to make the game "more stable" or 
		 * somesuch.
		 * 
		 * @param dwt the actual time passed as measured by the change in 
		 * absolute wall time.
		 * 
		 * @return the 'measured' time passed which will be reflected by the
		 * GameClock's <i>game time</i>. In most cases, users will simply want
		 * to return the input value itself. Less often users may want to return
		 * a constant value (perhaps to simulate a consistent framerate for 
		 * a physics engine), or a capped value (so that objects with a constant
		 * velocity don't "jump" too far on slow frames--instead they actually 
		 * slow down)
		 * 
		 */
		public long coerceDeltaTime(long dwt);
	}
	/**
	 * The StandardTimeManager does nothing to the system's reported time.
	 */
	public static class StandardTimeManager implements TimeManager {
		public long getAbsoluteWallTime(long lastAbsoluteTime){
			return System.nanoTime();
		}
		public long coerceDeltaTime(long dwt) { return dwt; }

	}
	/**
	 * The SmoothTimeManager will keep a pseudo running average over the last
	 * 1000 frames and peg the 'delta time' to within a factor of <code>factor</code> of
	 * that average. <code>factor</code> should be >= 1.0
	 */
	public static class SmoothTimeManager implements TimeManager {
		long total = 0;
		long n = 0;
		double factor;
		public SmoothTimeManager(double factor) { this.factor = factor; }
		public long getAbsoluteWallTime(long lastAbsoluteTime){
			return System.nanoTime();
		}
		public long coerceDeltaTime(long dwt) { 
			total += dwt;
			n++;
			long avg = (long) total / n;
			long high = (long) (factor*avg);
			long low = (long) (avg/factor);
			if (dwt > high) return high;
			if (dwt < low) return low;
			if (n > 1000) {
				n = 1000;
				total -= avg;
			}
			return dwt;
		}

	}
	/**
	 * A TimeManager that will peg perceived deltaTime between a high or low value.
	 */
	public static class PegTimeManager implements TimeManager {
		long low;
		long high;
		public PegTimeManager(long low, long high) {
			this.low = low;
			this.high = high;
		}
		public long getAbsoluteWallTime(long lastAbsoluteTime){
			return System.nanoTime();
		}
		public long coerceDeltaTime(long dwt) { 
			if (dwt < low) return low;
			if (dwt > high) return high;
			return dwt;
		}
	}

	/**
	 * A TimeManager that will pause the thread by sleeping so as to maintain
	 * a desired update rate.
	 */
	public static class SleepIfNeededTimeManager implements TimeManager {
		long targetMS;
		long targetNanos;
		double desiredFPS;
		public SleepIfNeededTimeManager(double desiredFPS) {
			this.desiredFPS = desiredFPS;
			targetMS = (long)(GameClock.MS_PER_SECOND / desiredFPS);
			targetNanos = (long)(GameClock.NANOS_PER_SECOND / desiredFPS);
		}
		public long getAbsoluteWallTime(long lastAbsoluteTime){
			
			long now = System.nanoTime();
			long spent;
			
			while (now - lastAbsoluteTime < targetNanos) {
				try {
					spent = (now-lastAbsoluteTime)/GameClock.NANOS_PER_MS;
					Thread.sleep(targetMS - spent);
				} catch (InterruptedException ie) {}
				now = System.nanoTime();
			}
			return now;
		}
		public long coerceDeltaTime(long dwt) { 
			return dwt;
		}
		
		public void setMaxFPS(double desiredFPS)
		{
			this.desiredFPS = desiredFPS;
			targetMS = (long)(GameClock.MS_PER_SECOND / desiredFPS);
		}
		
		public double getMaxFPS()
		{
			return desiredFPS;
		}

	}

	/**
	 * The Alarm class encapsulates data associated with a periodic
	 * alarm. Once set, an alarm's expired() method returns <code>false</code>
	 * until it reaches the preset future time. At that point expired() returns
	 * <code>true</code> until the alarm is reset.
	 * 
	 * 
	 * @author Scott Wallace
	 *
	 */
	public class Alarm {
		private long delayPeriod;
		private long alarmTime;
		
		/**
		 * Create an alarm that expires at some time in the future 
		 * (here time refers to GameTime).
		 * 
		 * 
		 * @param delayFromNow the offset from the current game time (in nano seconds)
		 */
		private Alarm(long delayFromNow) {
			delayPeriod = delayFromNow;
			alarmTime = gameNanoTime + delayPeriod;			
		}
		
		/**
		 * 
		 * @return <code>true</code> iff the alarm is past due (ringing).
		 */
		public boolean expired() { return gameNanoTime >= alarmTime; }
		
		
		/**
		 * Resets the alarm to a future time.  The new expiration time is
		 * based on the current game time using the same offset interval as 
		 * was used previously.
		 */
		public void reset() {
			alarmTime = gameNanoTime + delayPeriod;
		}
		
		/**
		 * Resets the alarm to a future time.  The new expiration time is
		 * based on the current game time plus the new delay
		 * 
		 * @param newDelay the offset in nanoseconds from the current game time
		 */
		public void reset(long newDelay) {
			delayPeriod = newDelay;
			alarmTime = gameNanoTime + delayPeriod;
		}
		
		public long elapsedTime() {
			// difference btwn current time and when alarm originated (set or reset)
			return gameNanoTime - (alarmTime-delayPeriod);
		}
		
		public long remainingTime() {
			return alarmTime - gameNanoTime; 
		}
	}
}



