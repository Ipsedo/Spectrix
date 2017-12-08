package com.samuelberrien.spectrix.normal;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.samuelberrien.spectrix.threads.MicThread;
import com.samuelberrien.spectrix.threads.StreamThread;
import com.samuelberrien.spectrix.threads.VisualizationThread;
import com.samuelberrien.spectrix.utils.core.Visualization;

/**
 * Created by samuel on 23/08/17.
 */

public class MyGLSurfaceView extends GLSurfaceView {

	private Visualization visualization;
	private VisualizationThread visualizationThread;

	private GLRenderer3D glRenderer3D;

	private int currentListening;

	public MyGLSurfaceView(Context context, Visualization visualization, int currentListening, OnVisualizationInitFinish onVisualizationInitFinish) {
		super(context);
		setEGLContextClientVersion(2);
		setPreserveEGLContextOnPause(true);

		this.visualization = visualization;
		if (visualization.is3D()) {
			glRenderer3D = new GLRenderer3D(getContext(), visualization, onVisualizationInitFinish);
			setRenderer(glRenderer3D);
			setOnTouchListener(glRenderer3D);
		} else {
			GLRenderer2D glRenderer2D = new GLRenderer2D(getContext(), visualization, onVisualizationInitFinish);
			setRenderer(glRenderer2D);
		}

		this.currentListening = currentListening;

		if (this.currentListening == VisualizationThread.STREAM_MUSIC) {
			visualizationThread = new StreamThread(visualization);
			visualizationThread.start();
		} else {
			visualizationThread = new MicThread(visualization);
			visualizationThread.start();
		}
	}

	@Override
	public void onPause() {
		visualizationThread.cancel();
		try {
			visualizationThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (visualizationThread != null) {
			visualizationThread.cancel();
			try {
				visualizationThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (currentListening == VisualizationThread.STREAM_MUSIC) {
			visualizationThread = new StreamThread(visualization);
			visualizationThread.start();
		} else {
			visualizationThread = new MicThread(visualization);
			visualizationThread.start();
		}
	}

	/*@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (glRenderer3D != null)
			glRenderer3D.onTouchEvent(e);

		return true;
	}*/

	@Override
	public boolean performClick() {
		return true;
	}

	public void setListening(int listeningId) {
		if (visualizationThread != null) {
			visualizationThread.cancel();
			try {
				visualizationThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		currentListening = listeningId;
		switch (currentListening) {
			case VisualizationThread.STREAM_MUSIC:
				visualizationThread = new StreamThread(visualization);
				visualizationThread.start();
				return;
			case VisualizationThread.MIC_MUSIC:
				visualizationThread = new MicThread(visualization);
				visualizationThread.start();
				return;
		}
		throw new IllegalArgumentException("Wrong listening identifiant !");
	}

	public interface OnVisualizationInitFinish {
		void onFinish();
	}
}
