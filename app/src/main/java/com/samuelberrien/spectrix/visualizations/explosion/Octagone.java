package com.samuelberrien.spectrix.visualizations.explosion;

import android.opengl.Matrix;

import java.nio.FloatBuffer;

public class Octagone {

	private float mOctagoneScale;
	private float mOctagoneAngle;
	private float[] mOctagoneRotationOrientation;
	private float[] mOctagoneRotationMatrix;
	private float[] mOctagoneTranslateVector;
	private float[] mOctagoneSpeedVector;
	private float[] mOctagoneModelMatrix;
	private FloatBuffer mOctagoneColorBuffer;

	/**
	 * @param mOctagoneScale
	 * @param mOctagoneAngle
	 * @param mOctagoneRotationOrientation
	 * @param mOctagoneTranslateVector
	 * @param mOctagoneSpeedVector
	 * @param mOctagoneColorBuffer
	 */
	public Octagone(float mOctagoneScale, float mOctagoneAngle, float[] mOctagoneRotationOrientation, float[] mOctagoneTranslateVector, float[] mOctagoneSpeedVector, FloatBuffer mOctagoneColorBuffer) {
		this.mOctagoneAngle = mOctagoneAngle;
		this.mOctagoneScale = mOctagoneScale;
		this.mOctagoneRotationOrientation = mOctagoneRotationOrientation;
		this.mOctagoneTranslateVector = mOctagoneTranslateVector;
		this.mOctagoneSpeedVector = mOctagoneSpeedVector;
		this.mOctagoneColorBuffer = mOctagoneColorBuffer;
		this.mOctagoneRotationMatrix = new float[16];
		this.mOctagoneModelMatrix = new float[16];
	}

	/**
	 *
	 */
	public void move() {
		float[] tmp = this.mOctagoneSpeedVector;
		this.mOctagoneSpeedVector = new float[]{tmp[0] * 0.5f, tmp[1] * 0.5f, tmp[2] * 0.5f};
		tmp = this.mOctagoneTranslateVector;
		this.mOctagoneTranslateVector = new float[]{tmp[0] + this.mOctagoneSpeedVector[0], tmp[1] + this.mOctagoneSpeedVector[1], tmp[2] + this.mOctagoneSpeedVector[2]};
		this.mOctagoneAngle = this.mOctagoneAngle + 1f;

		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, this.mOctagoneTranslateVector[0], this.mOctagoneTranslateVector[1], this.mOctagoneTranslateVector[2]);
		Matrix.setRotateM(this.mOctagoneRotationMatrix, 0, this.mOctagoneAngle, this.mOctagoneRotationOrientation[0], this.mOctagoneRotationOrientation[1], this.mOctagoneRotationOrientation[2]);
		float[] tmpMat = mModelMatrix.clone();
		Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, this.mOctagoneRotationMatrix, 0);
		Matrix.scaleM(mModelMatrix, 0, this.mOctagoneScale, this.mOctagoneScale, this.mOctagoneScale);

		this.mOctagoneModelMatrix = mModelMatrix.clone();
	}

	/**
	 * @return
	 */
	public float[] getmOctagoneModelMatrix() {
		return this.mOctagoneModelMatrix;
	}

	/**
	 * @return
	 */
	public FloatBuffer getmOctagoneColorBuffer() {
		return this.mOctagoneColorBuffer;
	}

	/**
	 * @return
	 */
	public double getSpeedVectorNorm() {
		return Matrix.length(this.mOctagoneSpeedVector[0], this.mOctagoneSpeedVector[1], this.mOctagoneSpeedVector[2]);
		//return Math.sqrt(this.mOctagoneSpeedVector[0] * this.mOctagoneSpeedVector[0] + this.mOctagoneSpeedVector[1] * this.mOctagoneSpeedVector[1] + this.mOctagoneSpeedVector[2] * this.mOctagoneSpeedVector[2]);
	}
}