package com.samuelberrien.spectrix.normal;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.samuelberrien.spectrix.utils.core.Visualization;
import com.samuelberrien.spectrix.utils.gesture.RotationGestureDetector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 23/08/17.
 */

public class GLRenderer3D implements GLSurfaceView.Renderer, RotationGestureDetector.OnRotationGestureListener {

	private Visualization visualization;

	protected Context context;

	protected final float[] mProjectionMatrix;
	protected final float[] mViewMatrix;

	private final float[] initCamLookDirVec;

	private final float[] mLightPosInModelSpace;
	private final float[] mLightPosInEyeSpace;
	private final float[] mLightModelMatrix;
	private final float[] mLightPosInWorldSpace;

	private float projectionAngle;

	private float ratio;

	private final float TOUCH_SCALE_FACTOR_MOVE = 0.05f;
	private final float TOUCH_SCALE_FACTOR_ZOOM = 1f;
	private final float TOUCH_SCALE_FACTOR_ROLL = 1f;
	private float mPreviousX;
	private float mPreviousY;

	private float[] cameraRotation;

	private boolean otherPointerUp;

	private ScaleGestureDetector myScaleGestureDetector;

	private float rollAngle;
	private float rollDelta;

	private float currCamPitch;
	private float currCamRoll;
	private float currCamYaw;

	private RotationGestureDetector rotationGestureDetector;

	private MyGLSurfaceView.OnVisualizationInitFinish onVisualizationInitFinish;

	public GLRenderer3D(Context context, Visualization visualization, MyGLSurfaceView.OnVisualizationInitFinish onVisualizationInitFinish) {
		this.context = context;

		this.visualization = visualization;

		projectionAngle = 40f;

		ratio = 1f;

		mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
		mLightPosInEyeSpace = new float[4];
		mLightModelMatrix = new float[16];
		mLightPosInWorldSpace = new float[4];

		otherPointerUp = false;

		rollAngle = 0f;
		rollDelta = 0f;

		currCamPitch = 0f;
		currCamRoll = 0f;
		currCamYaw = 0f;

		mProjectionMatrix = new float[16];
		mViewMatrix = new float[16];

		updateLight(0f, 0f, 0f);

		cameraRotation = new float[16];
		Matrix.setIdentityM(cameraRotation, 0);

		myScaleGestureDetector = new ScaleGestureDetector(context,
				new MyScaleGestureDetector());

		rotationGestureDetector = new RotationGestureDetector(this);

		this.onVisualizationInitFinish = onVisualizationInitFinish;

		initCamLookDirVec = visualization.getInitCamLookDirVec();
	}

