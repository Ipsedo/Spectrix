package com.samuelberrien.spectrix.normal;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.utils.Visualization;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 23/08/17.
 */

public class GLRenderer2D implements GLSurfaceView.Renderer {

    private Context context;
    private Visualization visualization;

    private final float[] mProjectionMatrix;
    private final float[] mViewMatrix;

    private MyGLSurfaceView.OnVisualizationInitFinish onVisualizationInitFinish;

    GLRenderer2D(Context context, Visualization visualization, MyGLSurfaceView.OnVisualizationInitFinish onVisualizationInitFinish) {
        this.context = context;
        this.visualization = visualization;
        mProjectionMatrix = new float[16];
        mViewMatrix = new float[16];
        this.onVisualizationInitFinish = onVisualizationInitFinish;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        visualization.init(context, false);
        onVisualizationInitFinish.onFinish();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float[] camPos = visualization.getCameraPosition();
        float[] camDir = visualization.getInitCamLookDirVec();

        Matrix.setLookAtM(mViewMatrix, 0, camPos[0], camPos[1], camPos[2], camDir[0] + camPos[0], camDir[1] + camPos[1], camDir[2] + camPos[2], 0f, 1.0f, 0.0f);

        //Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        visualization.draw(mProjectionMatrix, mViewMatrix, new float[3], new float[4]);
    }
}
