package com.samuelberrien.spectrix.obj.visualization;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.obj.ObjModel;

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

    private final float LIGHTAUGMENTATION = 10f;

    private Random rand;

    private float minDist;
    private float rangeDist;

    private int nbCenter;
    private int nbSameCenter;
    private float[][] mCenterPoint;
    private FloatBuffer[] mCenterColorBuffer;

    private int nbMaxOctagonePerExplosion;
    private float mFreqAugmentation = 0.3f;

    private ObjModel octagone;
    private ArrayList<Float> mOctagoneScale;
    private ArrayList<Float> mOctagoneAngle;
    private ArrayList<float[]> mOctagoneRotationOrientation;
    private ArrayList<float[]> mOctagoneRotationMatrix;
    private ArrayList<float[]> mOctagoneTranslateVector;
    private ArrayList<float[]> mOctagoneSpeedVector;
    private ArrayList<float[]> mOctagoneModelMatrix;
    private ArrayList<FloatBuffer> mOctagoneColorBuffer;

    public Explosion(Context context, int nbCenter, int nbSameCenter, int nbMaxOctagonePerExplosion, float minDist, float rangeDist){
        this.context = context;
        this.rand = new Random(System.currentTimeMillis());
        this.minDist = minDist;
        this.rangeDist = rangeDist;
        this.nbCenter = nbCenter;
        this.nbSameCenter = nbSameCenter;
        this.mCenterColorBuffer = new FloatBuffer[this.nbSameCenter * this.nbCenter];
        this.nbMaxOctagonePerExplosion = nbMaxOctagonePerExplosion;
        this.mCenterPoint = new float[this.nbCenter * this.nbSameCenter][3];
        this.octagone = new ObjModel(this.context, R.raw.octagone, 1f, 1f, 1f, LIGHTAUGMENTATION);
        this.mOctagoneScale = new ArrayList<>();
        this.mOctagoneAngle = new ArrayList<>();
        this.mOctagoneRotationOrientation = new ArrayList<>();
        this.mOctagoneRotationMatrix = new ArrayList<>();
        this.mOctagoneTranslateVector = new ArrayList<>();
        this.mOctagoneSpeedVector = new ArrayList<>();
        this.mOctagoneModelMatrix = new ArrayList<>();
        this.mOctagoneColorBuffer = new ArrayList<>();

        this.setupCenter();
    }

    private void setupCenter(){
        for(int i = 0; i < this.nbCenter * this.nbSameCenter; i++){
            float maxRange = rand.nextFloat() * rangeDist + minDist;
            double phi;
            double theta;
            if(Math.sin(90) == 1){
                phi = rand.nextDouble() * 360d;
                theta = rand.nextDouble() * 360d;
            }else{
                phi = rand.nextDouble() * Math.PI * 2;
                theta = rand.nextDouble() * Math.PI * 2;
            }
            this.mCenterPoint[i][0] = maxRange * (float) (Math.cos(phi) * Math.sin(theta));
            this.mCenterPoint[i][1] = maxRange * (float) Math.sin(phi);
            this.mCenterPoint[i][2] = maxRange * (float) (Math.cos(phi) * Math.cos(theta));

            float[] color = new float[this.octagone.getVertexDrawListLength() * 4 / 3];
            float red = rand.nextFloat();
            float green = rand.nextFloat();
            float blue = rand.nextFloat();
            for(int j=0; j < color.length; j+=4){
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

    private void addNewOctagone(float[] center, float magn, int indiceFreq, int indCenter){
        this.mOctagoneAngle.add(rand.nextFloat() * 360f);
        this.mOctagoneRotationOrientation.add(new float[]{rand.nextFloat() * 2 - 1f, rand.nextFloat() * 2 - 1f, rand.nextFloat() * 2 - 1f});
        this.mOctagoneRotationMatrix.add(new float[16]);
        this.mOctagoneTranslateVector.add(center);
        this.mOctagoneSpeedVector.add(new float[]{rand.nextFloat() * magn, rand.nextFloat() * magn, rand.nextFloat() * magn});
        this.mOctagoneModelMatrix.add(new float[16]);
        this.mOctagoneScale.add((this.nbCenter - indiceFreq) * 0.005f + 0.2f);


        this.mOctagoneColorBuffer.add(this.mCenterColorBuffer[indCenter]);
    }

    private void createNewOctagone(float[] freqArray){
        for(int i = 0; i < this.nbSameCenter * this.nbCenter; i++){
            int tmpFreqIndex = i / this.nbSameCenter;
            float tmpMagn = freqArray[tmpFreqIndex] + freqArray[tmpFreqIndex] * tmpFreqIndex * this.mFreqAugmentation;
            if(tmpMagn > 0.1f) {
                int nbNewOct = (int) (freqArray[tmpFreqIndex] * (float) this.nbMaxOctagonePerExplosion);
                for (int j = 0; j < nbNewOct; j++) {
                    this.addNewOctagone(this.mCenterPoint[i], tmpMagn, tmpFreqIndex, i);
                }
            }
        }
    }

    private void deleteOldOctagone(){
        for(int i=0; i < this.mOctagoneModelMatrix.size(); i++){
            double tmpSpeed = Math.sqrt(this.mOctagoneSpeedVector.get(i)[0] * this.mOctagoneSpeedVector.get(i)[0] + this.mOctagoneSpeedVector.get(i)[1] * this.mOctagoneSpeedVector.get(i)[1] + this.mOctagoneSpeedVector.get(i)[2] * this.mOctagoneSpeedVector.get(i)[2]);
            if(tmpSpeed < 0.1d){
                this.mOctagoneScale.remove(i);
                this.mOctagoneAngle.remove(i);
                this.mOctagoneRotationOrientation.remove(i);
                this.mOctagoneRotationMatrix.remove(i);
                this.mOctagoneTranslateVector.remove(i);
                this.mOctagoneSpeedVector.remove(i);
                this.mOctagoneModelMatrix.remove(i);
                this.mOctagoneColorBuffer.remove(i);
            }
        }
    }

    private void updateOctagone(){
        for(int i=0; i<this.mOctagoneModelMatrix.size(); i++){
            float[] tmp = this.mOctagoneSpeedVector.get(i);
            this.mOctagoneSpeedVector.set(i, new float[]{tmp[0] * 0.9f, tmp[1] * 0.9f, tmp[2] * 0.9f});
            tmp = this.mOctagoneTranslateVector.get(i);
            this.mOctagoneTranslateVector.set(i, new float[]{tmp[0] + this.mOctagoneSpeedVector.get(i)[0], tmp[1] + this.mOctagoneSpeedVector.get(i)[1], tmp[2] + this.mOctagoneSpeedVector.get(i)[2]});
            this.mOctagoneAngle.set(i, this.mOctagoneAngle.get(i) + 1f);

            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, this.mOctagoneTranslateVector.get(i)[0], this.mOctagoneTranslateVector.get(i)[1], this.mOctagoneTranslateVector.get(i)[2]);
            Matrix.setRotateM(this.mOctagoneRotationMatrix.get(i), 0, this.mOctagoneAngle.get(i), this.mOctagoneRotationOrientation.get(i)[0], this.mOctagoneRotationOrientation.get(i)[1], this.mOctagoneRotationOrientation.get(i)[2]);
            float[] tmpMat = mModelMatrix.clone();
            Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, this.mOctagoneRotationMatrix.get(i), 0);
            Matrix.scaleM(mModelMatrix, 0, this.mOctagoneScale.get(i), this.mOctagoneScale.get(i), this.mOctagoneScale.get(i));

            this.mOctagoneModelMatrix.set(i, mModelMatrix);
        }
    }

    public void update(float[] freqArray){
        this.deleteOldOctagone();
        this.createNewOctagone(freqArray);
        this.updateOctagone();
    }

    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace){
        for(int i=0; i<this.mOctagoneModelMatrix.size() ; i++){
            float[] tmpModelViewMatrix = new float[16];
            float[] tmpModelViewProjectionMatrix = new float[16];
            Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mOctagoneModelMatrix.get(i), 0);
            Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);

            this.octagone.setColor(this.mOctagoneColorBuffer.get(i));
            this.octagone.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);
        }
    }
}
