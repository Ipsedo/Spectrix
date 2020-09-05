package com.samuelberrien.spectrix.visualizations;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.drawable.ObjMtlVBO;
import com.samuelberrien.spectrix.drawable.ObjVBO;
import com.samuelberrien.spectrix.utils.Visualization;

import java.util.Random;

/**
 * Created by samuel on 30/08/17.
 */

public class Snow implements Visualization {

    private boolean isInit;

    private Context context;

    private Random rand;

    private final int maxFreq = 256;

    private final int MAXCPT = 16;
    private int cpt = MAXCPT;

    private final float LIGHTAUGMENTATION = 2.0f;
    private final float DISTANCECOEFF = 0.001f;

    private final float SCALE = 0.2f;

    private int nbPing;
    private ObjMtlVBO ping;
    private float[] mPingAngle;
    private float[][] mPingTranslateVector;
    private float[][] mPingRotationMatrix;
    private float[][] mPingModelMatrix;
    private float mPingFreqAttenuation = 0.7f;

    private int nbOctagone;
    private ObjVBO octagone;
    private float[][] mOctagoneColors;
    private float[] mOctagoneAngle;
    private float[][] mOctagoneRotationOrientation;
    private float[] mOctagoneScale;
    private float[][] mOctagoneTranslateVector;
    private float[][] mOctagoneRotationMatrix;
    private float[][] mOctagoneModelMatrix;
    private float mOctagoneFreqAttenuation = 0.001f;

    private int nbIgloo;
    private ObjMtlVBO igloo;
    private float[] mIglooAngle;
    private float[][] mIglooTranslateVector;
    private float[][] mIglooRotationMatrix;
    private float[][] mIglooModelMatrix;
    private float mIglooFreqAttenuation = 0.03f;

    private int nbTree;
    private ObjMtlVBO tree;
    private float[][] mTreeTranslateVector;
    private float[] mTreeScale;
    private float[][] mTreeModelMatrix;
    private float mTreeFreqAttenuation = 0.3f;

    private ObjMtlVBO whale;
    private float mWhaleAngle;
    private float[] mWhaleTranslateVector;
    private float[] mWhaleRotationMatrix;
    private float[] mWhaleModelMatrix;
    private float mWhaleFreqAttenuation = 0.07f;

    private ObjMtlVBO mountains;
    private float[] mMountainsTranslateVector = new float[3];
    private float[] mMountainsModelMatrix = new float[16];

    public Snow() {
        isInit = false;
    }

    @Override
    public void init(Context context, boolean isVR) {
        this.context = context;

        rand = new Random(System.currentTimeMillis());

        nbPing = 100;
        mPingAngle = new float[nbPing];
        mPingTranslateVector = new float[nbPing][3];
        mPingRotationMatrix = new float[nbPing][16];
        mPingModelMatrix = new float[nbPing][16];
        setupPinguin();

        nbOctagone = 100;
        mOctagoneColors = new float[nbOctagone][4];
        mOctagoneAngle = new float[nbOctagone];
        mOctagoneRotationOrientation = new float[nbOctagone][3];
        mOctagoneScale = new float[nbOctagone];
        mOctagoneTranslateVector = new float[nbOctagone][3];
        mOctagoneRotationMatrix = new float[nbOctagone][16];
        mOctagoneModelMatrix = new float[nbOctagone][16];
        setupOctagone();

        nbIgloo = 3;
        mIglooAngle = new float[nbIgloo];
        mIglooTranslateVector = new float[nbIgloo][3];
        mIglooRotationMatrix = new float[nbIgloo][16];
        mIglooModelMatrix = new float[nbIgloo][16];
        setupIgloo();

        nbTree = 30;
        mTreeTranslateVector = new float[nbTree][3];
        mTreeScale = new float[nbTree];
        mTreeModelMatrix = new float[nbTree][16];
        setupTree();

        mWhaleAngle = 0f;
        mWhaleTranslateVector = new float[3];
        mWhaleRotationMatrix = new float[16];
        mWhaleModelMatrix = new float[16];
        setupWhale();

        mMountainsTranslateVector = new float[3];
        mMountainsModelMatrix = new float[16];
        setupMountains();

        isInit = true;
    }