	public void onTouchEvent(MotionEvent e) {
		float invNbPointer = 1f / (float) e.getPointerCount();

		float moyX;
		float moyY;

		if (otherPointerUp) {
			moyX = 0;
			moyY = 0;
			for (int i = 0; i < e.getPointerCount(); i++) {
				moyX += e.getX(i);
				moyY += e.getY(i);
			}
			moyX *= invNbPointer;
			moyY *= invNbPointer;
			mPreviousX = moyX + 1f;
			mPreviousY = moyY + 1f;
			otherPointerUp = false;
		}

		switch (e.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				mPreviousX = e.getX(0) + 1f;
				mPreviousY = e.getY(0) + 1f;
				break;
			case MotionEvent.ACTION_UP:
				otherPointerUp = true;
				break;
			case MotionEvent.ACTION_MOVE:
				moyX = 0;
				moyY = 0;
				for (int i = 0; i < e.getPointerCount(); i++) {
					moyX += e.getX(i);
					moyY += e.getY(i);
				}
				moyX *= invNbPointer;
				moyY *= invNbPointer;

				float dx = moyX + 1f - mPreviousX;
				float dy = moyY + 1f - mPreviousY;

				dy = -dy;

				float[] tmp1 = new float[16];
				Matrix.setRotateM(tmp1, 0, dx * TOUCH_SCALE_FACTOR_MOVE, 0f, 1f, 0f);

				float[] tmp2 = new float[16];
				Matrix.setRotateM(tmp2, 0, dy * TOUCH_SCALE_FACTOR_MOVE, 1f, 0f, 0f);

				Matrix.multiplyMM(tmp1, 0, tmp2, 0, tmp1.clone(), 0);

				Matrix.setRotateM(tmp2, 0, rollDelta * TOUCH_SCALE_FACTOR_ROLL, 0f, 0f, 1f);

				Matrix.multiplyMM(tmp1, 0, tmp1.clone(), 0, tmp2, 0);

				Matrix.multiplyMM(cameraRotation, 0, cameraRotation.clone(), 0, tmp1, 0);

				/*currCamYaw += dx * TOUCH_SCALE_FACTOR_MOVE;
				currCamRoll += rollDelta * TOUCH_SCALE_FACTOR_ROLL;
				currCamPitch += dy * TOUCH_SCALE_FACTOR_MOVE;*/

				/*float[] yawM = new float[16];
				Matrix.setRotateM(yawM, 0, currCamYaw, 0, 1f, 0f);
				float[] pitchM = new float[16];
				Matrix.setRotateM(pitchM, 0, currCamPitch, 1f, 0f, 0f);
				float[] rollM = new float[16];
				Matrix.setRotateM(rollM, 0, currCamRoll, 0f, 0f, 1f);

				float[] acc = new float[16];
				Matrix.multiplyMM(acc, 0, yawM, 0, pitchM, 0);
				Matrix.multiplyMM(acc, 0, rollM, 0, acc.clone(), 0);

				cameraRotation = acc.clone();*/

				/*Matrix.setRotateEulerM(cameraRotation, 0, currCamPitch, currCamRoll, currCamYaw);*/
				break;
			case MotionEvent.ACTION_POINTER_UP:
				otherPointerUp = true;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				moyX = 0;
				moyY = 0;
				for (int i = 0; i < e.getPointerCount(); i++) {
					moyX += e.getX(i);
					moyY += e.getY(i);
				}
				moyX *= invNbPointer;
				moyY *= invNbPointer;
				mPreviousX = moyX + 1f;
				mPreviousY = moyY + 1f;

		}
		moyX = 0;
		moyY = 0;
		for (int i = 0; i < e.getPointerCount(); i++) {
			moyX += e.getX(i);
			moyY += e.getY(i);
		}
		moyX *= invNbPointer;
		moyY *= invNbPointer;
		mPreviousX = moyX + 1f;
		mPreviousY = moyY + 1f;

		myScaleGestureDetector.onTouchEvent(e);
		rotationGestureDetector.onTouchEvent(e);
	}

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		visualization.init(context, false);

		onVisualizationInitFinish.onFinish();
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		ratio = (float) width / height;

		//Matrix.frustumM(mProjectionMatrix, 0, -this.ratio, this.ratio, -1, 1, 3, 50f);

		Matrix.perspectiveM(mProjectionMatrix, 0, projectionAngle, ratio, 1, 50f);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		float[] camDir = new float[]{initCamLookDirVec[0], initCamLookDirVec[1], initCamLookDirVec[2], 0f};
		float[] camUp = new float[]{0f, 1f, 0f, 0f};

		Matrix.multiplyMV(camDir, 0, cameraRotation, 0, camDir.clone(), 0);
		Matrix.multiplyMV(camUp, 0, cameraRotation, 0, camUp.clone(), 0);

		float[] mCameraPosition = visualization.getCameraPosition();

		Matrix.multiplyMV(camDir, 0, cameraRotation, 0, camDir.clone(), 0);

		Matrix.setLookAtM(mViewMatrix, 0, mCameraPosition[0], mCameraPosition[1], mCameraPosition[2], camDir[0] + mCameraPosition[0], camDir[1] + mCameraPosition[1], camDir[2] + mCameraPosition[2], camUp[0], camUp[1], camUp[2]);
		Matrix.perspectiveM(mProjectionMatrix, 0, projectionAngle, ratio, 1, 50f);

		visualization.draw(mProjectionMatrix.clone(), mViewMatrix.clone(), mLightPosInEyeSpace.clone(), mCameraPosition.clone());
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	protected void updateLight(float x, float y, float z) {
		//TODO light pos
		Matrix.setIdentityM(mLightModelMatrix, 0);
		Matrix.translateM(mLightModelMatrix, 0, x, y, z);
		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
		Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
	}

	@Override
	public void onRotationBegin() {
		rollAngle = 0f;
	}

	@Override
	public void onRotation(RotationGestureDetector rotationDetector) {
		rollDelta = rollAngle + rotationDetector.getAngle();
		rollAngle = -rotationDetector.getAngle();
	}

	private class MyScaleGestureDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float toAdd;
			if (detector.getScaleFactor() < 1f) {
				toAdd = TOUCH_SCALE_FACTOR_ZOOM * 1f / detector.getScaleFactor();
			} else {
				toAdd = -TOUCH_SCALE_FACTOR_ZOOM * detector.getScaleFactor();
			}
			projectionAngle = toAdd + projectionAngle > 120f ? 120f : toAdd + projectionAngle < 10f ? 10f : toAdd + projectionAngle;

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
