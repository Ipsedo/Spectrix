package com.samuelberrien.spectrix.obj.visualization;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.obj.ObjModel;
import com.samuelberrien.spectrix.obj.ObjModelMtl;

import java.nio.FloatBuffer;
import java.util.Random;

/**
 * Created by samuel on 25/01/17.
 */

public class Snow {

    Context context;

    Random rand;

    private final int MAXCPT = 16;
    private int cpt = MAXCPT;

    private final float LIGHTAUGMENTATION = 2.0f;
    private final float DISTANCECOEFF = 0.001f;

    private final float SCALE = 0.2f;

    private int nbPing;
    private ObjModelMtl ping;
    private float[] mPingAngle;
    private float[][] mPingTranslateVector;
    private float[][] mPingRotationMatrix;
    private float[][] mPingModelMatrix;
    private float mPingFreqAttenuation = 0.7f;

    private int nbOctagone;
    private ObjModel octagone;
    private float[] mOctagoneAngle;
    private float[][] mOctagoneRotationOrientation;
    private float[] mOctagoneScale;
    private float[][] mOctagoneTranslateVector;
    private float[][] mOctagoneRotationMatrix;
    private float[][] mOctagoneModelMatrix;
    private float mOctagoneFreqAttenuation = 0.001f;

    private int nbIgloo;
    private ObjModelMtl igloo;
    private float[] mIglooAngle;
    private float[][] mIglooTranslateVector;
    private float[][] mIglooRotationMatrix;
    private float[][] mIglooModelMatrix;
    private float mIglooFreqAttenuation = 0.03f;

    private int nbTree;
    private ObjModelMtl tree;
    private float[][] mTreeTranslateVector;
    private float[] mTreeScale;
    private float[][] mTreeModelMatrix;
    private float mTreeFreqAttenuation = 0.3f;

    private ObjModelMtl whale;
    private float mWhaleAngle;
    private float[] mWhaleTranslateVector;
    private float[] mWhaleRotationMatrix;
    private float[] mWhaleModelMatrix;
    private float mWhaleFreqAttenuation = 0.07f;

    private ObjModelMtl mountains;
    private float[] mMountainsTranslateVector = new float[3];
    private float[] mMountainsModelMatrix = new float[16];

    /**
     *
     * @param context
     * @param nbPing
     * @param nbOctagone
     * @param nbIgloo
     * @param nbTree
     */
    public Snow(Context context, int nbPing, int nbOctagone, int nbIgloo, int nbTree){
        this.context = context;

        this.rand = new Random(System.currentTimeMillis());

        this.nbPing = nbPing;
        this.mPingAngle = new float[this.nbPing];
        this.mPingTranslateVector = new float[this.nbPing][3];
        this.mPingRotationMatrix = new float[this.nbPing][16];
        this.mPingModelMatrix = new float[this.nbPing][16];
        this.setupPinguin();

        this.nbOctagone = nbOctagone;
        this.mOctagoneAngle = new float[this.nbOctagone];
        this.mOctagoneRotationOrientation = new float[this.nbOctagone][3];
        this.mOctagoneScale = new float[this.nbOctagone];
        this.mOctagoneTranslateVector = new float[this.nbOctagone][3];
        this.mOctagoneRotationMatrix = new float[this.nbOctagone][16];
        this.mOctagoneModelMatrix = new float[this.nbOctagone][16];
        this.setupOctagone();

        this.nbIgloo = nbIgloo;
        this.mIglooAngle = new float[this.nbIgloo];
        this.mIglooTranslateVector = new float[this.nbIgloo][3];
        this.mIglooRotationMatrix = new float[this.nbIgloo][16];
        this.mIglooModelMatrix = new float[this.nbIgloo][16];
        this.setupIgloo();

        this.nbTree = nbTree;
        this.mTreeTranslateVector = new float[nbTree][3];
        this.mTreeScale = new float[this.nbTree];
        this.mTreeModelMatrix = new float[this.nbTree][16];
        this.setupTree();

        this.mWhaleAngle = 0f;
        this.mWhaleTranslateVector = new float[3];
        this.mWhaleRotationMatrix = new float[16];
        this.mWhaleModelMatrix = new float[16];
        this.setupWhale();

        this.mMountainsTranslateVector = new float[3];
        this.mMountainsModelMatrix = new float[16];
        this.setupMountains();
    }

