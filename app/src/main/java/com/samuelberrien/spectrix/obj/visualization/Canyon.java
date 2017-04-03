package com.samuelberrien.spectrix.obj.visualization;

import android.content.Context;
import android.media.audiofx.Visualizer;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.obj.HeightMap;

/**
 * Created by samuel on 03/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Canyon {

    private float[] freqArray = new float[Visualizer.getCaptureSizeRange()[1]];

    private HeightMap canyon;
    private float mCanyonScale;
    private float[] mCanyonModelMatrix;

    public Canyon(Context context, float mCanyonScale){
        this.canyon = new HeightMap(context, R.drawable.canyon_6_hm_2, R.drawable.canyon_6_tex_2, 0.05f, 0.8f, 3e-5f);
        this.mCanyonModelMatrix = new float[16];
        this.mCanyonScale = mCanyonScale;
    }

    private void updateMap() {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, -0.5f * this.mCanyonScale, -0.5f, -0.5f * this.mCanyonScale);
        Matrix.scaleM(mModelMatrix, 0, this.mCanyonScale, this.mCanyonScale, this.mCanyonScale);
        this.mCanyonModelMatrix = mModelMatrix.clone();
    }

    public void updateCanyon(){
        this.updateMap();
    }

    public void update(float[] freqArray){
        this.freqArray = freqArray;
    }

    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        float[] tmpMVMatrix = new float[16];
        float[] tmpMVPMatrix = new float[16];
        Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, this.mCanyonModelMatrix, 0);
        Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
        this.canyon.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace);
    }
}
