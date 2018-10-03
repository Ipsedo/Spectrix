package com.samuelberrien.spectrix.visualizations.spectrum;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.drawable.obj.ObjVBO;
import com.samuelberrien.spectrix.utils.core.Visualization;

import java.util.Random;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Created by samuel on 23/08/17.
 */

public class Spectrum implements Visualization {

	private static int nbSquare = 256;

	private boolean isInit;

	private float[][] mSquaresPortraitModelMatrix;
	private float[][] mSquaresPortraitPosition;
	private float mSquaresPortraitHeight;

	private float mSquaresLandWidth;
	private float[][] mSquaresLandPosition;
	private float[][] mSquaresLandModelMatrix;

	private ObjVBO cubeVR;
	private float[][] mCubeRotations;
	private float[] mCubePosition;
	private float[][] mCubeModelMatrix;
	private float[][] mCubeColors;

	private Square square;

	private Context context;

	private boolean isVR;

	public Spectrum() {
		isInit = false;
	}

	@Override
	public void init(Context context, boolean isVR) {
		square = new Square();

		this.isVR = isVR;

		mSquaresPortraitModelMatrix = new float[nbSquare][16];
		mSquaresPortraitPosition = new float[nbSquare][3];
		mSquaresPortraitHeight = 1f / (float) nbSquare;

		if (!isVR) {
			for (int i = 0; i < nbSquare; i++) {
				mSquaresPortraitPosition[i][0] = 0f;
				mSquaresPortraitPosition[i][1] = -1f + mSquaresPortraitHeight + mSquaresPortraitHeight * i * 2f;
				mSquaresPortraitPosition[i][2] = 0f;
			}

			mSquaresLandWidth = 1f / (nbSquare * 2f);
			mSquaresLandModelMatrix = new float[nbSquare * 2][16];
			mSquaresLandPosition = new float[nbSquare * 2][3];

			for (int i = 0; i < nbSquare; i++) {
				mSquaresLandPosition[i + nbSquare][0] = mSquaresLandWidth * i * 4f;
				mSquaresLandPosition[i + nbSquare][1] = 0f;
				mSquaresLandPosition[i + nbSquare][2] = 0f;

				mSquaresLandPosition[nbSquare - 1 - i][0] = -mSquaresLandWidth * i * 4f;
				mSquaresLandPosition[nbSquare - 1 - i][1] = 0f;
				mSquaresLandPosition[nbSquare - 1 - i][2] = 0f;
			}
		} else {
			mCubeRotations = new float[nbSquare * 2][16];
			mCubeModelMatrix = new float[nbSquare * 2][16];
			mCubePosition = new float[]{0.f, 0.f, 5.f};
			mCubeColors = new float[nbSquare * 2][4];

			Random r = new Random(System.currentTimeMillis());

			cubeVR = new ObjVBO(context, "obj/cube.obj", 1.f, 1.f, 1.f, 1.f, 0.f);
			for (int i = 0; i < nbSquare; i++) {

				mCubeColors[nbSquare - 1 - i] = new float[]{r.nextFloat(), r.nextFloat(), r.nextFloat(), 1.f};
				mCubeColors[i + nbSquare] = mCubeColors[nbSquare - 1 - i];

				float angle = 180.f * (float) i / (float) nbSquare;
				Matrix.setRotateM(mCubeRotations[nbSquare + i], 0, angle, 0.f, 1.f, 0.f);
				Matrix.setRotateM(mCubeRotations[nbSquare - 1 - i], 0, -angle, 0.f, 1.f, 0.f);
			}
		}

		this.context = context;

		isInit = true;
	}

	@Override
	public boolean isInit() {
		return isInit;
	}

	@Override
	public boolean is3D() {
		return isVR;
	}

