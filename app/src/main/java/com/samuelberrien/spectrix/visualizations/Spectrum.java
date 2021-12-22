package com.samuelberrien.spectrix.visualizations;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.drawable.ObjVBO;
import com.samuelberrien.spectrix.utils.Visualization;

import java.util.Random;

public class Spectrum implements Visualization {

    private boolean isInit;

    private static final int NbSquare = 256;

    private ObjVBO cubeVR;
    private float[][] mCubeRotations;
    private float[] mCubePosition;
    private float[][] mCubeModelMatrix;
    private float[][] mCubeColors;

    public Spectrum() {
        isInit = false;
    }

    @Override
    public void init(Context context, boolean isVR) {
        mCubeRotations = new float[NbSquare * 2][16];
        mCubeModelMatrix = new float[NbSquare * 2][16];
        mCubePosition = new float[]{0.f, 0.f, 5.f};
        mCubeColors = new float[NbSquare * 2][4];

        Random r = new Random(System.currentTimeMillis());

        cubeVR = new ObjVBO(context, "obj/cube.obj", 1.f, 1.f, 1.f, 1.f, 0.f);
        for (int i = 0; i < NbSquare; i++) {

            mCubeColors[NbSquare - 1 - i] = new float[]{r.nextFloat(), r.nextFloat(), r.nextFloat(), 1.f};
            mCubeColors[i + NbSquare] = mCubeColors[NbSquare - 1 - i];

            float angle = 180.f * (float) i / (float) NbSquare;
            Matrix.setRotateM(mCubeRotations[NbSquare + i], 0, angle, 0.f, 1.f, 0.f);
            Matrix.setRotateM(mCubeRotations[NbSquare - 1 - i], 0, -angle, 0.f, 1.f, 0.f);
        }

        isInit = true;
    }

    @Override
    public void update(float[] freqArray) {
        float[] mModelMatrix = new float[16];

        float scaleH = 1.5f;
        float scale = 0.01f;

        for (int i = 0; i < NbSquare; i++) {
            int idx1 = i + NbSquare;
            int idx2 = NbSquare - 1 - i;

            // fst part
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, mCubePosition[0], mCubePosition[1], mCubePosition[2]);

            Matrix.multiplyMM(mModelMatrix, 0, mCubeRotations[idx1], 0, mModelMatrix.clone(), 0);

            Matrix.scaleM(mModelMatrix, 0, scale, scaleH * freqArray[i], scale);

            mCubeModelMatrix[idx1] = mModelMatrix.clone();

            // snd part
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, mCubePosition[0], mCubePosition[1], mCubePosition[2]);

            Matrix.multiplyMM(mModelMatrix, 0, mCubeRotations[idx2], 0, mModelMatrix.clone(), 0);

            Matrix.scaleM(mModelMatrix, 0, scale, scaleH * freqArray[i], scale);

            mCubeModelMatrix[idx2] = mModelMatrix.clone();
        }
    }

    @Override
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] cameraPos) {
        float[] mMVMatrix = new float[16];
        float[] mMVPMatrix = new float[16];
        for (int i = 0; i < NbSquare * 2; i++) {
            Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mCubeModelMatrix[i].clone(), 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);

            cubeVR.setColor(mCubeColors[i]);
            cubeVR.draw(mMVPMatrix, mMVMatrix, mLightPosInEyeSpace);
        }
    }

    @Override
    public float[] getCameraPosition() {
        return new float[]{0.f, 0.f, 0.f};
    }

    @Override
    public float[] getLightPosition() {
        return new float[3];
    }

    @Override
    public float[] getInitCamLookDirVec() {
        return new float[]{0f, 0f, 1f};
    }

    @Override
    public String getName() {
        return "Spectrum";
    }

    @Override
    public boolean isInit() {
        return isInit;
    }
}
