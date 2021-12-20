package com.samuelberrien.spectrix.normal;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.samuelberrien.spectrix.threads.MicThread;
import com.samuelberrien.spectrix.threads.StreamThread;
import com.samuelberrien.spectrix.threads.VisualizationThread;
import com.samuelberrien.spectrix.utils.Visualization;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by samuel on 23/08/17.
 */

public class MyGLSurfaceView extends GLSurfaceView implements GLSurfaceView.EGLConfigChooser {

    private Visualization visualization;
    private VisualizationThread visualizationThread;

    private GLRenderer glRenderer;

    private int currentListening;

    public MyGLSurfaceView(Context context, Visualization visualization, int currentListening, OnVisualizationInitFinish onVisualizationInitFinish) {
        super(context);
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);

        this.visualization = visualization;

        setEGLConfigChooser(this);

        glRenderer = new GLRenderer(getContext(), visualization, onVisualizationInitFinish);
        setRenderer(glRenderer);
        setOnTouchListener(glRenderer);

        this.currentListening = currentListening;

        if (this.currentListening == VisualizationThread.STREAM_MUSIC) {
            visualizationThread = new StreamThread(visualization);
            visualizationThread.start();
        } else if (this.currentListening == VisualizationThread.MIC_MUSIC) {
            visualizationThread = new MicThread(visualization);
            visualizationThread.start();
        }
    }

    @Override
    public void onPause() {
        if (visualizationThread != null) {
            visualizationThread.cancel();
            try {
                visualizationThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        } else if (this.currentListening == VisualizationThread.MIC_MUSIC) {
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

    @Override
    public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eglDisplay) {
        int[] attribs = {
                EGL10.EGL_LEVEL, 0,
                EGL10.EGL_RENDERABLE_TYPE, 4,  // EGL_OPENGL_ES2_BIT
                EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 16,
                EGL10.EGL_SAMPLE_BUFFERS, 1,
                EGL10.EGL_SAMPLES, 4,  // 4x MSAA.
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] configCounts = new int[1];
        egl10.eglChooseConfig(eglDisplay, attribs, configs, 1, configCounts);

        if (configCounts[0] == 0) {
            // Failed! Error handling.
            return null;
        } else {
            return configs[0];
        }
    }

    public interface OnVisualizationInitFinish {
        void onFinish();
    }
}
