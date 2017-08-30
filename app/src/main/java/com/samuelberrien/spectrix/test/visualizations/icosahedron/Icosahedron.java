package com.samuelberrien.spectrix.test.visualizations.icosahedron;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.obj.drawable.ObjModelMtl;
import com.samuelberrien.spectrix.test.utils.Visualization;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by samuel on 24/08/17.
 */

public class Icosahedron implements Visualization {

	private Context context;

	private final float LIGHTAUGMENTATION = 1f;
	private final float DISTANCECOEFF = 0.001f;

	private Random rand;

	private float minDist;
	private float rangeDist;

	private int nbIcosahedron;
	private int nbSameIcosahedron;
	private ObjModelMtl icosahedron;
	private ArrayList<FloatBuffer>[] mAmbColorBuffer;
	private ArrayList<FloatBuffer>[] mDiffColorBuffer;
	private ArrayList<FloatBuffer>[] mSpecColorBuffer;
	private float[] mScale;
	private float[] mAngle;
	private float[] mAngleToAdd;
	private float[][] mTranslateVector;
	private float[][] mRotationOrientation;
	private float[][] mRotationMatrix;
	private float[][] mModelMatrix;
	private float mFreqAugmentation = 0.3f;

	private boolean isInit;

	public Icosahedron() {
		isInit = false;
	}

	@Override
	public void init(Context context, boolean isVR) {
		this.context = context;

		this.minDist = 10f;
		this.rangeDist = 20f;

		this.rand = new Random(System.currentTimeMillis());

		this.nbIcosahedron = 30;
		this.nbSameIcosahedron = !isVR ? 10 : 5;
		this.mAmbColorBuffer = new ArrayList[this.nbSameIcosahedron * this.nbIcosahedron];
		this.mDiffColorBuffer = new ArrayList[this.nbSameIcosahedron * this.nbIcosahedron];
		this.mSpecColorBuffer = new ArrayList[this.nbSameIcosahedron * this.nbIcosahedron];
		this.mScale = new float[this.nbIcosahedron * this.nbSameIcosahedron];
		this.mAngle = new float[this.nbIcosahedron * this.nbSameIcosahedron];
		this.mAngleToAdd = new float[this.nbIcosahedron * this.nbSameIcosahedron];
		this.mTranslateVector = new float[this.nbIcosahedron * this.nbSameIcosahedron][3];
		this.mRotationOrientation = new float[this.nbIcosahedron * this.nbSameIcosahedron][3];
		this.mRotationMatrix = new float[this.nbIcosahedron * this.nbSameIcosahedron][16];
		this.mModelMatrix = new float[this.nbIcosahedron * this.nbSameIcosahedron][16];

		this.setupIcosahedrons();

		isInit = true;
	}

	@Override
	public boolean isInit() {
		return isInit;
	}

	@Override
	public boolean is3D() {
		return true;
	}

	@Override
	public void update(float[] freqArray) {
		for (int i = 0; i < this.nbIcosahedron * this.nbSameIcosahedron; i++) {
			float[] mModelMatrix = new float[16];
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, this.mTranslateVector[i][0], this.mTranslateVector[i][1], this.mTranslateVector[i][2]);
			Matrix.setRotateM(mRotationMatrix[i], 0, mAngle[i] += mAngleToAdd[i], this.mRotationOrientation[i][0], this.mRotationOrientation[i][1], this.mRotationOrientation[i][2]);
			float[] tmpMat = mModelMatrix.clone();
			Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, mRotationMatrix[i], 0);
			int tmpFreqIndex = i / this.nbSameIcosahedron;
			float scale = this.mScale[i];
			float tmp = freqArray[tmpFreqIndex] + freqArray[tmpFreqIndex] * tmpFreqIndex * this.mFreqAugmentation;
			if (tmp > 0.7f) {
				scale += 0.7f * mScale[i];
			} else {
				scale += tmp * mScale[i];
			}
			Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

			this.mModelMatrix[i] = mModelMatrix.clone();
		}
	}

	@Override
	public float[] getCameraPosition() {
		return new float[]{0f, 0f, 0f};
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		float[] tmpModelViewMatrix = new float[16];
		float[] tmpModelViewProjectionMatrix = new float[16];
		for (int i = 0; i < this.nbIcosahedron * this.nbSameIcosahedron; i++) {
			Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mModelMatrix[i], 0);
			Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);

			this.icosahedron.setColors(this.mAmbColorBuffer[i], this.mDiffColorBuffer[i], this.mSpecColorBuffer[i]);
			this.icosahedron.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		}
	}

	@Override
	public String getName() {
		return "Icosahedron";
	}

	private void setupIcosahedrons() {
		this.icosahedron = new ObjModelMtl(this.context, "obj/icosahedron/icosahedron_obj.obj", "obj/icosahedron/icosahedron_mtl.mtl", LIGHTAUGMENTATION, DISTANCECOEFF);
		for (int i = 0; i < this.nbIcosahedron * this.nbSameIcosahedron; i++) {
			this.mAmbColorBuffer[i] = this.icosahedron.makeColor(this.rand);
			this.mDiffColorBuffer[i] = this.icosahedron.makeColor(this.rand);
			this.mSpecColorBuffer[i] = this.icosahedron.makeColor(0.5f, 0.5f, 0.5f);

			this.mScale[i] = (this.nbIcosahedron - i / this.nbSameIcosahedron) * 0.005f + 0.5f;

			float maxRange = rand.nextFloat() * rangeDist + minDist;
			double phi;
			double theta;
			phi = rand.nextDouble() * Math.PI * 2;
			theta = rand.nextDouble() * Math.PI * 2;
			float x = maxRange * (float) (Math.cos(phi) * Math.sin(theta));
			float y = maxRange * (float) Math.sin(phi);
			float z = maxRange * (float) (Math.cos(phi) * Math.cos(theta));
			this.mTranslateVector[i][0] = x;
			this.mTranslateVector[i][1] = y;
			this.mTranslateVector[i][2] = z;

			this.mAngle[i] = rand.nextFloat() * 360f;
			this.mAngleToAdd[i] = 1f + rand.nextFloat() * 3f;
			this.mRotationOrientation[i][0] = rand.nextFloat() * 2f - 1f;
			this.mRotationOrientation[i][1] = rand.nextFloat() * 2f - 1f;
			this.mRotationOrientation[i][2] = rand.nextFloat() * 2f - 1f;
		}
	}
}
