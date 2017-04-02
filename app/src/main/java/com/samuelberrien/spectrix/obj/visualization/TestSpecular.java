package com.samuelberrien.spectrix.obj.visualization;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.obj.HeightMap;
import com.samuelberrien.spectrix.obj.ObjModelMtlTestSpecular;

/**
 * Created by samuel on 21/02/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class TestSpecular {

    /*private ObjModelMtlTestSpecular ping;
    private float mPingAngle;
    private float[] mPingTranslateVector;
    private float[] mPingRotationMatrix;
    private float[] mPingModelMatrix;*/
    private HeightMap hm;
    private float[] mHmModelMatrix;

    public TestSpecular(Context context) {
        /*this.ping = new ObjModelMtlTestSpecular(context, R.raw.canyon_obj, R.raw.canyon_mtl, 5f, 0.01f);
        this.mPingAngle = 0f;
        this.mPingTranslateVector = new float[]{0f, -2f, 0f};
        this.mPingRotationMatrix = new float[16];
        this.mPingModelMatrix = new float[16];*/
        this.hm = new HeightMap(context, R.drawable.mountains_height_2, R.drawable.mountains_tex_2, 0.1f, 0.8f);
        this.mHmModelMatrix = new float[16];
    }

    public void update() {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, 0f, 0f, 0f);
        Matrix.scaleM(mModelMatrix, 0, 5f, 5f, 5f);
        this.mHmModelMatrix = mModelMatrix.clone();
    }

    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        float[] tmpMVMatrix = new float[16];
        float[] tmpMVPMatrix = new float[16];
        Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, this.mHmModelMatrix, 0);
        Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
        this.hm.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace);
    }
}
