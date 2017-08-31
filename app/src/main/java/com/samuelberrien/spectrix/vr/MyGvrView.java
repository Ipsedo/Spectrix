package com.samuelberrien.spectrix.vr;

import android.content.Context;

import com.google.vr.sdk.base.GvrView;
import com.samuelberrien.spectrix.threads.MicThread;
import com.samuelberrien.spectrix.threads.StreamThread;
import com.samuelberrien.spectrix.threads.VisualizationThread;
import com.samuelberrien.spectrix.utils.core.Visualization;

/**
 * Created by samuel on 11/01/17.
 */

public class MyGvrView extends GvrView {

	private Visualization visualization;
	private GLStereoRenderer glStereoRenderer;
	private VisualizationThread updateVrThread;

	private boolean listeningStream;

	public MyGvrView(Context context, Visualization visualization, boolean listeningStream) {
		super(context);
		setEGLContextClientVersion(2);
		setTransitionViewEnabled(true);

		this.listeningStream = listeningStream;

		this.visualization = visualization;

		glStereoRenderer = new GLStereoRenderer(context, this.visualization);
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
		if (listeningStream) {
			updateVrThread = new StreamVrThread(visualization);
			updateVrThread.start();
		} else {
			updateVrThread = new MicVrThread(visualization);
			updateVrThread.start();
		}
	}

	private class StreamVrThread extends StreamThread {

		StreamVrThread(Visualization visualization) {
			super(visualization);
		}

		@Override
		protected void work(Visualization visualization) {
			glStereoRenderer.updateFreqArray(getFrequencyMagns());
		}
	}

	private class MicVrThread extends MicThread {

		MicVrThread(Visualization visualization) {
			super(visualization);
		}

		@Override
		public void work(Visualization visualization) {
			glStereoRenderer.updateFreqArray(getFrequencyMagns());
		}
	}
}
