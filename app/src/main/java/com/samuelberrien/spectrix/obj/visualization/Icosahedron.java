package com.samuelberrien.spectrix.obj.visualization;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.obj.ObjModel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

/**
 * Created by samuel on 25/01/17.
 */

public class Icosahedron {

    private Context context;

    private final float LIGHTAUGMENTATION = 10f;

    private Random rand;

    private float minDist;
    private float rangeDist;

    private int nbIcosahedron;
    private int nbSameIcosahedron;
    private ObjModel icosahedron;
    private FloatBuffer[] mColorBuffer;
    private float[] mScale;
    private float[] mAngle;
    private float[][] mTranslateVector;
    private float[][] mRotationOrientation;
    private float[][] mRotationMatrix;
    private float[][] mModelMatrix;
    private float mFreqAugmentation = 0.3f;

    public Icosahedron(Context context, int nbIcosahedron, int nbSameIcosahedron, float minDist, float rangeDist){
        this.context = context;

        this.minDist = minDist;
        this.rangeDist = rangeDist;

        this.rand = new Random(System.currentTimeMillis());

        this.nbIcosahedron = nbIcosahedron;
        this.nbSameIcosahedron = nbSameIcosahedron;
        this.mColorBuffer = new FloatBuffer[this.nbIcosahedron * this.nbSameIcosahedron];
        this.mScale = new float[this.nbIcosahedron * this.nbSameIcosahedron];
        this.mAngle = new float[this.nbIcosahedron * this.nbSameIcosahedron];
        this.mTranslateVector = new float[this.nbIcosahedron * this.nbSameIcosahedron][3];
        this.mRotationOrientation = new float[this.nbIcosahedron * this.nbSameIcosahedron][3];
        this.mRotationMatrix = new float[this.nbIcosahedron * this.nbSameIcosahedron][16];
        this.mModelMatrix = new float[this.nbIcosahedron * this.nbSameIcosahedron][16];

        this.setupIcosahedrons();
    }

    private void setupIcosahedrons(){
        this.icosahedron = new ObjModel(this.context, R.raw.icosahedron, rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION);
        for(int i=0; i < this.nbIcosahedron * this.nbSameIcosahedron; i++){
            float[] color = new float[this.icosahedron.getVertexDrawListLength() * 4 / 3];
            float red = rand.nextFloat();
            float green = rand.nextFloat();
            float blue = rand.nextFloat();
            for(int j=0; j < color.length; j+=4){
                color[j] = red;
                color[j + 1] = green;
                color[j + 2] = blue;
                color[j + 3] = 1f;
            }
            this.mColorBuffer[i] = ByteBuffer.allocateDirect(color.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            this.mColorBuffer[i].put(color)
                    .position(0);

            this.mScale[i] = (this.nbIcosahedron - i / this.nbSameIcosahedron) * 0.005f + 0.5f;

            float maxRange = rand.nextFloat() * rangeDist + minDist;
            double phi;
            double theta;
            /*if(Math.sin(90) == 1){
                phi = rand.nextDouble() * 360d;
                theta = rand.nextDouble() * 360d;
            }else{*/
                phi = rand.nextDouble() * Math.PI * 2;
                theta = rand.nextDouble() * Math.PI * 2;
            //}
            float x = maxRange * (float) (Math.cos(phi) * Math.sin(theta));
            float y = maxRange * (float) Math.sin(phi);
            float z = maxRange * (float) (Math.cos(phi) * Math.cos(theta));
            this.mTranslateVector[i][0] = x;
            this.mTranslateVector[i][1] = y;
            this.mTranslateVector[i][2] = z;

            this.mAngle[i] = rand.nextFloat() * 360f;
            this.mRotationOrientation[i][0] = rand.nextFloat() * 2f - 1f;
            this.mRotationOrientation[i][1] = rand.nextFloat() * 2f - 1f;
            this.mRotationOrientation[i][2] = rand.nextFloat() * 2f - 1f;
        }
    }

    public void updateIcosahedrons(float[] freqArray){
        for(int i=0; i<this.nbIcosahedron * this.nbSameIcosahedron; i++) {
            float[] mModelMatrix = new float[16];
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, this.mTranslateVector[i][0], this.mTranslateVector[i][1], this.mTranslateVector[i][2]);
            Matrix.setRotateM(mRotationMatrix[i], 0, mAngle[i] += 1f, this.mRotationOrientation[i][0], this.mRotationOrientation[i][1], this.mRotationOrientation[i][2]);
            float[] tmpMat = mModelMatrix.clone();
            Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, mRotationMatrix[i], 0);
            int tmpFreqIndex = i / this.nbSameIcosahedron;
            float scale = this.mScale[i];
            float tmp = freqArray[tmpFreqIndex] + freqArray[tmpFreqIndex] * tmpFreqIndex * this.mFreqAugmentation;
            if(tmp > 0.7f){
                scale += 0.7f * mScale[i];
            }else{
                scale += tmp * mScale[i];
            }
            Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

            this.mModelMatrix[i] = mModelMatrix.clone();
        }
    }

    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace){
        for(int i=0; i<this.nbIcosahedron * this.nbSameIcosahedron; i++){
            float[] tmpModelViewMatrix = new float[16];
            float[] tmpModelViewProjectionMatrix = new float[16];
            Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mModelMatrix[i], 0);
            Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);

            this.icosahedron.setColor(this.mColorBuffer[i]);
            this.icosahedron.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);
        }
    }
}
