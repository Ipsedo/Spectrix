package com.samuelberrien.spectrix.utils;

import android.content.Context;

/**
 * Created by samuel on 23/08/17.
 */

public interface Visualization {
    void init(Context context, boolean isVR);

    boolean isInit();

    void update(float[] freqArray);

    float[] getCameraPosition();

    float[] getLightPosition();

    float[] getInitCamLookDirVec();

    void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition);

    String getName();
}
