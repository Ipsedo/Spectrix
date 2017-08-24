package com.samuelberrien.spectrix.test.vr;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;
import com.samuelberrien.spectrix.test.Visualization;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by samuel on 11/01/17.
 */

public class GLStereoRenderer implements GvrView.StereoRenderer {

	protected Context context;

	private static final float Z_NEAR = 1f;
	private static final float Z_FAR = 50.0f;
	protected float mCameraX = 0f;
	protected float mCameraY = 0f;
	protected float mCameraZ = 0.001f;

	protected float[] mProjectionMatrix = new float[16];
	protected final float[] mViewMatrix = new float[16];

	private float[] mHeadView = new float[16];
	private float[] mCamera = new float[16];

	private final float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
	protected final float[] mLightPosInEyeSpace = new float[4];
	private final float[] mLightModelMatrix = new float[16];
	private final float[] mLightPosInWorldSpace = new float[4];
	private float mLightX, mLightY, mLightZ;

	private Visualization visualization;

	/**
	 * @param context
	 */
	public GLStereoRenderer(Context context, Visualization visualization) {
		this.context = context;
		this.visualization = visualization;
	}

	@Override
	public void onSurfaceCreated(EGLConfig eglConfig) {
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		this.visualization.init(context);
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void updateLight(float x, float y, float z) {
		this.mLightX = x;
		this.mLightY = y;
		this.mLightZ = z;
	}

	@Override
	public void onNewFrame(HeadTransform headTransform) {
		Matrix.setLookAtM(this.mCamera, 0, this.mCameraX, this.mCameraY, this.mCameraZ, 0.0f + this.mCameraX, 0.0f + this.mCameraY, 1f + this.mCameraZ, 0.0f, 1.0f, 0.0f);
		headTransform.getHeadView(this.mHeadView, 0);
	}

	@Override
	public void onDrawEye(Eye eye) {
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// Apply the eye transformation to the camera.
		Matrix.multiplyMM(mViewMatrix, 0, eye.getEyeView(), 0, mCamera, 0);

		mProjectionMatrix = eye.getPerspective(Z_NEAR, Z_FAR);

		Matrix.setIdentityM(mLightModelMatrix, 0);
		Matrix.translateM(mLightModelMatrix, 0, this.mLightX, this.mLightY, this.mLightZ);
		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
		Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

		float[] cam = new float[4];
		Matrix.multiplyMV(cam, 0, this.mViewMatrix, 0, new float[]{this.mCameraX, this.mCameraY, this.mCameraZ, 1.0f}, 0);

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
}