    /**
     *
     */
    private void setupTree(){
        this.tree = new ObjModelMtl(this.context, R.raw.snow_tree_obj, R.raw.snow_tree_mtl, LIGHTAUGMENTATION, DISTANCECOEFF);
        for(int i=0; i < this.nbTree; i++){
            double r = 5d * rand.nextDouble();
            double theta;
            /*if(Math.sin(90) == 1){
                theta = rand.nextDouble() * 360d;
            }else{*/
                theta = rand.nextDouble() * Math.PI * 2d;
            //}

            float up = rand.nextFloat() * 0.5f;

            this.mTreeTranslateVector[i][0] = (float) ((15d + r) * Math.cos(theta));
            this.mTreeTranslateVector[i][1] = 1f + up;
            this.mTreeTranslateVector[i][2] = (float) ((15d + r) * Math.sin(theta));

            this.mTreeScale[i] = 1f + up;
        }
    }

    /**
     *
     */
    private void setupPinguin() {
        this.ping = new ObjModelMtl(this.context, R.raw.snow_pingouin_obj, R.raw.snow_pingouin_mtl, LIGHTAUGMENTATION, DISTANCECOEFF);
        for(int i=0; i < nbPing; i++) {
            double r = 5d * rand.nextDouble();
            double theta;
            /*if(Math.sin(90) == 1){
                theta = rand.nextDouble() * 360d;
            }else{*/
                theta = rand.nextDouble() * Math.PI * 2d;
            //}

            this.mPingTranslateVector[i][0] = (float) ((5d + r) * Math.cos(theta));
            this.mPingTranslateVector[i][1] = 0f;
            this.mPingTranslateVector[i][2] = (float) ((5d + r) * Math.sin(theta));
            this.mPingAngle[i] = rand.nextFloat() * 360f;
        }
    }

    /**
     *
     */
    private void setupOctagone() {
        this.octagone = new ObjModel(this.context, R.raw.octagone, 1f, 0.8f, 0.8f, LIGHTAUGMENTATION, DISTANCECOEFF);
        for(int i=0; i<this.nbOctagone; i++){
            double theta;
            double phi;
            double r1 = 20d;
            double r2 = rand.nextDouble() * 5d;

            /*if(Math.cos(90) == 0){
                theta = rand.nextDouble() * 360f;
                phi = rand.nextDouble() * 135f + 22.5f;
                this.mOctagoneAngle[i] = rand.nextFloat() * 360f;
            }else{*/
                theta = rand.nextDouble() * Math.PI * 2d;
                phi = rand.nextDouble() * Math.PI * 6 / 8 + Math.PI / 8;
                this.mOctagoneAngle[i] = (float) (rand.nextDouble() * 360f);
            //}

            this.mOctagoneRotationOrientation[i][0] = rand.nextFloat() * 2f - 1f;
            this.mOctagoneRotationOrientation[i][1] = rand.nextFloat() * 2f - 1f;
            this.mOctagoneRotationOrientation[i][2] = rand.nextFloat() * 2f - 1f;

            this.mOctagoneTranslateVector[i][0] = (float) ((r1 + r2) * Math.cos(phi) * Math.sin(theta));
            this.mOctagoneTranslateVector[i][1] = (float) ((r1 + r2) * Math.sin(phi));
            this.mOctagoneTranslateVector[i][2] = (float) ((r1 + r2) * Math.cos(phi) * Math.cos(theta));

            this.mOctagoneScale[i] = i / this.nbOctagone + 0.4f;
        }
    }

    /**
     *
     */
    private void setupIgloo() {
        this.igloo = new ObjModelMtl(this.context, R.raw.snow_igloo_obj, R.raw.snow_igloo_mtl, LIGHTAUGMENTATION, DISTANCECOEFF);
        for(int i=0; i < this.nbIgloo; i++) {
            double r = 5d * rand.nextDouble();
            double theta;
            /*if (Math.sin(90) == 1) {
                theta = rand.nextDouble() * 360d;
            } else {*/
                theta = rand.nextDouble() * Math.PI * 2d;
            //}
            this.mIglooTranslateVector[i][0] = (float) ((5d + r) * Math.cos(theta));
            this.mIglooTranslateVector[i][1] = -1f;
            this.mIglooTranslateVector[i][2] = (float) ((5d + r) * Math.sin(theta));
            this.mIglooAngle[i] = rand.nextFloat() * 360f;
        }
    }

