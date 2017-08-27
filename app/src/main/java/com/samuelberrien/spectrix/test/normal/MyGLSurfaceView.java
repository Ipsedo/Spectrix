package com.samuelberrien.spectrix.test.normal;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.samuelberrien.spectrix.test.utils.UpdateThread;
import com.samuelberrien.spectrix.test.utils.Visualization;

/**
 * Created by samuel on 23/08/17.
 */

public class MyGLSurfaceView extends GLSurfaceView {

	private Visualization visualization;
	private UpdateThread updateThread;

	private boolean isRendererSetted = false;

	private GLRenderer3D glRenderer3D;

	public MyGLSurfaceView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		setPreserveEGLContextOnPause(true);
	}

	public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEGLContextClientVersion(2);
		setPreserveEGLContextOnPause(true);
	}

	public void setVisualization(Visualization visualization) {
		if (isRendererSetted)
			throw new RuntimeException();
		this.visualization = visualization;
		if (visualization.is3D()) {
			glRenderer3D = new GLRenderer3D(getContext(), this.visualization);
			setRenderer(glRenderer3D);
		} else {
			GLRenderer2D glRenderer2D = new GLRenderer2D(getContext(), this.visualization);
			setRenderer(glRenderer2D);
		}
		if (updateThread != null && !updateThread.isCanceled()) {
			updateThread.cancel();
		}
		updateThread = new UpdateThread(this.visualization);
		updateThread.start();
		isRendererSetted = true;
	}

	@Override
	public void onPause() {
		updateThread.cancel();
		try {
			updateThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (updateThread != null) {
			updateThread.cancel();
			try {
				updateThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			updateThread = new UpdateThread(this.visualization);
			updateThread.start();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if(glRenderer3D != null)
			glRenderer3D.handleEvent(e);
		return true;
	}
}
