package com.samuelberrien.spectrix.obj.visualization;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.obj.drawable.ObjModel;

import java.util.Random;

/**
 * Created by samuel on 25/01/17.
 */

public class Room {

    private Context context;

    private float scale;

    private final float LIGHTAUGMENTATION = 1f;
    private final float DISTANCECOEFF = 0.01f;

    private Random rand;

    private final int mMaxCounter = 16;
    private int mCounter = mMaxCounter;

    private final float mCadreAtenuation = 0.8f;
    private ObjModel cadreD;
    private float[] mCadreDTranslateVector = new float[]{-4f, 6, -7.55f};
    private float[] mCadreDModel = new float[16];
    private ObjModel cadreG;
    private float[] mCadreGTranslateVector = new float[]{4f, 6, -7.55f};
    private float[] mCadreGModel = new float[16];
    private ObjModel cadreM;
    private float[] mCadreMTranslateVector = new float[]{0f, 6, -7.55f};
    private float[] mCadreMModel = new float[16];
    private ObjModel cadreAD;
    private float[] mCadreADTranslateVector = new float[]{-4f, 6, 7.55f};
    private float[] mCadreADModel = new float[16];
    private ObjModel cadreAG;
    private float[] mCadreAGTranslateVector = new float[]{4f, 6, 7.55f};
    private float[] mCadreAGModel = new float[16];
    private ObjModel cadreAM;
    private float[] mCadreAMTranslateVector = new float[]{0f, 6, 7.55f};
    private float[] mCadreAMModel = new float[16];

    private float mLampeChevetFreqAttenuation = 100f;
    private ObjModel lampeChevetD;
    private float mLampeChevetDAngle = 0f;
    private float[] mLampeChevetDRotationMatrix = new float[16];
    private float[] mLampeChevetDTranslateVector = new float[]{5.0970f, 3.5283f, -6.6115f};
    private float[] mLampeChevetDModel = new float[16];
    private ObjModel lampeChevetG;
    private float mLampeChevetGAngle = 0f;
    private float[] mLampeChevetGRotationMatrix = new float[16];
    private float[] mLampeChevetGTranslateVector = new float[]{-5.0970f, 3.5283f, -6.6115f};
    private float[] mLampeChevetGModel = new float[16];

    private ObjModel lampePlafond;
    private float mLampePlafondAtenuation = 50f;
    private float[] mLampePlafondTranslateVector = new float[]{0f, 9.5462f, 0f};
    private float[] mLampePlafondModel = new float[16];

    private ObjModel lit2Places;
    private final float mLitVibrationAtenuation = 0.015f;
    private float[] mLit2PlacesTranslateVector = new float[]{0f, 1.8046f, -3.8427f};
    private float[] mLit2PlacesModel = new float[16];

    private ObjModel murs;
    private float[] mMursTranslateVector = new float[]{0f, 0f, 0f};
    private float[] mMursModel = new float[16];

    private final float mOreillerFreqAtenuation = 0.2f;
    private ObjModel oreillerD;
    private float[] mOreillerDTranslateVector = new float[]{1.35f, 3.2184f, -6.1602f};
    private float[] mOreillerDModel = new float[16];
    private ObjModel oreillerG;
    private float[] mOreillerGTranslateVector = new float[]{-1.35f, 3.2184f, -6.1362f};
    private float[] mOreillerGModel = new float[16];

    private ObjModel porte;
    private float mPorteVibrationAtenuation = 0.15f;
    private float[] mPorteTranslateVector = new float[]{7.8f, 3.5f, 1.5f};
    private float[] mPorteModel = new float[16];

    private ObjModel stores;
    private float mStoresVibrationAtenuation = 0.15f;
    private float[] mStoresTranslateVector = new float[]{-7.6871f, 6.3401f, 0f};
    private float[] mStoresModel = new float[16];

    private float mTableChevetFreqAtenuation = 1.8f;
    private ObjModel tableChevetD;
    private float[] mTableChevetDTranslateVector = new float[]{5f, 1.3321f, -6.1209f};
    private float[] mTableChevetDModel = new float[16];
    private ObjModel tableChevetG;
    private float[] mTableChevetGTranslateVector = new float[]{-5f, 1.3321f, -6.1209f};
    private float[] mTableChevetGModel = new float[16];

    private float[] freqArray = new float[1024];

