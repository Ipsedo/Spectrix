package com.samuelberrien.spectrix.obj.visualization;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.obj.ObjModel;
import com.samuelberrien.spectrix.obj.TextCube;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by samuel on 03/02/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Explosion {

    private Context context;

    private final float LIGHTAUGMENTATION = 2f;
    private final float DISTANCECOEFF = 0f;

    private Random rand;

    private float minDist;
    private float rangeDist;

    private int nbCenter;
    private int nbSameCenter;
    private float[][] mCenterPoint;
    private FloatBuffer[] mCenterColorBuffer;

    private float maxOctagonSpeed;

    private int nbMaxOctagonePerExplosion;
    private float mFreqAugmentation = 0.3f;

    private float[] freqArray = new float[1024];

    private ObjModel octagone;
    private ArrayList<Octagone> mOctagone;

    private TextCube textCube;
    //private ObjModel text;
    private float mTextScale = 0.5f;
    private float[] mTextTranslateVector = new float[3];
    private float[] mTextModelMatrix = new float[16];
    private long firstTimeMillisWithoutMusic = System.currentTimeMillis();

    /**
     * @param context
     * @param nbCenter
     * @param nbSameCenter
     * @param nbMaxOctagonePerExplosion
     * @param maxOctagonSpeed
     * @param minDist
     * @param rangeDist
     */
    public Explosion(Context context, int nbCenter, int nbSameCenter, int nbMaxOctagonePerExplosion, float maxOctagonSpeed, float minDist, float rangeDist) {
        this.context = context;
        this.rand = new Random(System.currentTimeMillis());
        this.minDist = minDist;
        this.rangeDist = rangeDist;
        this.nbCenter = nbCenter;
        this.nbSameCenter = nbSameCenter;
        this.mCenterColorBuffer = new FloatBuffer[this.nbSameCenter * this.nbCenter];
        this.nbMaxOctagonePerExplosion = nbMaxOctagonePerExplosion;
        this.mCenterPoint = new float[this.nbCenter * this.nbSameCenter][3];
        this.octagone = new ObjModel(this.context, R.raw.octagone, 1f, 1f, 1f, LIGHTAUGMENTATION, DISTANCECOEFF);
        this.maxOctagonSpeed = maxOctagonSpeed;
        this.mOctagone = new ArrayList<>();


        this.textCube = new TextCube(this.context);
        //this.text = new ObjModel(this.context, R.raw.best_experience_with_playing_music_obj, 1f, 1f, 1f, LIGHTAUGMENTATION, DISTANCECOEFF);

        this.setupCenter();
        this.setupTextCube();
    }

    /**
     *
     */
    private void setupTextCube() {
        this.mTextTranslateVector[0] = 0f;
        this.mTextTranslateVector[1] = 0f;
        this.mTextTranslateVector[2] = 0f;
    }

    /**
     *
     */
    private void setupCenter() {
        for (int i = 0; i < this.nbCenter * this.nbSameCenter; i++) {
            float maxRange = rand.nextFloat() * rangeDist + minDist;
            double phi = rand.nextDouble() * Math.PI * 2;
            double theta = rand.nextDouble() * Math.PI * 2;
            this.mCenterPoint[i][0] = maxRange * (float) (Math.cos(phi) * Math.sin(theta));
            this.mCenterPoint[i][1] = maxRange * (float) Math.sin(phi);
            this.mCenterPoint[i][2] = maxRange * (float) (Math.cos(phi) * Math.cos(theta));

            float[] color = new float[this.octagone.getVertexDrawListLength() * 4 / 3];
            float red = rand.nextFloat();
            float green = rand.nextFloat();
            float blue = rand.nextFloat();
            for (int j = 0; j < color.length; j += 4) {
                color[j] = red;
                color[j + 1] = green;
                color[j + 2] = blue;
                color[j + 3] = 1f;
            }
            FloatBuffer tmp = ByteBuffer.allocateDirect(color.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            tmp.put(color)
                    .position(0);
            this.mCenterColorBuffer[i] = tmp;
        }
    }

    /**
     * @param center
     * @param magn
     * @param indiceFreq
     * @param indCenter
     */
    private void addNewOctagone(float[] center, float magn, int indiceFreq, int indCenter) {
        double phi = rand.nextDouble() * Math.PI * 2;
        double theta = rand.nextDouble() * Math.PI * 2;
        float xSpeed = magn * this.maxOctagonSpeed * (float) (Math.cos(phi) * Math.sin(theta));
        float ySpeed = magn * this.maxOctagonSpeed * (float) Math.sin(phi);
        float zSpeed = magn * this.maxOctagonSpeed * (float) (Math.cos(phi) * Math.cos(theta));
        this.mOctagone.add(new Octagone((this.nbCenter - indiceFreq) * 0.002f + 0.03f, rand.nextFloat() * 360f, new float[]{rand.nextFloat() * 2 - 1f, rand.nextFloat() * 2 - 1f, rand.nextFloat() * 2 - 1f}, center, new float[]{xSpeed, ySpeed, zSpeed}, this.mCenterColorBuffer[indCenter]));
    }

    /**
     * @param freqArray
     */
    private void createNewOctagone(float[] freqArray) {
        for (int i = 0; i < this.nbSameCenter * this.nbCenter; i++) {
            int tmpFreqIndex = i / this.nbSameCenter;
            float tmpMagn = freqArray[tmpFreqIndex] + freqArray[tmpFreqIndex] * tmpFreqIndex * this.mFreqAugmentation;
            int nbNewOct = (int) (tmpMagn * (float) this.nbMaxOctagonePerExplosion);
            for (int j = 0; j < nbNewOct; j++) {
                this.addNewOctagone(this.mCenterPoint[i], tmpMagn, tmpFreqIndex, i);
            }
        }
    }

    /**
     *
     */
    private void deleteOldOctagone() {
        for (int i = 0; i < this.mOctagone.size(); i++) {
            if (this.mOctagone.get(i).getSpeedVectorNorm() < 0.2f) {
                this.mOctagone.remove(i);
            }
        }
    }

    /**
     *
     */
    private void moveOctagone() {
        for (int i = 0; i < this.mOctagone.size(); i++) {
            Octagone tmpOct = this.mOctagone.get(i);
            tmpOct.move();
            this.mOctagone.set(i, tmpOct);
        }
    }

    /**
     *
     */
    private void updateText() {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, this.mTextTranslateVector[0], this.mTextTranslateVector[1], this.mTextTranslateVector[2]);
        Matrix.scaleM(mModelMatrix, 0, this.mTextScale, this.mTextScale, this.mTextScale);

        this.mTextModelMatrix = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     * @param cubeX
     * @param cubeY
     * @param cubeZ
     */
    public void update(float[] freqArray, float cubeX, float cubeY, float cubeZ) {
        this.mTextTranslateVector[0] = cubeX * 5;
        this.mTextTranslateVector[1] = cubeY * 5;
        this.mTextTranslateVector[2] = cubeZ * 5;
        this.freqArray = freqArray;
    }

    /**
     *
     */
    public void updateVisualization() {
        this.deleteOldOctagone();
        this.createNewOctagone(freqArray);
        this.moveOctagone();
        this.updateText();
    }

    /**
     * @param mProjectionMatrix
     * @param mViewMatrix
     * @param mLightPosInEyeSpace
     */
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace) {
        float[] tmpModelViewMatrix = new float[16];
        float[] tmpModelViewProjectionMatrix = new float[16];
        for (int i = 0; i < this.mOctagone.size(); i++) {
            Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mOctagone.get(i).getmOctagoneModelMatrix(), 0);
            Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);

            this.octagone.setColor(this.mOctagone.get(i).getmOctagoneColorBuffer());
            this.octagone.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);
        }
        if (!this.mOctagone.isEmpty()) {
            this.firstTimeMillisWithoutMusic = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - this.firstTimeMillisWithoutMusic > 3000) {
            Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mTextModelMatrix, 0);
            Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
            this.textCube.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);
        }
    }

    private class Octagone {
        private float mOctagoneScale;
        private float mOctagoneAngle;
        private float[] mOctagoneRotationOrientation;
        private float[] mOctagoneRotationMatrix;
        private float[] mOctagoneTranslateVector;
        private float[] mOctagoneSpeedVector;
        private float[] mOctagoneModelMatrix;
        private FloatBuffer mOctagoneColorBuffer;

        /**
         * @param mOctagoneScale
         * @param mOctagoneAngle
         * @param mOctagoneRotationOrientation
         * @param mOctagoneTranslateVector
         * @param mOctagoneSpeedVector
         * @param mOctagoneColorBuffer
         */
        public Octagone(float mOctagoneScale, float mOctagoneAngle, float[] mOctagoneRotationOrientation, float[] mOctagoneTranslateVector, float[] mOctagoneSpeedVector, FloatBuffer mOctagoneColorBuffer) {
            this.mOctagoneAngle = mOctagoneAngle;
            this.mOctagoneScale = mOctagoneScale;
            this.mOctagoneRotationOrientation = mOctagoneRotationOrientation;
            this.mOctagoneTranslateVector = mOctagoneTranslateVector;
            this.mOctagoneSpeedVector = mOctagoneSpeedVector;
            this.mOctagoneColorBuffer = mOctagoneColorBuffer;
            this.mOctagoneRotationMatrix = new float[16];
            this.mOctagoneModelMatrix = new float[16];
        }

        /**
         *
         */
        public void move() {
            float[] tmp = this.mOctagoneSpeedVector;
            this.mOctagoneSpeedVector = new float[]{tmp[0] * 0.5f, tmp[1] * 0.5f, tmp[2] * 0.5f};
            tmp = this.mOctagoneTranslateVector;
            this.mOctagoneTranslateVector = new float[]{tmp[0] + this.mOctagoneSpeedVector[0], tmp[1] + this.mOctagoneSpeedVector[1], tmp[2] + this.mOctagoneSpeedVector[2]};
            this.mOctagoneAngle = this.mOctagoneAngle + 1f;

            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, this.mOctagoneTranslateVector[0], this.mOctagoneTranslateVector[1], this.mOctagoneTranslateVector[2]);
            Matrix.setRotateM(this.mOctagoneRotationMatrix, 0, this.mOctagoneAngle, this.mOctagoneRotationOrientation[0], this.mOctagoneRotationOrientation[1], this.mOctagoneRotationOrientation[2]);
            float[] tmpMat = mModelMatrix.clone();
            Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, this.mOctagoneRotationMatrix, 0);
            Matrix.scaleM(mModelMatrix, 0, this.mOctagoneScale, this.mOctagoneScale, this.mOctagoneScale);

            this.mOctagoneModelMatrix = mModelMatrix.clone();
        }

        /**
         * @return
         */
        public float[] getmOctagoneModelMatrix() {
            return this.mOctagoneModelMatrix;
        }

        /**
         * @return
         */
        public FloatBuffer getmOctagoneColorBuffer() {
            return this.mOctagoneColorBuffer;
        }

        /**
         * @return
         */
        public double getSpeedVectorNorm() {
            return Math.sqrt(this.mOctagoneSpeedVector[0] * this.mOctagoneSpeedVector[0] + this.mOctagoneSpeedVector[1] * this.mOctagoneSpeedVector[1] + this.mOctagoneSpeedVector[2] * this.mOctagoneSpeedVector[2]);
        }
    }
}
