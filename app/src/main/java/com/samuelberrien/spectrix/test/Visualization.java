package com.samuelberrien.spectrix.test;

import android.content.Context;

/**
 * Created by samuel on 23/08/17.
 */

public interface Visualization {

	void init(Context context);

	boolean isInit();

	boolean is3D();

	void update(float[] freqArray);

	void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition);
}