    /**
     * @param context
     * @param scale
     */
    public Room(Context context, float scale) {
        this.rand = new Random(System.currentTimeMillis());
        this.context = context;
        this.scale = scale;
        this.cadreD = new ObjModel(this.context, "obj/room/room_cadre.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.cadreG = new ObjModel(this.context, "obj/room/room_cadre.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.cadreM = new ObjModel(this.context, "obj/room/room_cadre.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.cadreAD = new ObjModel(this.context, "obj/room/room_cadre.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.cadreAG = new ObjModel(this.context, "obj/room/room_cadre.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.cadreAM = new ObjModel(this.context, "obj/room/room_cadre.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.lampeChevetD = new ObjModel(this.context, "obj/room/room_lampe_chevet.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.lampeChevetG = new ObjModel(this.context, "obj/room/room_lampe_chevet.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.lampePlafond = new ObjModel(this.context, "obj/room/room_lampe_plafond.obj", 1f, 1f, 1f, LIGHTAUGMENTATION, DISTANCECOEFF);
        this.lit2Places = new ObjModel(this.context, "obj/room/room_lit_2_places.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.murs = new ObjModel(this.context, "obj/room/room_murs.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.oreillerD = new ObjModel(this.context, "obj/room/room_oreiller.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.oreillerG = new ObjModel(this.context, "obj/room/room_oreiller.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.porte = new ObjModel(this.context, "obj/room/room_porte.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.stores = new ObjModel(this.context, "obj/room/room_stores.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.tableChevetD = new ObjModel(this.context, "obj/room/room_table_chevet.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
        this.tableChevetG = new ObjModel(this.context, "obj/room/room_table_chevet.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
    }

    /**
     * @param freqArray
     */
    private void updateCadreD(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mCadreDTranslateVector[0] * scale, this.mCadreDTranslateVector[1] * scale, this.mCadreDTranslateVector[2] * scale * -1f);

        float max = 0f;
        for (int i = 7; i < 11; i++) {
            if (freqArray[i] > max) {
                max = freqArray[i];
            }
        }
        float tmpScale = scale + max * this.mCadreAtenuation * scale;
        Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

        this.mCadreDModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateCadreG(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mCadreGTranslateVector[0] * scale, this.mCadreGTranslateVector[1] * scale, this.mCadreGTranslateVector[2] * scale * -1f);

        float max = 0f;
        for (int i = 15; i < 19; i++) {
            if (freqArray[i] > max) {
                max = freqArray[i];
            }
        }
        float tmpScale = scale + max * this.mCadreAtenuation * scale;
        Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

        this.mCadreGModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateCadreM(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mCadreMTranslateVector[0] * scale, this.mCadreMTranslateVector[1] * scale, this.mCadreMTranslateVector[2] * scale * -1f);

        float max = 0f;
        for (int i = 11; i < 15; i++) {
            if (freqArray[i] > max) {
                max = freqArray[i];
            }
        }
        float tmpScale = scale + max * this.mCadreAtenuation * scale;
        Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

        this.mCadreMModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateCadreAD(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mCadreADTranslateVector[0] * scale, this.mCadreADTranslateVector[1] * scale, this.mCadreADTranslateVector[2] * scale * -1f);

        float[] tmpRotationMatrix = new float[16];
        Matrix.setIdentityM(tmpRotationMatrix, 0);
        Matrix.setRotateM(tmpRotationMatrix, 0, 180, 0f, 1f, 0f);
        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, tmpRotationMatrix, 0);

        float max = 0f;
        for (int i = 19; i < 23; i++) {
            if (freqArray[i] > max) {
                max = freqArray[i];
            }
        }
        float tmpScale = scale + max * this.mCadreAtenuation * scale;
        Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

        this.mCadreADModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateCadreAG(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mCadreAGTranslateVector[0] * scale, this.mCadreAGTranslateVector[1] * scale, this.mCadreAGTranslateVector[2] * scale * -1f);

        float[] tmpRotationMatrix = new float[16];
        Matrix.setIdentityM(tmpRotationMatrix, 0);
        Matrix.setRotateM(tmpRotationMatrix, 0, 180, 0f, 1f, 0f);
        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, tmpRotationMatrix, 0);

        float max = 0f;
        for (int i = 27; i < 30; i++) {
            if (freqArray[i] > max) {
                max = freqArray[i];
            }
        }
        float tmpScale = scale + max * this.mCadreAtenuation * scale;
        Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

        this.mCadreAGModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateCadreAM(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mCadreAMTranslateVector[0] * scale, this.mCadreAMTranslateVector[1] * scale, this.mCadreAMTranslateVector[2] * scale * -1f);

        float[] tmpRotationMatrix = new float[16];
        Matrix.setIdentityM(tmpRotationMatrix, 0);
        Matrix.setRotateM(tmpRotationMatrix, 0, 180, 0f, 1f, 0f);
        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, tmpRotationMatrix, 0);

        float max = 0f;
        for (int i = 23; i < 27; i++) {
            if (freqArray[i] > max) {
                max = freqArray[i];
            }
        }
        float tmpScale = scale + max * this.mCadreAtenuation * scale;
        Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

        this.mCadreAMModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateLampeChevetD(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mLampeChevetDTranslateVector[0] * scale, this.mLampeChevetDTranslateVector[1] * scale, this.mLampeChevetDTranslateVector[2] * scale * -1f);

        float max = 0f;
        for (int j = 44; j < 105; j++) {
            if (max < freqArray[j]) {
                max = freqArray[j];
            }
        }
        if (this.mCounter % 2 == 0) {
            this.mLampeChevetDAngle += max * this.mLampeChevetFreqAttenuation;
        } else {
            this.mLampeChevetDAngle -= max * this.mLampeChevetFreqAttenuation;
        }
        Matrix.setRotateM(this.mLampeChevetDRotationMatrix, 0, this.mLampeChevetDAngle, 0f, 1f, 0f);
        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, this.mLampeChevetDRotationMatrix, 0);

        Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

        this.mLampeChevetDModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateLampeChevetG(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mLampeChevetGTranslateVector[0] * scale, this.mLampeChevetGTranslateVector[1] * scale, this.mLampeChevetGTranslateVector[2] * scale * -1f);

        float max = 0f;
        for (int j = 105; j < 206; j++) {
            if (max < freqArray[j]) {
                max = freqArray[j];
            }
        }
        if (this.mCounter % 2 == 1) {
            this.mLampeChevetGAngle += max * this.mLampeChevetFreqAttenuation;
        } else {
            this.mLampeChevetGAngle -= max * this.mLampeChevetFreqAttenuation;
        }
        Matrix.setRotateM(this.mLampeChevetGRotationMatrix, 0, this.mLampeChevetGAngle, 0f, 1f, 0f);
        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, this.mLampeChevetDRotationMatrix, 0);

        Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

        this.mLampeChevetGModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateLampePlafond(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mLampePlafondTranslateVector[0] * scale, this.mLampePlafondTranslateVector[1] * scale, this.mLampePlafondTranslateVector[2] * scale * -1f);

        float max = 0f;
        for (int i = 206; i < 307; i++) {
            if (freqArray[i] > max) {
                max = freqArray[i];
            }
        }
        float tmpScale = scale + max * this.mLampePlafondAtenuation;
        Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

        this.mLampePlafondModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateLit2Places(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mLit2PlacesTranslateVector[0] * scale, this.mLit2PlacesTranslateVector[1] * scale, this.mLit2PlacesTranslateVector[2] * scale * -1f);

        float tmpScale;
        if ((freqArray[0] > 0.2f || freqArray[1] > 0.2f) && this.mCounter % 2 == 0) {
            tmpScale = scale + scale * this.mLitVibrationAtenuation;
        } else {
            tmpScale = scale;
        }
        Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

        this.mLit2PlacesModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateMurs(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, this.mMursTranslateVector[0] * scale, this.mMursTranslateVector[1] * scale, this.mMursTranslateVector[2] * scale * -1f);
        Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);
        this.mMursModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateOreillerD(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mOreillerDTranslateVector[0] * scale, this.mOreillerDTranslateVector[1] * scale, this.mOreillerDTranslateVector[2] * scale * -1f);

        float max = 0f;
        for (int i = 3; i < 5; i++) {
            if (max < freqArray[i]) {
                max = freqArray[i];
            }
        }
        float tmpScale = scale + max * this.mOreillerFreqAtenuation * scale;
        Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

        this.mOreillerDModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateOreillerG(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mOreillerGTranslateVector[0] * scale, this.mOreillerGTranslateVector[1] * scale, this.mOreillerGTranslateVector[2] * scale * -1f);

        float max = 0f;
        for (int i = 5; i < 7; i++) {
            if (max < freqArray[i]) {
                max = freqArray[i];
            }
        }
        float tmpScale = scale + max * this.mOreillerFreqAtenuation * scale;
        Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

        this.mOreillerGModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updatePorte(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mPorteTranslateVector[0] * scale, this.mPorteTranslateVector[1] * scale, this.mPorteTranslateVector[2] * scale * -1f);

        float tmpScale;
        if ((freqArray[1] > 0.2f || freqArray[2] > 0.2f) && this.mCounter % 2 == 0) {
            tmpScale = scale + scale * this.mPorteVibrationAtenuation;
        } else {
            tmpScale = scale;
        }
        Matrix.scaleM(mModelMatrix, 0, tmpScale, scale, scale);

        this.mPorteModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateStores(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mStoresTranslateVector[0] * scale, this.mStoresTranslateVector[1] * scale, this.mStoresTranslateVector[2] * scale * -1f);

        float tmpScale;
        if ((freqArray[1] > 0.2f || freqArray[2] > 0.2f) && this.mCounter % 2 == 0) {
            tmpScale = scale + scale * this.mStoresVibrationAtenuation;
        } else {
            tmpScale = scale;
        }
        Matrix.scaleM(mModelMatrix, 0, tmpScale, scale, scale);

        this.mStoresModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateTableChevetD(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mTableChevetDTranslateVector[0] * scale, this.mTableChevetDTranslateVector[1] * scale, this.mTableChevetDTranslateVector[2] * scale * -1f);

        float max = 0f;
        for (int i = 22; i < 33; i++) {
            if (max < freqArray[i]) {
                max = freqArray[i];
            }
        }
        float tmpScale = scale + max * this.mTableChevetFreqAtenuation * scale;
        Matrix.scaleM(mModelMatrix, 0, tmpScale, scale, tmpScale);

        this.mTableChevetDModel = mModelMatrix.clone();
    }

    /**
     * @param freqArray
     */
    private void updateTableChevetG(float[] freqArray) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, this.mTableChevetGTranslateVector[0] * scale, this.mTableChevetGTranslateVector[1] * scale, this.mTableChevetGTranslateVector[2] * scale * -1f);

        float max = 0f;
        for (int i = 33; i < 44; i++) {
            if (max < freqArray[i]) {
                max = freqArray[i];
            }
        }
        float tmpScale = scale + max * this.mTableChevetFreqAtenuation * scale;
        Matrix.scaleM(mModelMatrix, 0, tmpScale, scale, tmpScale);

        this.mTableChevetGModel = mModelMatrix.clone();
    }

