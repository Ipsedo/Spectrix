package com.samuelberrien.spectrix.test.utils;

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
		/*visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
		visualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS);*/
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

		//Visualizer.MeasurementPeakRms measurementPeakRms = new Visualizer.MeasurementPeakRms();

		while (!this.isCanceled) {
			if (visualization.isInit()) {
				byte[] bytes = new byte[visualizer.getCaptureSize()];
				visualizer.getFft(bytes);
				float[] fft = new float[bytes.length / 2];

				//visualizer.getMeasurementPeakRms(measurementPeakRms);

				for (int i = 0; i < fft.length; i++) {
					float real = (float) (bytes[(i * 2) + 0]) / 128.0f;
					float imag = (float) (bytes[(i * 2) + 1]) / 128.0f;
					fft[i] = ((real * real) + (imag * imag));//* 9600f / ((float) measurementPeakRms.mPeak + 9601f);
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
		visualizer.release();
	}
}
