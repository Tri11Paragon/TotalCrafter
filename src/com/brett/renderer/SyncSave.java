package com.brett.renderer;

/**
* @author Brett
* @date Jun. 21, 2020
*/

public class SyncSave {
	
	private static long lastFrameTime = 0;
	
	public static void syncd(int fps) {
		long delta = System.nanoTime() - lastFrameTime;
		long time = 1000000000/fps;
		while ((time - delta) > 0) {
			delta = System.nanoTime() - lastFrameTime;
			Thread.yield();
		}
		lastFrameTime = System.nanoTime();
	}
	
	public static class Sync {
		/** number of nano seconds in a second */
		private static final long NANOS_IN_SECOND = 1000L * 1000L * 1000L;

		/** The time to sleep/yield until the next frame */
		private static long nextFrame = 0;
		
		/** whether the initialisation code has run */
		private static boolean initialised = false;
		
		/** for calculating the averages the previous sleep/yield times are stored */
		private static RunningAvg sleepDurations = new RunningAvg(10);
		private static RunningAvg yieldDurations = new RunningAvg(10);
		
		
		/**
		 * An accurate sync method that will attempt to run at a constant frame rate.
		 * It should be called once every frame.
		 * 
		 * @param fps - the desired frame rate, in frames per second
		 */
		public static void sync(int fps) {
			if (fps <= 0) return;
			if (!initialised) initialise();
			
			try {
				// sleep until the average sleep time is greater than the time remaining till nextFrame
				for (long t0 = getTime(), t1; (nextFrame - t0) > sleepDurations.avg(); t0 = t1) {
					Thread.sleep(1);
					sleepDurations.add((t1 = getTime()) - t0); // update average sleep time
				}
		
				// slowly dampen sleep average if too high to avoid yielding too much
				sleepDurations.dampenForLowResTicker();
		
				// yield until the average yield time is greater than the time remaining till nextFrame
				for (long t0 = getTime(), t1; (nextFrame - t0) > yieldDurations.avg(); t0 = t1) {
					Thread.yield();
					yieldDurations.add((t1 = getTime()) - t0); // update average yield time
				}
			} catch (InterruptedException e) {
				
			}
			
			// schedule next frame, drop frame(s) if already too late for next frame
			nextFrame = Math.max(nextFrame + NANOS_IN_SECOND / fps, getTime());
		}
		
		/**
		 * This method will initialise the sync method by setting initial
		 * values for sleepDurations/yieldDurations and nextFrame.
		 * 
		 * If running on windows it will start the sleep timer fix.
		 */
		private static void initialise() {
			initialised = true;
			
			sleepDurations.init(1000 * 1000);
			yieldDurations.init((int) (-(getTime() - getTime()) * 1.333));
			
			nextFrame = getTime();
			
			String osName = System.getProperty("os.name");
			
			if (osName.startsWith("Win")) {
				// On windows the sleep functions can be highly inaccurate by 
				// over 10ms making in unusable. However it can be forced to 
				// be a bit more accurate by running a separate sleeping daemon
				// thread.
				Thread timerAccuracyThread = new Thread(new Runnable() {
					public void run() {
						try {
							Thread.sleep(Long.MAX_VALUE);
						} catch (Exception e) {}
					}
				});
				
				timerAccuracyThread.setName("LWJGL Timer");
				timerAccuracyThread.setDaemon(true);
				timerAccuracyThread.start();
			}
		}

		/**
		 * Get the system time in nano seconds
		 * 
		 * @return will return the current time in nano's
		 */
		private static long getTime() {
			return System.nanoTime();
		}


	}
	
	private static long variableYieldTime, lastTime;
	
	/**
     * An accurate sync method that adapts automatically
     * to the system it runs on to provide reliable results.
     *
     * @param fps The desired frame rate, in frames per second
     * @author kappa (On the LWJGL Forums)
     */
    public static void sync(int fps) {
        if (fps <= 0) return;
         
        long sleepTime = 1000000000 / fps; // nanoseconds to sleep this frame
        // yieldTime + remainder micro & nano seconds if smaller than sleepTime
        long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000*1000));
        long overSleep = 0; // time the sync goes over by
         
        try {
            while (true) {
                long t = System.nanoTime() - lastTime;
                 
                if (t < sleepTime - yieldTime) {
                    Thread.sleep(1);
                }else if (t < sleepTime) {
                    // burn the last few CPU cycles to ensure accuracy
                    Thread.yield();
                }else {
                    overSleep = t - sleepTime;
                    break; // exit while loop
                }
            }
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }finally{
        	lastTime = System.nanoTime() - Math.min(overSleep, sleepTime);
           
            // auto tune the time sync should yield
            if (overSleep > variableYieldTime) {
                // increase by 200 microseconds (1/5 a ms)
                variableYieldTime = Math.min(variableYieldTime + 200*1000, sleepTime);
            }
            else if (overSleep < variableYieldTime - 200*1000) {
                // decrease by 2 microseconds
                variableYieldTime = Math.max(variableYieldTime - 2*1000, 0);
            }
        }
    }
	
	protected static class RunningAvg {
		private final long[] slots;
		private int offset;
		
		private static final long DAMPEN_THRESHOLD = 10 * 1000L * 1000L; // 10ms
		private static final float DAMPEN_FACTOR = 0.9f; // don't change: 0.9f is exactly right!

		public RunningAvg(int slotCount) {
			this.slots = new long[slotCount];
			this.offset = 0;
		}

		public void init(long value) {
			while (this.offset < this.slots.length) {
				this.slots[this.offset++] = value;
			}
		}

		public void add(long value) {
			this.slots[this.offset++ % this.slots.length] = value;
			this.offset %= this.slots.length;
		}

		public long avg() {
			long sum = 0;
			for (int i = 0; i < this.slots.length; i++) {
				sum += this.slots[i];
			}
			return sum / this.slots.length;
		}
		
		public void dampenForLowResTicker() {
			if (this.avg() > DAMPEN_THRESHOLD) {
				for (int i = 0; i < this.slots.length; i++) {
					this.slots[i] *= DAMPEN_FACTOR;
				}
			}
		}
	}
	
}