    /**
     *
     */
    private void count() {
        this.mCounter--;
        if (this.mCounter <= 0) {
            this.mCounter = mMaxCounter;
        }
    }

    public void update(float[] freqArray) {
        this.freqArray = freqArray;
    }

    /**
     *
     */
    public void updateRoom() {
        this.updateMurs(freqArray);

        this.updateCadreD(freqArray);
        this.updateCadreG(freqArray);
        this.updateCadreM(freqArray);
        this.updateCadreAD(freqArray);
        this.updateCadreAG(freqArray);
        this.updateCadreAM(freqArray);

        this.updateLampeChevetD(freqArray);
        this.updateLampeChevetG(freqArray);

        this.updateLampePlafond(freqArray);

        this.updateLit2Places(freqArray);

        this.updateOreillerD(freqArray);
        this.updateOreillerG(freqArray);

        this.updatePorte(freqArray);

        this.updateStores(freqArray);

        this.updateTableChevetD(freqArray);
        this.updateTableChevetG(freqArray);

        this.count();
    }

    /**
     * @return
     */
    public float[] getLightPos() {
        return new float[]{0f * scale, 7f * scale, 0f * scale};
    }

    /**
     * @param mProjectionMatrix
     * @param mViewMatrix
     * @param mLightPosInEyeSpace
     */
    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace) {
        float[] tmpModelViewMatrix = new float[16];
        float[] tmpModelViewProjectionMatrix = new float[16];

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mCadreDModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.cadreD.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mCadreGModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.cadreG.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mCadreMModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.cadreM.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mCadreADModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.cadreAD.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mCadreAGModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.cadreAG.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mCadreAMModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.cadreAM.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mLampeChevetDModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.lampeChevetD.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mLampeChevetGModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.lampeChevetG.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mLampePlafondModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.lampePlafond.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mLit2PlacesModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.lit2Places.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mOreillerDModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.oreillerD.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mOreillerGModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.oreillerG.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mPorteModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.porte.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mStoresModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.stores.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mTableChevetDModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.tableChevetD.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mTableChevetGModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.tableChevetG.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

        Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, this.mMursModel, 0);
        Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
        this.murs.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);
    }
}
