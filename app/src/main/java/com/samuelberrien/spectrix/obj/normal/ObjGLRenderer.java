package com.samuelberrien.spectrix.obj.normal;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 05/01/17.
 */

public abstract class ObjGLRenderer implements GLSurfaceView.Renderer {

	protected Context context;

	protected final float[] mProjectionMatrix = new float[16];
	protected final float[] mViewMatrix = new float[16];

	private final float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
	protected final float[] mLightPosInEyeSpace = new float[4];
	private final float[] mLightModelMatrix = new float[16];
	private final float[] mLightPosInWorldSpace = new float[4];

	protected float[] mCameraPosition = new float[3];
	protected float[] mCameraDirection = new float[3];
	private float mCameraUp = 1f;
	private float phi = 0f;
	private float theta = 0f;
	private float maxRange = 1f;
	private float projectionAngle = 40f;
	private boolean isZoomingDoubleTap;
	private boolean isZoomingUp;
	private float ratio = 1f;

	/**
	 * @param context
	 */
	public ObjGLRenderer(Context context) {
		this.context = context;
	}

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		this.mCameraDirection = new float[]{this.mCameraPosition[0], this.mCameraPosition[1], this.mCameraPosition[2] + 1f};
		this.isZoomingDoubleTap = false;
		this.isZoomingUp = false;
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	}

	/**
	 * update the camera look point with orientation angles
	 *
	 * @param phi   angle phi
	 * @param theta angle theta
	 */
	public void updateCameraOrientation(float phi, float theta) {
		float oldPhi = this.phi;

		this.phi += phi;
		this.theta += theta;

		if (oldPhi > 0 && (int) ((oldPhi + Math.toRadians(90)) / Math.toRadians(180)) != (int) ((this.phi + Math.toRadians(90)) / Math.toRadians(180))) {
			this.mCameraUp = -this.mCameraUp;
		} else if (oldPhi < 0 && (int) ((oldPhi - Math.toRadians(90)) / Math.toRadians(180)) != (int) ((this.phi - Math.toRadians(90)) / Math.toRadians(180))) {
			this.mCameraUp = -this.mCameraUp;
		}

		this.mCameraDirection[0] = this.maxRange * (float) (Math.cos(this.phi) * Math.sin(this.theta)) + this.mCameraPosition[0];
		this.mCameraDirection[1] = this.maxRange * (float) Math.sin(this.phi) + this.mCameraPosition[1];
		this.mCameraDirection[2] = this.maxRange * (float) (Math.cos(this.phi) * Math.cos(this.theta)) + this.mCameraPosition[2];
	}

	/**
	 * @param dist
	 */
	public void updateZoom(float dist) {
		this.projectionAngle += dist;
		if (this.projectionAngle < 10f) {
			this.projectionAngle = 10f;
		}
		if (this.projectionAngle > 100f) {
			this.projectionAngle = 100f;
		}
		this.updateProjection();
	}

	public void setDoubleTapZoom(boolean turnOn, boolean toUp) {
		this.isZoomingDoubleTap = turnOn;
		this.isZoomingUp = toUp;
	}

	public boolean isZoomUp() {
		return this.projectionAngle < 40f;
	}

	/**
	 * @param freqArray
	 */
	protected abstract void update(float[] freqArray);

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	protected void updateLight(float x, float y, float z) {
		Matrix.setIdentityM(this.mLightModelMatrix, 0);
		Matrix.translateM(this.mLightModelMatrix, 0, x, y, z);
		Matrix.multiplyMV(this.mLightPosInWorldSpace, 0, this.mLightModelMatrix, 0, this.mLightPosInModelSpace, 0);
		Matrix.multiplyMV(this.mLightPosInEyeSpace, 0, this.mViewMatrix, 0, this.mLightPosInWorldSpace, 0);
	}

	public void onDrawFrame(GL10 unused) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		if (this.isZoomingDoubleTap && this.isZoomingUp) {
			this.updateZoom(10);
		} else if (this.isZoomingDoubleTap) {
			this.updateZoom(-10);
		}
		this.updateProjection();

		Matrix.setLookAtM(this.mViewMatrix, 0, this.mCameraPosition[0], this.mCameraPosition[1], this.mCameraPosition[2], this.mCameraDirection[0], this.mCameraDirection[1], this.mCameraDirection[2], 0f, this.mCameraUp, 0f);
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		this.ratio = (float) width / height;

		//Matrix.frustumM(mProjectionMatrix, 0, -this.ratio, this.ratio, -1, 1, 3, 50f);

		Matrix.perspectiveM(this.mProjectionMatrix, 0, this.projectionAngle, ratio, 1, 50f);
	}

	private void updateProjection() {
		Matrix.perspectiveM(this.mProjectionMatrix, 0, this.projectionAngle, this.ratio, 1, 50f);
	}
}