    @Override
    public boolean isInit() {
        return isInit;
    }

    @Override
    public boolean is3D() {
        return true;
    }

    @Override
    public void update(float[] freqArray) {
        updatePing(freqArray);
        updateIgloo(freqArray);
        updateWhale(freqArray);
        updateOctagone(freqArray);
        updateMountains();
        updateTree(freqArray);
        count();
    }

    @Override
    public float[] getCameraPosition() {
        return new float[]{0f, 1.5f, 0f};
    }

    @Override
    public float[] getLightPosition() {
        return new float[]{0f, 9f, 0f};
    }

    @Override
    public float[] getInitCamLookDirVec() {
        return new float[]{0f, 0f, 1f};
    }

    @Override
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
        float[] tmpMVMatrix = new float[16];
        float[] tmpMVPMatrix = new float[16];

        for (int i = 0; i < nbPing; i++) {
            Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, mPingModelMatrix[i], 0);
            Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
            ping.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace, mCameraPosition);
        }

        for (int i = 0; i < nbIgloo; i++) {
            Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, mIglooModelMatrix[i], 0);
            Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
            igloo.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace, mCameraPosition);
        }

        Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, mWhaleModelMatrix, 0);
        Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
        whale.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace, mCameraPosition);

        for (int i = 0; i < nbOctagone; i++) {
            Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, mOctagoneModelMatrix[i], 0);
            Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
            octagone.setColor(mOctagoneColors[i]);
            octagone.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace);
        }

        for (int i = 0; i < nbTree; i++) {
            Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, mTreeModelMatrix[i], 0);
            Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
            tree.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace, mCameraPosition);
        }

        Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, mMountainsModelMatrix, 0);
        Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
        mountains.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace, mCameraPosition);
    }

    @Override
    public String getName() {
        return "Snow";
    }

    /**
     *
     */
    private void setupTree() {
        tree = new ObjMtlVBO(context, "obj/snow/snow_tree_obj.obj", "obj/snow/snow_tree_mtl.mtl", LIGHTAUGMENTATION, DISTANCECOEFF, false);
        for (int i = 0; i < nbTree; i++) {
            double r = 5d * rand.nextDouble(), theta = rand.nextDouble() * Math.PI * 2d;
            float up = rand.nextFloat() * 0.5f;

            mTreeTranslateVector[i][0] = (float) ((15d + r) * Math.cos(theta));
            mTreeTranslateVector[i][1] = 1f + up;
            mTreeTranslateVector[i][2] = (float) ((15d + r) * Math.sin(theta));

            mTreeScale[i] = 1f + up;
        }
    }

    /**
     *
     */
    private void setupPinguin() {
        ping = new ObjMtlVBO(context, "obj/snow/snow_pingouin_obj.obj", "obj/snow/snow_pingouin_mtl.mtl", LIGHTAUGMENTATION, DISTANCECOEFF, false);
        for (int i = 0; i < nbPing; i++) {
            double r = 5d * rand.nextDouble(), theta = rand.nextDouble() * Math.PI * 2d;

            mPingTranslateVector[i][0] = (float) ((5d + r) * Math.cos(theta));
            mPingTranslateVector[i][1] = 0f;
            mPingTranslateVector[i][2] = (float) ((5d + r) * Math.sin(theta));
            mPingAngle[i] = rand.nextFloat() * 360f;
        }
    }

    /**
     *
     */
    private void setupOctagone() {
        octagone = new ObjVBO(context, "obj/octagone.obj", 1f, 0.8f, 0.8f, LIGHTAUGMENTATION, DISTANCECOEFF);
        for (int i = 0; i < nbOctagone; i++) {
            double theta, phi, r1 = 20d, r2 = rand.nextDouble() * 5d;

            mOctagoneColors[i][0] = rand.nextFloat();
            mOctagoneColors[i][1] = rand.nextFloat();
            mOctagoneColors[i][2] = rand.nextFloat();
            mOctagoneColors[i][3] = 1f;

            theta = rand.nextDouble() * Math.PI * 2d;
            phi = rand.nextDouble() * Math.PI * 6 / 8 + Math.PI / 8;
            mOctagoneAngle[i] = (float) (rand.nextDouble() * 360f);

            mOctagoneRotationOrientation[i][0] = rand.nextFloat() * 2f - 1f;
            mOctagoneRotationOrientation[i][1] = rand.nextFloat() * 2f - 1f;
            mOctagoneRotationOrientation[i][2] = rand.nextFloat() * 2f - 1f;

            mOctagoneTranslateVector[i][0] = (float) ((r1 + r2) * Math.cos(phi) * Math.sin(theta));
            mOctagoneTranslateVector[i][1] = (float) ((r1 + r2) * Math.sin(phi));
            mOctagoneTranslateVector[i][2] = (float) ((r1 + r2) * Math.cos(phi) * Math.cos(theta));

            mOctagoneScale[i] = i / nbOctagone + 0.4f;
        }
    }

    /**
     *
     */
    private void setupIgloo() {
        igloo = new ObjMtlVBO(context, "obj/snow/snow_igloo_obj.obj", "obj/snow/snow_igloo_mtl.mtl", LIGHTAUGMENTATION, DISTANCECOEFF, false);
        for (int i = 0; i < nbIgloo; i++) {
            double r = 5d * rand.nextDouble(), theta = rand.nextDouble() * Math.PI * 2d;
            mIglooTranslateVector[i][0] = (float) ((5d + r) * Math.cos(theta));
            mIglooTranslateVector[i][1] = -1f;
            mIglooTranslateVector[i][2] = (float) ((5d + r) * Math.sin(theta));
            mIglooAngle[i] = rand.nextFloat() * 360f;
        }
    }

    /**
     *
     */
    private void setupWhale() {
        mWhaleTranslateVector[0] = 0f;
        mWhaleTranslateVector[1] = 3f;
        mWhaleTranslateVector[2] = 10f;
        mWhaleAngle = 0f;
        whale = new ObjMtlVBO(context, "obj/snow/snow_baleine_obj.obj", "obj/snow/snow_baleine_mtl.mtl", LIGHTAUGMENTATION, DISTANCECOEFF, true);
    }

    /**
     *
     */
    private void setupMountains() {
        mMountainsTranslateVector[0] = 0f;
        mMountainsTranslateVector[1] = -3f;
        mMountainsTranslateVector[2] = 0f;
        mountains = new ObjMtlVBO(context, "obj/snow/snow_mountains_obj.obj", "obj/snow/snow_mountains_mtl.mtl", LIGHTAUGMENTATION, DISTANCECOEFF, false);
    }

    /**
     * @param freqArray
     */
    private void updateTree(float[] freqArray) {
        for (int i = 0; i < nbTree; i++) {
            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, mTreeTranslateVector[i][0], mTreeTranslateVector[i][1], mTreeTranslateVector[i][2]);
            float max = 0f;
            for (int j = i * 4; j < (i + 1) * 4; j++) {
                if (max < freqArray[j]) {
                    max = freqArray[j];
                }
            }
            float scale = mTreeScale[i] + max * mTreeScale[i] + max * i * mTreeFreqAttenuation * mTreeScale[i];
            Matrix.scaleM(mModelMatrix, 0, mTreeScale[i], scale, mTreeScale[i]);
            mTreeModelMatrix[i] = mModelMatrix.clone();
        }
    }

    /**
     * @param freqArray
     */
    private void updatePing(float[] freqArray) {
        for (int i = 0; i < nbPing; i++) {
            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);

            Matrix.translateM(mModelMatrix, 0, mPingTranslateVector[i][0], mPingTranslateVector[i][1], mPingTranslateVector[i][2]);

            float sum = 0f;
            for (int j = maxFreq * i / nbPing; j < maxFreq * (i + 1) / nbPing; j++) {
                sum += freqArray[i];
            }
            sum = sum <= 10f ? sum : 10f;
            if (cpt % 2 == i % 2) {
                mPingAngle[i] += sum + sum * i * mPingFreqAttenuation;
            } else {
                mPingAngle[i] -= sum + sum * i * mPingFreqAttenuation;
            }
            Matrix.setRotateM(mPingRotationMatrix[i], 0, mPingAngle[i], 0f, 1f, 0f);

            float[] tmpMat = mModelMatrix.clone();
            Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, mPingRotationMatrix[i], 0);

            Matrix.scaleM(mModelMatrix, 0, SCALE, SCALE, SCALE);

            mPingModelMatrix[i] = mModelMatrix.clone();
        }
    }

    /**
     * @param freqArray
     */
    private void updateOctagone(float[] freqArray) {
        for (int i = 0; i < nbOctagone; i++) {
            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);

            Matrix.translateM(mModelMatrix, 0, mOctagoneTranslateVector[i][0], mOctagoneTranslateVector[i][1], mOctagoneTranslateVector[i][2]);

            Matrix.setRotateM(mOctagoneRotationMatrix[i], 0, mOctagoneAngle[i] += 1f, mOctagoneRotationOrientation[i][0], mOctagoneRotationOrientation[i][1], mOctagoneRotationOrientation[i][2]);

            float[] tmpMat = mModelMatrix.clone();
            Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, mOctagoneRotationMatrix[i], 0);

            float sum = 0f;
            for (int j = maxFreq * i / nbOctagone; j < maxFreq * (i + 1) / nbOctagone; j++) {
                sum += freqArray[i];
            }
            float scale = mOctagoneScale[i] + sum * mOctagoneScale[i] + sum * i * mOctagoneFreqAttenuation * mOctagoneScale[i];
            scale = scale > 2 * mOctagoneScale[i] ? 2 * mOctagoneScale[i] : scale;
            Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

            mOctagoneModelMatrix[i] = mModelMatrix.clone();
        }
    }

    /**
     * @param freqArray
     */
    private void updateIgloo(float[] freqArray) {
        for (int i = 0; i < nbIgloo; i++) {
            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, mIglooTranslateVector[i][0], mIglooTranslateVector[i][1], mIglooTranslateVector[i][2]);
            Matrix.setRotateM(mIglooRotationMatrix[i], 0, mIglooAngle[i], 0, 1f, 0f);
            float[] tmpMat = mModelMatrix.clone();
            Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, mIglooRotationMatrix[i], 0);
            float scale = SCALE + freqArray[i] * mIglooFreqAttenuation;
            scale = scale <= 2f ? scale : 2f;
            Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);
            mIglooModelMatrix[i] = mModelMatrix.clone();
        }
    }

    /**
     * @param freqArray
     */
    private void updateWhale(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, mWhaleTranslateVector[0], mWhaleTranslateVector[1], mWhaleTranslateVector[2]);

        Matrix.setRotateM(mWhaleRotationMatrix, 0, mWhaleAngle += 0.5f, 0f, 1f, 0f);

        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, mWhaleRotationMatrix, 0, tmpMat, 0);

        float scale;
        if (freqArray[3] > 0.2 && cpt % 2 == 0) {
            scale = SCALE + SCALE * mWhaleFreqAttenuation;//* freqArray[3];
        } else {
            scale = SCALE;
        }
        Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

        mWhaleModelMatrix = mModelMatrix.clone();
    }

    /**
     *
     */
    private void updateMountains() {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        float scale = SCALE;
        Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

        Matrix.translateM(mModelMatrix, 0, mMountainsTranslateVector[0], mMountainsTranslateVector[1], mMountainsTranslateVector[2]);

        mMountainsModelMatrix = mModelMatrix.clone();
    }

    private void count() {
        cpt--;
        if (cpt <= 0) {
            cpt = MAXCPT;
        }
    }
}
