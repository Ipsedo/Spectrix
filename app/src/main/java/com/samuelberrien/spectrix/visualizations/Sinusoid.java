package com.samuelberrien.spectrix.visualizations;

import android.content.Context;

import com.samuelberrien.spectrix.utils.Visualization;

public class Sinusoid implements Visualization {
    @Override
    public void init(Context context, boolean isVR) {

    }

    @Override
    public boolean isInit() {
        return false;
    }

    @Override
    public void update(float[] magnArray, float[] phaseArray) {

    }

    @Override
    public float[] getCameraPosition() {
        return new float[0];
    }

    @Override
    public float[] getLightPosition() {
        return new float[0];
    }

    @Override
    public float[] getInitCamLookDirVec() {
        return new float[0];
    }

    @Override
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {

    }

    @Override
    public String getName() {
        return null;
    }
}