	@Override
	public void update(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		if (!isVR && context.getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
			for (int i = 0; i < nbSquare; i++) {
				Matrix.setIdentityM(mModelMatrix, 0);
				Matrix.translateM(mModelMatrix, 0,
						mSquaresPortraitPosition[i][0],
						mSquaresPortraitPosition[i][1],
						mSquaresPortraitPosition[i][2]);
				Matrix.scaleM(mModelMatrix, 0,
						freqArray[i],
						mSquaresPortraitHeight,
						1f);
				mSquaresPortraitModelMatrix[i] = mModelMatrix.clone();
			}
		} else if (!isVR && context.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
			float scale = 1f;
			for (int i = 0; i < nbSquare; i++) {
				Matrix.setIdentityM(mModelMatrix, 0);
				Matrix.translateM(mModelMatrix, 0,
						mSquaresLandPosition[i + nbSquare][0] * scale,
						mSquaresLandPosition[i + nbSquare][1] * scale,
						mSquaresLandPosition[i + nbSquare][2] + (isVR ? 10f : 0f));
				Matrix.scaleM(mModelMatrix, 0,
						mSquaresLandWidth * scale * 2f,
						freqArray[i] * scale,
						scale);
				mSquaresLandModelMatrix[i + nbSquare] = mModelMatrix.clone();
				Matrix.setIdentityM(mModelMatrix, 0);
				Matrix.translateM(mModelMatrix, 0,
						mSquaresLandPosition[nbSquare - 1 - i][0] * scale,
						mSquaresLandPosition[nbSquare - 1 - i][1] * scale,
						mSquaresLandPosition[nbSquare - 1 - i][2] + (isVR ? 10f : 0f));
				Matrix.scaleM(mModelMatrix, 0,
						mSquaresLandWidth * scale * 2f,
						freqArray[i] * scale,
						scale);
				mSquaresLandModelMatrix[nbSquare - 1 - i] = mModelMatrix.clone();
			}
		} else if (isVR) {
			float scaleH = 3.f;
			float scale = 0.01f;

			for (int i = 0; i < nbSquare; i++) {
				int idx1 = i + nbSquare;
				int idx2 = nbSquare - 1 - i;

				// fst part
				Matrix.setIdentityM(mModelMatrix, 0);
				Matrix.translateM(mModelMatrix, 0, mCubePosition[0], mCubePosition[1], mCubePosition[2]);

				Matrix.multiplyMM(mModelMatrix, 0, mCubeRotations[idx1], 0, mModelMatrix.clone(), 0);

				Matrix.scaleM(mModelMatrix, 0, scale, scaleH * freqArray[i], scale);

				mCubeModelMatrix[idx1] = mModelMatrix.clone();

				// snd part
				Matrix.setIdentityM(mModelMatrix, 0);
				Matrix.translateM(mModelMatrix, 0, mCubePosition[0], mCubePosition[1], mCubePosition[2]);

				Matrix.multiplyMM(mModelMatrix, 0, mCubeRotations[idx2], 0, mModelMatrix.clone(), 0);

				Matrix.scaleM(mModelMatrix, 0, scale, scaleH * freqArray[i], scale);

				mCubeModelMatrix[idx2] = mModelMatrix.clone();
			}
		}
	}

	@Override
	public float[] getCameraPosition() {
		return isVR ? new float[]{0.f, 1.f, 0.f} : new float[]{0f, 0f, -3f};
	}

	@Override
	public float[] getLightPosition() {
		return new float[3];
	}

	@Override
	public float[] getInitCamLookDirVec() {
		return isVR ? new float[]{0f, 0f, 1f} : new float[]{0f, 0f, 3f};
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] cameraPos) {
		float[] mMVPMatrix = new float[16];
		if (!isVR && context.getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
			for (int i = 0; i < nbSquare; i++) {
				Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mSquaresPortraitModelMatrix[i].clone(), 0);
				Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix.clone(), 0);
				square.draw(mMVPMatrix);
			}
		} else if (!isVR && context.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
			for (int i = 0; i < nbSquare * 2; i++) {
				Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mSquaresLandModelMatrix[i].clone(), 0);
				Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix.clone(), 0);
				square.draw(mMVPMatrix);
			}
		} else if (isVR) {
			float[] mMVMatrix = new float[16];
			for (int i = 0; i < nbSquare * 2; i++) {
				Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mCubeModelMatrix[i].clone(), 0);
				Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);

				cubeVR.setColor(mCubeColors[i]);
				cubeVR.draw(mMVPMatrix, mMVMatrix, mLightPosInEyeSpace);
			}
		}
	}

	@Override
	public String getName() {
		return "Spectrum";
	}
}
