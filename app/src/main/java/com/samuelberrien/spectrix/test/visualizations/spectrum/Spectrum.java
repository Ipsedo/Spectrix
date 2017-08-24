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

	private boolean isPortrait;

	private float[][] mSquaresModelMatrix;
	private float[][] mSquaresPosition;
	private float mSquaresHeight;

	private float hightFreqsAugmentation = 30f;

	private Square square;

	public Spectrum() {
		isInit = false;
	}

	@Override
	public void init(Context context) {
		square = new Square();

		isPortrait = context.getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT;

		if (isPortrait) {
			mSquaresModelMatrix = new float[nbSquare][16];
			mSquaresPosition = new float[nbSquare][3];
			mSquaresHeight = 1f / (float) nbSquare;

			for (int i = 0; i < nbSquare; i++) {
				mSquaresPosition[i][0] = 0f;
				mSquaresPosition[i][1] = -1f + mSquaresHeight + mSquaresHeight * i * 2f;
				mSquaresPosition[i][2] = 0f;
			}
		}

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
		if (isPortrait) {
			for (int i = 0; i < nbSquare; i++) {
				Matrix.setIdentityM(mModelMatrix, 0);
				Matrix.translateM(mModelMatrix, 0, mSquaresPosition[i][0], mSquaresPosition[i][1], mSquaresPosition[i][2]); //0f, (float) i / (float) (nbSquare / 2) - 1f, 0f);
				Matrix.scaleM(mModelMatrix, 0, freqArray[i] + freqArray[i] * (float) i / this.hightFreqsAugmentation, mSquaresHeight, 1f);
				mSquaresModelMatrix[i] = mModelMatrix.clone();
			}
		}
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] unused1, float[] unused2) {
		float[] mMVPMatrix = new float[16];
		if (isPortrait) {
			for (int i = 0; i < nbSquare; i++) {
				Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mSquaresModelMatrix[i].clone(), 0);
				Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix.clone(), 0);
				square.draw(mMVPMatrix);
			}
		}
	}
}
