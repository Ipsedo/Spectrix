package com.samuelberrien.spectrix.obj.visualization;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.obj.ObjModelMtlTestSpecular;

/**
 * Created by samuel on 21/02/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class TestSpecular {

    private ObjModelMtlTestSpecular ping;
    private float mPingAngle;
    private float[] mPingTranslateVector;
    private float[] mPingRotationMatrix;
    private float[] mPingModelMatrix;

    public TestSpecular(Context context){
        this.ping = new ObjModelMtlTestSpecular(context, R.raw.snow_tree_obj, R.raw.snow_tree_mtl, 10f, 0.01f);
        this.mPingAngle = 0f;
        this.mPingTranslateVector = new float[]{0f, 0f, 5f};
        this.mPingRotationMatrix = new float[16];
        this.mPingModelMatrix = new float[16];
    }

    public void update(){
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mPingTranslateVector[0], this.mPingTranslateVector[1], this.mPingTranslateVector[2]);


        Matrix.setRotateM(mPingRotationMatrix, 0, mPingAngle += 1f, 0f, 1f, 0f);

        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, mPingRotationMatrix, 0);

        Matrix.scaleM(mModelMatrix, 0, 0.5f, 0.5f, 0.5f);

        this.mPingModelMatrix = mModelMatrix.clone();
    }

    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition){
        float[] tmpMVMatrix = new float[16];
        float[] tmpMVPMatrix = new float[16];
        Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, this.mPingModelMatrix, 0);
        Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
        this.ping.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace, mCameraPosition);
    }
}
