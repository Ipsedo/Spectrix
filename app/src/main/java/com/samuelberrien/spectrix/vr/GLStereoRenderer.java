package com.samuelberrien.spectrix.vr;

import android.content.Context;
import android.media.audiofx.Visualizer;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;
import com.samuelberrien.spectrix.utils.core.Visualization;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by samuel on 11/01/17.
 */

public class GLStereoRenderer implements GvrView.StereoRenderer {

	protected Context context;

	private final float Z_NEAR = 1f;
	private final float Z_FAR = 50f;

	private float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];

	private float[] mHeadView = new float[16];
	private float[] mCamera = new float[16];

	private final float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
	protected final float[] mLightPosInEyeSpace = new float[4];
	private final float[] mLightModelMatrix = new float[16];
	private final float[] mLightPosInWorldSpace = new float[4];

	private Visualization visualization;
	private float[] freqArray;

	/**
	 * @param context
	 */
	GLStereoRenderer(Context context, Visualization visualization) {
		this.context = context;
		this.visualization = visualization;
		freqArray = new float[Visualizer.getCaptureSizeRange()[1]];
	}

	@Override
	public void onSurfaceCreated(EGLConfig eglConfig) {
		if (visualization.is3D()) {
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			GLES20.glEnable(GLES20.GL_CULL_FACE);
			GLES20.glDepthFunc(GLES20.GL_LEQUAL);
			GLES20.glDepthMask(true);
		}
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		visualization.init(context, true);
	}

	@Override
	public void onNewFrame(HeadTransform headTransform) {
		float[] camPos = visualization.getCameraPosition();
		float[] camDir = visualization.getInitCamLookDirVec();
		Matrix.setLookAtM(mCamera, 0, camPos[0], camPos[1], camPos[2], camDir[0] + camPos[0], camDir[1] + camPos[1], camDir[2] + camPos[2], 0.0f, 1.0f, 0.0f);
		headTransform.getHeadView(mHeadView, 0);
		visualization.update(freqArray);
	}

	private void updateLight(float[] xyz) {
		Matrix.setIdentityM(mLightModelMatrix, 0);
		Matrix.translateM(mLightModelMatrix, 0, xyz[0], xyz[1], xyz[2]);
		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
		Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
	}

	@Override
	public void onDrawEye(Eye eye) {
		if (visualization.is3D()) {
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			GLES20.glEnable(GLES20.GL_CULL_FACE);
			GLES20.glDepthFunc(GLES20.GL_LEQUAL);
			GLES20.glDepthMask(true);
		}
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// Apply the eye transformation to the camera.
		Matrix.multiplyMM(mViewMatrix, 0, eye.getEyeView(), 0, mCamera, 0);

		mProjectionMatrix = eye.getPerspective(Z_NEAR, Z_FAR);

		updateLight(visualization.getLightPosition());

		float[] cam = visualization.getCameraPosition();

		visualization.draw(mProjectionMatrix.clone(), mViewMatrix.clone(), mLightPosInEyeSpace.clone(), new float[]{cam[0], cam[1], cam[2]});
	}

	@Override
	public void onFinishFrame(Viewport viewport) {
	}

	@Override
	public void onSurfaceChanged(int width, int height) {
	}

	@Override
	public void onRendererShutdown() {
	}

	void updateFreqArray(float[] newFreqArray) {
		freqArray = newFreqArray;
	}
}
