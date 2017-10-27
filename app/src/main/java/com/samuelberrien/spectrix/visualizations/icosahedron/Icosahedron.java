package com.samuelberrien.spectrix.visualizations.icosahedron;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.drawable.obj.ObjSpecVBO;
import com.samuelberrien.spectrix.utils.core.Visualization;

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
	private ObjSpecVBO icosahedron;
	private float[] mScale;
	private float[] mAngle;
	private float[] mAngleToAdd;
	private float[][] mTranslateVector;
	private float[][] mRotationOrientation;
	private float[][] mRotationMatrix;
	private float[][] mModelMatrix;
	private float[][] mIcoColors;

	private boolean isInit;

	public Icosahedron() {
		isInit = false;
	}

	@Override
	public void init(Context context, boolean isVR) {
		this.context = context;

		minDist = 10f;
		rangeDist = 20f;

		rand = new Random(System.currentTimeMillis());

		nbIcosahedron = 60;
		nbSameIcosahedron = !isVR ? 10 : 5;
		mScale = new float[nbIcosahedron * nbSameIcosahedron];
		mAngle = new float[nbIcosahedron * nbSameIcosahedron];
		mAngleToAdd = new float[nbIcosahedron * nbSameIcosahedron];
		mTranslateVector = new float[nbIcosahedron * nbSameIcosahedron][3];
		mRotationOrientation = new float[nbIcosahedron * nbSameIcosahedron][3];
		mRotationMatrix = new float[nbIcosahedron * nbSameIcosahedron][16];
		mModelMatrix = new float[nbIcosahedron * nbSameIcosahedron][16];
		mIcoColors = new float[nbIcosahedron * nbSameIcosahedron][4];

		setupIcosahedrons();

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
		for (int i = 0; i < nbIcosahedron * nbSameIcosahedron; i++) {
			float[] mModelMatrix = new float[16];
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, mTranslateVector[i][0], mTranslateVector[i][1], mTranslateVector[i][2]);
			Matrix.setRotateM(mRotationMatrix[i], 0, mAngle[i] += mAngleToAdd[i], mRotationOrientation[i][0], mRotationOrientation[i][1], mRotationOrientation[i][2]);
			float[] tmpMat = mModelMatrix.clone();
			Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, mRotationMatrix[i], 0);
			int tmpFreqIndex = i / nbSameIcosahedron;
			float scale = mScale[i];
			float tmp = freqArray[tmpFreqIndex];// + freqArray[tmpFreqIndex] * tmpFreqIndex * mFreqAugmentation;
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
	public float[] getInitCamLookDirVec() {
		return new float[]{0f, 0f, 1f};
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		float[] tmpModelViewMatrix = new float[16];
		float[] tmpModelViewProjectionMatrix = new float[16];
		for (int i = 0; i < this.nbIcosahedron * this.nbSameIcosahedron; i++) {
			Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mModelMatrix[i], 0);
			Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
			icosahedron.setDiffColor(mIcoColors[i]);
			icosahedron.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace, mCameraPosition);
		}
	}

	@Override
	public String getName() {
		return "Icosahedron";
	}

	private void setupIcosahedrons() {
		icosahedron = new ObjSpecVBO(context, "obj/icosahedron/icosahedron_obj.obj", LIGHTAUGMENTATION, DISTANCECOEFF);
		for (int i = 0; i < nbIcosahedron * nbSameIcosahedron; i++) {

			mScale[i] = (nbIcosahedron - i / nbSameIcosahedron) * 0.005f + 0.5f;

			float maxRange = rand.nextFloat() * rangeDist + minDist;
			double phi;
			double theta;
			phi = rand.nextDouble() * Math.PI * 2;
			theta = rand.nextDouble() * Math.PI * 2;
			float x = maxRange * (float) (Math.cos(phi) * Math.sin(theta));
			float y = maxRange * (float) Math.sin(phi);
			float z = maxRange * (float) (Math.cos(phi) * Math.cos(theta));
			mTranslateVector[i][0] = x;
			mTranslateVector[i][1] = y;
			mTranslateVector[i][2] = z;

			mIcoColors[i][0] = rand.nextFloat();
			mIcoColors[i][1] = rand.nextFloat();
			mIcoColors[i][2] = rand.nextFloat();
			mIcoColors[i][3] = 1f;

			mAngle[i] = rand.nextFloat() * 360f;
			mAngleToAdd[i] = 1f + rand.nextFloat() * 3f;
			mRotationOrientation[i][0] = rand.nextFloat() * 2f - 1f;
			mRotationOrientation[i][1] = rand.nextFloat() * 2f - 1f;
			mRotationOrientation[i][2] = rand.nextFloat() * 2f - 1f;
		}
	}
}
