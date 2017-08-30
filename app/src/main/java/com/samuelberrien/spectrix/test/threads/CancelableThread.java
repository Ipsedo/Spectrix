package com.samuelberrien.spectrix.test.threads;

import com.samuelberrien.spectrix.test.utils.Visualization;

/**
 * Created by samuel on 30/08/17.
 */

public abstract class CancelableThread extends Thread {

	private Visualization visualization;

	private boolean isCanceled;

	public CancelableThread(String name, Visualization visualization) {
		super(name);
		this.visualization = visualization;
		isCanceled = false;
	}

	public boolean isCanceled() {
		return isCanceled;
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
}
