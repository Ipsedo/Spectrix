package com.samuelberrien.spectrix.threads;

import com.samuelberrien.spectrix.utils.core.Visualization;

/**
 * Created by samuel on 30/08/17.
 */

public abstract class VisualizationThread extends Thread {

	private static long TIME_TO_WAIT = 30L;

	public static final int STREAM_MUSIC = 0;
	public static final int MIC_MUSIC = 1;
	public static final int NONE = 3;

	private Visualization visualization;

	private boolean isCanceled;

	private long lastTime;

	VisualizationThread(String name, Visualization visualization) {
		super(name);
		this.visualization = visualization;
		isCanceled = false;
		lastTime = System.currentTimeMillis();
	}

	public void cancel() {
		isCanceled = true;
	}

	@Override
	public void run() {
		while (!isCanceled && !visualization.isInit()) {
			try {
				Thread.sleep(TIME_TO_WAIT);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}

		while (!this.isCanceled) {
			long t1 = System.currentTimeMillis();
			work(visualization);
			try {
				long toWait = TIME_TO_WAIT - (System.currentTimeMillis() - t1);
				Thread.sleep(toWait >= 0 ? toWait : 0);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			System.out.println("Update/s : " + 1000L / (System.currentTimeMillis() - lastTime));
			lastTime = System.currentTimeMillis();
		}

		onEnd();
	}

	protected abstract void work(Visualization visualization);

	protected abstract void onEnd();

	protected abstract float[] getFrequencyMagns();

}
