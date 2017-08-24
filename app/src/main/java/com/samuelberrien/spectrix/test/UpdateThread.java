package com.samuelberrien.spectrix.test;

import android.media.audiofx.Visualizer;

/**
 * Created by samuel on 23/08/17.
 */

public class UpdateThread extends Thread {

	private boolean isCanceled;
	private Visualization visualization;
	private Visualizer visualizer;

	public UpdateThread(Visualization visualization) {
		super("UpdateThread");
		isCanceled = false;
		this.visualization = visualization;

		visualizer = new Visualizer(0);
		visualizer.setEnabled(false);
		visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
		visualizer.setEnabled(true);
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
			if (visualization.isInit()) {
				byte[] bytes = new byte[visualizer.getCaptureSize()];
				visualizer.getFft(bytes);
				float[] fft = new float[bytes.length / 2];
				for (int i = 0; i < fft.length; i++) {
					float real = (float) (bytes[(i * 2) + 0]) / 128.0f;
					float imag = (float) (bytes[(i * 2) + 1]) / 128.0f;
					fft[i] = (real * real) + (imag * imag);
				}
				visualization.update(fft);
			}

			try {
				Thread.sleep(50L);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}

		visualizer.setEnabled(false);
	}
}