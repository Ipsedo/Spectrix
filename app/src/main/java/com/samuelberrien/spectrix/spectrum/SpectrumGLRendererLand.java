package com.samuelberrien.spectrix.spectrum;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 17/12/16.
 */

public class SpectrumGLRendererLand implements GLSurfaceView.Renderer {

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[][] mSquaresModelMatrix;

    private int nbSquares;
    private ArrayList<SpectrumRect> mSquares;

    private float hightFreqsAugmentation = 30f;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        this.nbSquares = 512;
        this.mSquaresModelMatrix = new float[this.nbSquares][16];
        this.mSquares = new ArrayList<>();
        for (int i = 0; i < this.nbSquares; i++) {
            this.mSquares.add(new SpectrumRect(-0.5f * (2f / this.nbSquares - 0.005f), 0.5f * (2f / this.nbSquares - 0.005f), -1f, 1f));//new SpectrumRect((float) i / (float) (this.nbSquares / 2) - 1.0f, (float) i / (float) (this.nbSquares / 2) - 1.0f + 2.0f / this.nbSquares - 0.005f, -1.0f, 1.0f);
        }
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * update the squares with the given frequencies
     *
     * @param freqArray array of frequency
     */
    public void updateSquares(float[] freqArray) {
        for (int i = 0; i < this.nbSquares / 2; i++) {
            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, i / (this.nbSquares * 0.25f), 0f, 0f);
            Matrix.scaleM(mModelMatrix, 0, 1f, freqArray[i] + freqArray[i] * (float) i / this.hightFreqsAugmentation, 1f);
            Matrix.multiplyMM(this.mSquaresModelMatrix[i + this.nbSquares / 2], 0, mMVPMatrix, 0, mModelMatrix, 0);
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, -i / (this.nbSquares * 0.25f), 0f, 0f);
            Matrix.scaleM(mModelMatrix, 0, 1f, freqArray[i] + freqArray[i] * (float) i / this.hightFreqsAugmentation, 1f);
            Matrix.multiplyMM(this.mSquaresModelMatrix[this.nbSquares / 2 - 1 - i], 0, mMVPMatrix, 0, mModelMatrix, 0);
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        //Draw Rects
        for (int i = 0; i < this.nbSquares; i++) {
            this.mSquares.get(i).draw(this.mSquaresModelMatrix[i]);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }
}
