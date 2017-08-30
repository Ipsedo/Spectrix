package com.samuelberrien.spectrix.test.normal;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.samuelberrien.spectrix.test.utils.Visualization;
import com.samuelberrien.spectrix.test.utils.threads.CancelableThread;
import com.samuelberrien.spectrix.test.utils.threads.MicThread;
import com.samuelberrien.spectrix.test.utils.threads.StreamThread;

/**
 * Created by samuel on 23/08/17.
 */

public class MyGLSurfaceView extends GLSurfaceView {

	private Visualization visualization;
	private CancelableThread cancelableThread;

	private GLRenderer3D glRenderer3D;

	public static final int STREAM_MUSIC = 0;
	public static final int MIC_MUSIC = 1;

	private int currentListening;

	public MyGLSurfaceView(Context context, Visualization visualization, int currentListening) {
		super(context);
		setEGLContextClientVersion(2);
		setPreserveEGLContextOnPause(true);

		this.visualization = visualization;
		if (this.visualization.is3D()) {
			glRenderer3D = new GLRenderer3D(getContext(), this.visualization);
			setRenderer(glRenderer3D);
		} else {
			GLRenderer2D glRenderer2D = new GLRenderer2D(getContext(), this.visualization);
			setRenderer(glRenderer2D);
		}

		this.currentListening = currentListening;

		if(this.currentListening == STREAM_MUSIC) {
			cancelableThread = new StreamThread(visualization);
			cancelableThread.start();
		} else {
			cancelableThread = new MicThread(visualization);
			cancelableThread.start();
		}
	}

	@Override
	public void onPause() {
		cancelableThread.cancel();
		try {
			cancelableThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (cancelableThread != null) {
			cancelableThread.cancel();
			try {
				cancelableThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(currentListening == STREAM_MUSIC) {
			cancelableThread = new StreamThread(visualization);
			cancelableThread.start();
		} else {
			cancelableThread = new MicThread(visualization);
			cancelableThread.start();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (glRenderer3D != null)
			glRenderer3D.onTouchEvent(e);

		return true;
	}

	public void setListening(int listeningId) {
		if (cancelableThread != null) {
			cancelableThread.cancel();
			try {
				cancelableThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		switch (listeningId) {
			case STREAM_MUSIC:
				cancelableThread = new StreamThread(visualization);
				cancelableThread.start();
				return;
			case MIC_MUSIC:
				cancelableThread = new MicThread(visualization);
				cancelableThread.start();
				return;
		}
		throw new IllegalArgumentException("Wrong listening identifiant !");
	}
}
