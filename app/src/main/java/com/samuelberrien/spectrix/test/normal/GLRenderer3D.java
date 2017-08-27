package com.samuelberrien.spectrix.test.normal;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.samuelberrien.spectrix.test.utils.Visualization;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 23/08/17.
 */

public class GLRenderer3D implements GLSurfaceView.Renderer {

	private Visualization visualization;

	protected Context context;

	protected final float[] mProjectionMatrix;
	protected final float[] mViewMatrix;

	private final float[] mLightPosInModelSpace;
	private final float[] mLightPosInEyeSpace;
	private final float[] mLightModelMatrix;
	private final float[] mLightPosInWorldSpace;

	private float[] mCameraPosition;

	private float projectionAngle;

	private float ratio;

	private final float TOUCH_SCALE_FACTOR_MOVE = 0.05f;
	private final float TOUCH_SCALE_FACTOR_ZOOM = 0.05f;
	private float mPreviousX;
	private float mPreviousY;

	private float[] cameraRotation;

	private boolean moreThanOneTouch;

	private ScaleGestureDetector myScaleGestureDetector;

	public GLRenderer3D(Context context, Visualization visualization) {
		this.context = context;

		this.visualization = visualization;

		projectionAngle = 40f;

		ratio = 1f;

		mCameraPosition = new float[3];

		mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
		mLightPosInEyeSpace = new float[4];
		mLightModelMatrix = new float[16];
		mLightPosInWorldSpace = new float[4];

		moreThanOneTouch = false;

		mProjectionMatrix = new float[16];
		mViewMatrix = new float[16];

		cameraRotation = new float[16];
		Matrix.setIdentityM(cameraRotation, 0);

		myScaleGestureDetector = new ScaleGestureDetector(context,
				new MyScaleGestureDetector());
	}

	public void handleEvent(MotionEvent e) {
		if (moreThanOneTouch && e.getPointerCount() == 1) {
			moreThanOneTouch = false;
			mPreviousX = e.getX() + 1f;
			mPreviousY = e.getY() + 1f;
		}
		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mPreviousX = e.getX() + 1f;
				mPreviousY = e.getY() + 1f;
				break;
			case MotionEvent.ACTION_MOVE:
				float dx = e.getX() + 1f - mPreviousX;
				float dy = e.getY() + 1f - mPreviousY;

				dy = -dy;

				float[] tmp1 = new float[16];
				Matrix.setRotateM(tmp1, 0, dx * TOUCH_SCALE_FACTOR_MOVE, 0f, 1f, 0f);

				float[] tmp2 = new float[16];
				Matrix.setRotateM(tmp2, 0, dy * TOUCH_SCALE_FACTOR_MOVE, 1f, 0f, 0f);

				Matrix.multiplyMM(tmp1, 0, tmp1.clone(), 0, tmp2.clone(), 0);

				Matrix.multiplyMM(cameraRotation, 0, cameraRotation.clone(), 0, tmp1, 0);
				break;
			case MotionEvent.ACTION_POINTER_UP:
				moreThanOneTouch = true;
				break;

		}
		mPreviousX = e.getX() + 1f;
		mPreviousY = e.getY() + 1f;

		myScaleGestureDetector.onTouchEvent(e);
	}

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		visualization.init(context);
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		this.ratio = (float) width / height;

		//Matrix.frustumM(mProjectionMatrix, 0, -this.ratio, this.ratio, -1, 1, 3, 50f);

		Matrix.perspectiveM(mProjectionMatrix, 0, projectionAngle, ratio, 1, 50f);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		float[] camDir = new float[]{0f, 0f, 1f, 0f};
		float[] camUp = new float[]{0f, 1f, 0f, 0f};
		Matrix.multiplyMV(camDir, 0, cameraRotation, 0, camDir.clone(), 0);
		Matrix.multiplyMV(camUp, 0, cameraRotation, 0, camUp.clone(), 0);

		Matrix.setLookAtM(mViewMatrix, 0, mCameraPosition[0], mCameraPosition[1], mCameraPosition[2], camDir[0], camDir[1], camDir[2], camUp[0], camUp[1], camUp[2]);
		Matrix.perspectiveM(mProjectionMatrix, 0, projectionAngle, ratio, 1, 50f);

		visualization.draw(mProjectionMatrix.clone(), mViewMatrix.clone(), mLightPosInEyeSpace.clone(), mCameraPosition.clone());
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	protected void updateLight(float x, float y, float z) {
		Matrix.setIdentityM(mLightModelMatrix, 0);
		Matrix.translateM(mLightModelMatrix, 0, x, y, z);
		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
		Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
	}

	private class MyScaleGestureDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			if (projectionAngle <= 120f && projectionAngle >= 10f) {
				float toAdd;
				if (detector.getScaleFactor() < 1f) {
					toAdd = 1f / detector.getScaleFactor();
				} else {
					toAdd = -detector.getScaleFactor();
				}
				projectionAngle += toAdd;
			} else if (projectionAngle > 120f) {
				projectionAngle = 120f;
			} else if (projectionAngle < 10f) {
				projectionAngle = 10f;
			}
			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {

		}
	}
}
