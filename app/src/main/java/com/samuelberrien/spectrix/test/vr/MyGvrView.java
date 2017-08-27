package com.samuelberrien.spectrix.test.vr;

import android.content.Context;
import android.media.audiofx.Visualizer;

import com.google.vr.cardboard.ThreadUtils;
import com.google.vr.sdk.base.GvrView;
import com.samuelberrien.spectrix.test.utils.UpdateThread;
import com.samuelberrien.spectrix.test.utils.Visualization;

/**
 * Created by samuel on 11/01/17.
 */

public class MyGvrView extends GvrView {

	private Visualization visualization;
	private GLStereoRenderer glStereoRenderer;
	private UpdateVrThread updateVrThread;

	public MyGvrView(Context context, Visualization visualization) {
		super(context);
		setEGLContextClientVersion(2);
		setTransitionViewEnabled(true);

		this.visualization = visualization;

		glStereoRenderer = new GLStereoRenderer(context, visualization);
		setRenderer(glStereoRenderer);
	}

	@Override
	public void onPause() {
		updateVrThread.cancel();
		try {
			updateVrThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (updateVrThread != null) {
			updateVrThread.cancel();
			try {
				updateVrThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		updateVrThread = new UpdateVrThread();
		updateVrThread.start();
	}

	private class UpdateVrThread extends Thread {


		private Visualizer visualizer;
		private boolean isCanceled;

		public UpdateVrThread() {
			isCanceled = false;
			visualizer = new Visualizer(0);
			visualizer.setEnabled(false);
			visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
			visualizer.setEnabled(true);
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

			while (!isCanceled) {
				if (visualization.isInit()) {
					byte[] bytes = new byte[visualizer.getCaptureSize()];
					visualizer.getFft(bytes);
					float[] fft = new float[bytes.length / 2];

					for (int i = 0; i < fft.length; i++) {
						float real = (float) (bytes[(i * 2) + 0]) / 128.0f;
						float imag = (float) (bytes[(i * 2) + 1]) / 128.0f;
						fft[i] = ((real * real) + (imag * imag));
					}
					glStereoRenderer.updateFreqArray(fft);
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

		public void cancel() {
			isCanceled = true;
		}
	}
}
