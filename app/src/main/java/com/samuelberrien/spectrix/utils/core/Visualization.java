package com.samuelberrien.spectrix.utils.core;

import android.content.Context;

/**
 * Created by samuel on 23/08/17.
 */

public interface Visualization {
	void init(Context context, boolean isVR);

	boolean isInit();

	boolean is3D();

	void update(float[] freqArray);

	float[] getCameraPosition();

	float[] getInitCamLookDirVec();

	void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition);

	String getName();
}
