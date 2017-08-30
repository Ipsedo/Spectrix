package com.samuelberrien.spectrix.test.threads;

import android.media.audiofx.Visualizer;

import com.samuelberrien.spectrix.test.utils.Visualization;

/**
 * Created by samuel on 23/08/17.
 */

public class StreamThread extends CancelableThread {

	private Visualizer visualizer;

	public StreamThread(Visualization visualization) {
		super("StreamThread", visualization);

		visualizer = new Visualizer(0);
		visualizer.setEnabled(false);
		/*visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
		visualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS);*/
		visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
		visualizer.setEnabled(true);
	}

	@Override
	protected void work(Visualization visualization) {
		byte[] bytes = new byte[visualizer.getCaptureSize()];
		visualizer.getFft(bytes);
		float[] fft = new float[bytes.length / 2];

		for (int i = 0; i < fft.length; i++) {
			float real = (float) (bytes[(i * 2) + 0]) / 128.0f;
			float imag = (float) (bytes[(i * 2) + 1]) / 128.0f;
			fft[i] = ((real * real) + (imag * imag));
		}
		visualization.update(fft);
	}

	@Override
	protected void onEnd() {
		visualizer.setEnabled(false);
		visualizer.release();
	}

}
