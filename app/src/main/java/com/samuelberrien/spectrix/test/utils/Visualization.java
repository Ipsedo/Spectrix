package com.samuelberrien.spectrix.test.utils;

import android.content.Context;

import com.samuelberrien.spectrix.test.visualizations.icosahedron.Icosahedron;
import com.samuelberrien.spectrix.test.visualizations.spectrum.Spectrum;

/**
 * Created by samuel on 23/08/17.
 */

public interface Visualization {
	void init(Context context, boolean isVR);

	boolean isInit();

	boolean is3D();

	void update(float[] freqArray);

	float[] getCameraPosition();

	void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition);

	String getName();
}
