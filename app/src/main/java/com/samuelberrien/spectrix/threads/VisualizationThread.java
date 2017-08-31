package com.samuelberrien.spectrix.threads;

import com.samuelberrien.spectrix.utils.core.Visualization;

/**
 * Created by samuel on 30/08/17.
 */

public abstract class VisualizationThread extends Thread {

	public static final int STREAM_MUSIC = 0;
	public static final int MIC_MUSIC = 1;

	private Visualization visualization;

	private boolean isCanceled;

	public VisualizationThread(String name, Visualization visualization) {
		super(name);
		this.visualization = visualization;
		isCanceled = false;
	}

	public void cancel() {
		isCanceled = true;
	}

	@Override
	public void run() {
		while (!isCanceled && !visualization.isInit()) {
			try {
				Thread.sleep(50L);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}

		while (!this.isCanceled) {
			work(visualization);
			try {
				Thread.sleep(50L);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}

		onEnd();
	}

	protected abstract void work(Visualization visualization);

	protected abstract void onEnd();

	protected abstract float[] getFrequencyMagns();
}