    /**
     *
     */
    private void setupWhale(){
        this.mWhaleTranslateVector[0] = 0f;
        this.mWhaleTranslateVector[1] = 3f;
        this.mWhaleTranslateVector[2] = 10f;
        this.mWhaleAngle = 0f;
        this.whale = new ObjModelMtl(this.context, R.raw.snow_baleine_obj, R.raw.snow_baleine_mtl, LIGHTAUGMENTATION, DISTANCECOEFF);
        this.whale.setColors(this.whale.makeColor(rand), this.whale.makeColor(rand), this.whale.makeColor(0.5f, 0.5f, 0.5f));
    }

    /**
     *
     */
    private void setupMountains() {
        this.mMountainsTranslateVector[0] = 0f;
        this.mMountainsTranslateVector[1] = -3f;
        this.mMountainsTranslateVector[2] = 0f;
        this.mountains = new ObjModelMtl(this.context, R.raw.snow_mountains_obj, R.raw.snow_mountains_mtl, LIGHTAUGMENTATION, DISTANCECOEFF);
    }

    /**
     *
     * @param freqArray
     */
    private void updateTree(float[] freqArray){
        for(int i=0; i<this.nbTree; i++){
            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, this.mTreeTranslateVector[i][0], this.mTreeTranslateVector[i][1], this.mTreeTranslateVector[i][2]);
            float max = 0f;
            for(int j= i * 4; j < (i + 1) * 4; j++){
                if(max < freqArray[j]){
                    max = freqArray[j];
                }
            }
            float scale = this.mTreeScale[i] + max * this.mTreeScale[i] + max * i * this.mTreeFreqAttenuation * this.mTreeScale[i];
            Matrix.scaleM(mModelMatrix, 0, this.mTreeScale[i], scale, this.mTreeScale[i]);
            this.mTreeModelMatrix[i] = mModelMatrix.clone();
        }
    }

    /**
     *
     * @param freqArray
     */
    private void updatePing(float[] freqArray) {
        for(int i=0; i<nbPing; i++) {
            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);

            Matrix.translateM(mModelMatrix, 0, this.mPingTranslateVector[i][0], this.mPingTranslateVector[i][1], this.mPingTranslateVector[i][2]);

            float sum = 0f;
            for (int j = freqArray.length * i / this.nbPing; j < freqArray.length * (i + 1) / this.nbPing; j++) {
                sum += freqArray[i];
            }
            if (this.cpt % 2 == i % 2) {
                this.mPingAngle[i] += sum + sum * i * this.mPingFreqAttenuation;
            } else {
                this.mPingAngle[i] -= sum + sum * i * this.mPingFreqAttenuation;
            }
            Matrix.setRotateM(mPingRotationMatrix[i], 0, mPingAngle[i], 0f, 1f, 0f);

            float[] tmpMat = mModelMatrix.clone();
            Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, mPingRotationMatrix[i], 0);

            Matrix.scaleM(mModelMatrix, 0, SCALE, SCALE, SCALE);

            this.mPingModelMatrix[i] = mModelMatrix.clone();
        }
    }

    /**
     *
     * @param freqArray
     */
    private void updateOctagone(float[] freqArray) {
        for(int i=0; i<this.nbOctagone; i++){
            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);

            Matrix.translateM(mModelMatrix, 0, this.mOctagoneTranslateVector[i][0], this.mOctagoneTranslateVector[i][1], this.mOctagoneTranslateVector[i][2]);

            Matrix.setRotateM(this.mOctagoneRotationMatrix[i], 0, this.mOctagoneAngle[i] += 1f, this.mOctagoneRotationOrientation[i][0], this.mOctagoneRotationOrientation[i][1], this.mOctagoneRotationOrientation[i][2]);

            float[] tmpMat = mModelMatrix.clone();
            Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, this.mOctagoneRotationMatrix[i], 0);

            float sum = 0f;
            for (int j = freqArray.length * i / this.nbOctagone; j < freqArray.length * (i + 1) / this.nbOctagone; j++){
                sum += freqArray[i];
            }
            float scale = this.mOctagoneScale[i] + sum * this.mOctagoneScale[i] + sum * i * this.mOctagoneFreqAttenuation * this.mOctagoneScale[i];
            scale = scale > 2 * this.mOctagoneScale[i] ? 2 * this.mOctagoneScale[i] : scale;
            Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

            this.mOctagoneModelMatrix[i] = mModelMatrix.clone();
        }
    }

    /**
     *
     * @param freqArray
     */
    private void updateIgloo(float[] freqArray) {
        for(int i=0; i<this.nbIgloo; i++) {
            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, this.mIglooTranslateVector[i][0], this.mIglooTranslateVector[i][1], this.mIglooTranslateVector[i][2]);
            Matrix.setRotateM(this.mIglooRotationMatrix[i], 0, mIglooAngle[i], 0, 1f, 0f);
            float[] tmpMat = mModelMatrix.clone();
            Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, this.mIglooRotationMatrix[i], 0);
            float scale = SCALE + freqArray[i] * this.mIglooFreqAttenuation;
            Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);
            this.mIglooModelMatrix[i] = mModelMatrix.clone();
        }
    }

    /**
     *
     * @param freqArray
     */
    private void updateWhale(float[] freqArray){
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mWhaleTranslateVector[0], this.mWhaleTranslateVector[1], this.mWhaleTranslateVector[2]);

        this.mWhaleAngle += 0.125f;
        Matrix.setRotateM(this.mWhaleRotationMatrix, 0, this.mWhaleAngle, 0f, 1f, 0f);

        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, mWhaleRotationMatrix, 0, tmpMat, 0);

        float scale;
        if(freqArray[3] > 0.2 && this.cpt % 2 == 0){
            scale = SCALE + SCALE * this.mWhaleFreqAttenuation;//* freqArray[3];
        }else{
            scale = SCALE;
        }
        Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

        this.mWhaleModelMatrix = mModelMatrix.clone();
    }

    /**
     *
     */
    private void updateMountains(){
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        float scale = SCALE;
        Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

        Matrix.translateM(mModelMatrix, 0, this.mMountainsTranslateVector[0], this.mMountainsTranslateVector[1], this.mMountainsTranslateVector[2]);

        this.mMountainsModelMatrix = mModelMatrix.clone();
    }

    private void count(){
        this.cpt--;
        if(this.cpt <= 0){
            this.cpt = MAXCPT;
        }
    }

    /**
     *
     * @param freqArray
     */
    public void update(float[] freqArray){
        this.updatePing(freqArray);
        this.updateIgloo(freqArray);
        this.updateWhale(freqArray);
        this.updateOctagone(freqArray);
        this.updateMountains();
        this.updateTree(freqArray);
        this.count();
    }

    /**
     *
     * @param mProjectionMatrix
     * @param mViewMatrix
     * @param mLightPosInEyeSpace
     */
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition){
        float[] tmpMVMatrix = new float[16];
        float[] tmpMVPMatrix = new float[16];

        for(int i=0; i < this.nbPing; i++) {
            Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, this.mPingModelMatrix[i], 0);
            Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
            this.ping.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace, mCameraPosition);
        }

        for(int i=0; i < this.nbIgloo; i++) {
            Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, this.mIglooModelMatrix[i], 0);
            Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
            this.igloo.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace, mCameraPosition);
        }

        Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, this.mWhaleModelMatrix, 0);
        Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
        this.whale.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace, mCameraPosition);

        for(int i=0; i < this.nbOctagone; i++){
            Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, this.mOctagoneModelMatrix[i], 0);
            Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
            this.octagone.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace);
        }

        for(int i=0; i < this.nbTree; i++){
            Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, this.mTreeModelMatrix[i], 0);
            Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
            this.tree.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace, mCameraPosition);
        }

        Matrix.multiplyMM(tmpMVMatrix, 0, mViewMatrix, 0, this.mMountainsModelMatrix, 0);
        Matrix.multiplyMM(tmpMVPMatrix, 0, mProjectionMatrix, 0, tmpMVMatrix, 0);
        this.mountains.draw(tmpMVPMatrix, tmpMVMatrix, mLightPosInEyeSpace, mCameraPosition);
    }
}
