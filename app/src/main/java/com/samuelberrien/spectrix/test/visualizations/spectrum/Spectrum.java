package com.samuelberrien.spectrix.test.visualizations.spectrum;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.test.utils.Visualization;

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

	private float hightFreqsAugmentation = 30f;

	private Square square;

	private Context context;

	public Spectrum() {
		isInit = false;
	}

	@Override
	public void init(Context context) {
		square = new Square();

		mSquaresPortraitModelMatrix = new float[nbSquare][16];
		mSquaresPortraitPosition = new float[nbSquare][3];
		mSquaresPortraitHeight = 1f / (float) nbSquare;

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

		this.context = context;

		isInit = true;
	}

	@Override
	public boolean isInit() {
		return isInit;
	}

	@Override
	public boolean is3D() {
		return false;
	}

	@Override
	public void update(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		if (context.getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
			for (int i = 0; i < nbSquare; i++) {
				Matrix.setIdentityM(mModelMatrix, 0);
				Matrix.translateM(mModelMatrix, 0, mSquaresPortraitPosition[i][0], mSquaresPortraitPosition[i][1], mSquaresPortraitPosition[i][2]); //0f, (float) i / (float) (nbSquare / 2) - 1f, 0f);
				Matrix.scaleM(mModelMatrix, 0, freqArray[i] + freqArray[i] * (float) i / this.hightFreqsAugmentation, mSquaresPortraitHeight, 1f);
				mSquaresPortraitModelMatrix[i] = mModelMatrix.clone();
			}
		} else {
			for (int i = 0; i < nbSquare; i++) {
				Matrix.setIdentityM(mModelMatrix, 0);
				Matrix.translateM(mModelMatrix, 0, mSquaresLandPosition[i + nbSquare][0], mSquaresLandPosition[i + nbSquare][1], mSquaresLandPosition[i + nbSquare][2]);
				Matrix.scaleM(mModelMatrix, 0, mSquaresLandWidth, freqArray[i] + freqArray[i] * (float) i / this.hightFreqsAugmentation, 1f);
				mSquaresLandModelMatrix[i + nbSquare] = mModelMatrix.clone();
				Matrix.setIdentityM(mModelMatrix, 0);
				Matrix.translateM(mModelMatrix, 0, mSquaresLandPosition[nbSquare - 1 - i][0], mSquaresLandPosition[nbSquare - 1 - i][1], mSquaresLandPosition[nbSquare - 1 - i][2]);
				Matrix.scaleM(mModelMatrix, 0, mSquaresLandWidth, freqArray[i] + freqArray[i] * (float) i / this.hightFreqsAugmentation, 1f);
				mSquaresLandModelMatrix[nbSquare - 1 - i] = mModelMatrix.clone();
			}
		}
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] unused1, float[] unused2) {
		float[] mMVPMatrix = new float[16];
		if (context.getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
			for (int i = 0; i < nbSquare; i++) {
				Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mSquaresPortraitModelMatrix[i].clone(), 0);
				Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix.clone(), 0);
				square.draw(mMVPMatrix);
			}
		} else {
			for (int i = 0; i < nbSquare * 2; i++) {
				Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mSquaresLandModelMatrix[i].clone(), 0);
				Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix.clone(), 0);
				square.draw(mMVPMatrix);
			}
		}
	}

	@Override
	public String getName() {
		return "Spectrum";
	}
}
