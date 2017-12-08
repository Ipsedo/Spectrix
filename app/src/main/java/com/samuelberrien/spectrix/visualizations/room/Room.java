package com.samuelberrien.spectrix.visualizations.room;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.drawable.obj.ObjModelVBO;
import com.samuelberrien.spectrix.utils.core.Visualization;

import java.util.Random;

/**
 * Created by samuel on 30/08/17.
 */

public class Room implements Visualization {

	private boolean isInit;

	private float scale;

	private final float LIGHTAUGMENTATION;
	private final float DISTANCECOEFF;

	private Random rand;

	private final int mMaxCounter = 16;
	private int mCounter = mMaxCounter;

	private final float mCadreAtenuation = 0.8f;
	private ObjModelVBO cadreD;
	private float[] mCadreDTranslateVector = new float[]{-4f, 6, -7.55f};
	private float[] mCadreDModel = new float[16];
	private ObjModelVBO cadreG;
	private float[] mCadreGTranslateVector = new float[]{4f, 6, -7.55f};
	private float[] mCadreGModel = new float[16];
	private ObjModelVBO cadreM;
	private float[] mCadreMTranslateVector = new float[]{0f, 6, -7.55f};
	private float[] mCadreMModel = new float[16];
	private ObjModelVBO cadreAD;
	private float[] mCadreADTranslateVector = new float[]{-4f, 6, 7.55f};
	private float[] mCadreADModel = new float[16];
	private ObjModelVBO cadreAG;
	private float[] mCadreAGTranslateVector = new float[]{4f, 6, 7.55f};
	private float[] mCadreAGModel = new float[16];
	private ObjModelVBO cadreAM;
	private float[] mCadreAMTranslateVector = new float[]{0f, 6, 7.55f};
	private float[] mCadreAMModel = new float[16];

	private float mLampeChevetFreqAttenuation = 100f;
	private ObjModelVBO lampeChevetD;
	private float mLampeChevetDAngle = 0f;
	private float[] mLampeChevetDRotationMatrix = new float[16];
	private float[] mLampeChevetDTranslateVector = new float[]{5.0970f, 3.5283f, -6.6115f};
	private float[] mLampeChevetDModel = new float[16];
	private ObjModelVBO lampeChevetG;
	private float mLampeChevetGAngle = 0f;
	private float[] mLampeChevetGRotationMatrix = new float[16];
	private float[] mLampeChevetGTranslateVector = new float[]{-5.0970f, 3.5283f, -6.6115f};
	private float[] mLampeChevetGModel = new float[16];

	private ObjModelVBO lampePlafond;
	private float mLampePlafondAtenuation = 50f;
	private float[] mLampePlafondTranslateVector = new float[]{0f, 9.5462f, 0f};
	private float[] mLampePlafondModel = new float[16];

	private ObjModelVBO lit2Places;
	private final float mLitVibrationAtenuation = 0.015f;
	private float[] mLit2PlacesTranslateVector = new float[]{0f, 1.8046f, -3.8427f};
	private float[] mLit2PlacesModel = new float[16];

	private ObjModelVBO murs;
	private float[] mMursTranslateVector = new float[]{0f, 0f, 0f};
	private float[] mMursModel = new float[16];

	private final float mOreillerFreqAtenuation = 0.2f;
	private ObjModelVBO oreillerD;
	private float[] mOreillerDTranslateVector = new float[]{1.35f, 3.2184f, -6.1602f};
	private float[] mOreillerDModel = new float[16];
	private ObjModelVBO oreillerG;
	private float[] mOreillerGTranslateVector = new float[]{-1.35f, 3.2184f, -6.1362f};
	private float[] mOreillerGModel = new float[16];

	private ObjModelVBO porte;
	private float mPorteVibrationAtenuation = 0.15f;
	private float[] mPorteTranslateVector = new float[]{7.8f, 3.5f, 1.5f};
	private float[] mPorteModel = new float[16];

	private ObjModelVBO stores;
	private float mStoresVibrationAtenuation = 0.15f;
	private float[] mStoresTranslateVector = new float[]{-7.6871f, 6.3401f, 0f};
	private float[] mStoresModel = new float[16];

	private float mTableChevetFreqAtenuation = 1.8f;
	private ObjModelVBO tableChevetD;
	private float[] mTableChevetDTranslateVector = new float[]{5f, 1.3321f, -6.1209f};
	private float[] mTableChevetDModel = new float[16];
	private ObjModelVBO tableChevetG;
	private float[] mTableChevetGTranslateVector = new float[]{-5f, 1.3321f, -6.1209f};
	private float[] mTableChevetGModel = new float[16];

	public Room() {
		isInit = false;
		LIGHTAUGMENTATION = 1f;
		DISTANCECOEFF = 0.01f;
	}

	@Override
	public void init(Context context, boolean isVR) {
		rand = new Random(System.currentTimeMillis());
		scale = 1f;
		cadreD = new ObjModelVBO(context, "obj/room/room_cadre.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		cadreG = new ObjModelVBO(context, "obj/room/room_cadre.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		cadreM = new ObjModelVBO(context, "obj/room/room_cadre.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		cadreAD = new ObjModelVBO(context, "obj/room/room_cadre.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		cadreAG = new ObjModelVBO(context, "obj/room/room_cadre.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		cadreAM = new ObjModelVBO(context, "obj/room/room_cadre.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		lampeChevetD = new ObjModelVBO(context, "obj/room/room_lampe_chevet.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		lampeChevetG = new ObjModelVBO(context, "obj/room/room_lampe_chevet.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		lampePlafond = new ObjModelVBO(context, "obj/room/room_lampe_plafond.obj", 1f, 1f, 1f, LIGHTAUGMENTATION, DISTANCECOEFF);
		lit2Places = new ObjModelVBO(context, "obj/room/room_lit_2_places.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		murs = new ObjModelVBO(context, "obj/room/room_murs.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		oreillerD = new ObjModelVBO(context, "obj/room/room_oreiller.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		oreillerG = new ObjModelVBO(context, "obj/room/room_oreiller.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		porte = new ObjModelVBO(context, "obj/room/room_porte.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		stores = new ObjModelVBO(context, "obj/room/room_stores.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		tableChevetD = new ObjModelVBO(context, "obj/room/room_table_chevet.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
		tableChevetG = new ObjModelVBO(context, "obj/room/room_table_chevet.obj", rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), LIGHTAUGMENTATION, DISTANCECOEFF);
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
		updateMurs();

		updateCadreD(freqArray);
		updateCadreG(freqArray);
		updateCadreM(freqArray);
		updateCadreAD(freqArray);
		updateCadreAG(freqArray);
		updateCadreAM(freqArray);

		updateLampeChevetD(freqArray);
		updateLampeChevetG(freqArray);

		updateLampePlafond(freqArray);

		updateLit2Places(freqArray);

		updateOreillerD(freqArray);
		updateOreillerG(freqArray);

		updatePorte(freqArray);

		updateStores(freqArray);

		updateTableChevetD(freqArray);
		updateTableChevetG(freqArray);

		count();
	}

	@Override
	public float[] getCameraPosition() {
		return new float[]{0f * scale, 7f * scale, 0f * scale};
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
		float[] tmpModelViewMatrix = new float[16];
		float[] tmpModelViewProjectionMatrix = new float[16];

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mCadreDModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		cadreD.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mCadreGModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		cadreG.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mCadreMModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		cadreM.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mCadreADModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		cadreAD.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mCadreAGModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		cadreAG.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mCadreAMModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		cadreAM.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mLampeChevetDModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		lampeChevetD.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mLampeChevetGModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		lampeChevetG.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mLampePlafondModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		lampePlafond.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mLit2PlacesModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		lit2Places.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mOreillerDModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		oreillerD.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mOreillerGModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		oreillerG.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mPorteModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		porte.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mStoresModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		stores.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mTableChevetDModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		tableChevetD.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mTableChevetGModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		tableChevetG.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);

		Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, mMursModel, 0);
		Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);
		murs.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);
	}

	@Override
	public String getName() {
		return "Room";
	}

	private void updateCadreD(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mCadreDTranslateVector[0] * scale, mCadreDTranslateVector[1] * scale, mCadreDTranslateVector[2] * scale * -1f);

		float max = 0f;
		for (int i = 7; i < 11; i++) {
			if (freqArray[i] > max) {
				max = freqArray[i];
			}
		}
		float tmpScale = scale + max * mCadreAtenuation * scale;
		tmpScale = tmpScale <= 2f ? tmpScale : 2f;
		Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

		mCadreDModel = mModelMatrix.clone();
	}

	private void updateCadreG(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mCadreGTranslateVector[0] * scale, mCadreGTranslateVector[1] * scale, mCadreGTranslateVector[2] * scale * -1f);

		float max = 0f;
		for (int i = 15; i < 19; i++) {
			if (freqArray[i] > max) {
				max = freqArray[i];
			}
		}
		float tmpScale = scale + max * mCadreAtenuation * scale;
		tmpScale = tmpScale <= 2f ? tmpScale : 2f;
		Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

		mCadreGModel = mModelMatrix.clone();
	}

	private void updateCadreM(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mCadreMTranslateVector[0] * scale, mCadreMTranslateVector[1] * scale, mCadreMTranslateVector[2] * scale * -1f);

		float max = 0f;
		for (int i = 11; i < 15; i++) {
			if (freqArray[i] > max) {
				max = freqArray[i];
			}
		}
		float tmpScale = scale + max * mCadreAtenuation * scale;
		tmpScale = tmpScale <= 2f ? tmpScale : 2f;
		Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

		mCadreMModel = mModelMatrix.clone();
	}

	private void updateCadreAD(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mCadreADTranslateVector[0] * scale, mCadreADTranslateVector[1] * scale, mCadreADTranslateVector[2] * scale * -1f);

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
		float tmpScale = scale + max * mCadreAtenuation * scale;
		tmpScale = tmpScale <= 2f ? tmpScale : 2f;
		Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

		mCadreADModel = mModelMatrix.clone();
	}

	private void updateCadreAG(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mCadreAGTranslateVector[0] * scale, mCadreAGTranslateVector[1] * scale, mCadreAGTranslateVector[2] * scale * -1f);

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
		float tmpScale = scale + max * mCadreAtenuation * scale;
		tmpScale = tmpScale <= 2f ? tmpScale : 2f;
		Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

		mCadreAGModel = mModelMatrix.clone();
	}

	private void updateCadreAM(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mCadreAMTranslateVector[0] * scale, mCadreAMTranslateVector[1] * scale, mCadreAMTranslateVector[2] * scale * -1f);

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
		float tmpScale = scale + max * mCadreAtenuation * scale;
		tmpScale = tmpScale <= 2f ? tmpScale : 2f;
		Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

		mCadreAMModel = mModelMatrix.clone();
	}

	private void updateLampeChevetD(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mLampeChevetDTranslateVector[0] * scale, mLampeChevetDTranslateVector[1] * scale, mLampeChevetDTranslateVector[2] * scale * -1f);

		float max = 0f;
		for (int j = 44; j < 105; j++) {
			if (max < freqArray[j]) {
				max = freqArray[j];
			}
		}
		if (mCounter % 2 == 0) {
			mLampeChevetDAngle += max * mLampeChevetFreqAttenuation;
		} else {
			mLampeChevetDAngle -= max * mLampeChevetFreqAttenuation;
		}
		Matrix.setRotateM(mLampeChevetDRotationMatrix, 0, mLampeChevetDAngle, 0f, 1f, 0f);
		float[] tmpMat = mModelMatrix.clone();
		Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, mLampeChevetDRotationMatrix, 0);

		Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

		mLampeChevetDModel = mModelMatrix.clone();
	}

	private void updateLampeChevetG(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mLampeChevetGTranslateVector[0] * scale, mLampeChevetGTranslateVector[1] * scale, mLampeChevetGTranslateVector[2] * scale * -1f);

		float max = 0f;
		for (int j = 105; j < 206; j++) {
			if (max < freqArray[j]) {
				max = freqArray[j];
			}
		}
		if (mCounter % 2 == 1) {
			mLampeChevetGAngle += max * mLampeChevetFreqAttenuation;
		} else {
			mLampeChevetGAngle -= max * mLampeChevetFreqAttenuation;
		}
		Matrix.setRotateM(mLampeChevetGRotationMatrix, 0, mLampeChevetGAngle, 0f, 1f, 0f);
		float[] tmpMat = mModelMatrix.clone();
		Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, mLampeChevetDRotationMatrix, 0);

		Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

		mLampeChevetGModel = mModelMatrix.clone();
	}

	private void updateLampePlafond(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mLampePlafondTranslateVector[0] * scale, mLampePlafondTranslateVector[1] * scale, mLampePlafondTranslateVector[2] * scale * -1f);

		float max = 0f;
		for (int i = 206; i < 307; i++) {
			if (freqArray[i] > max) {
				max = freqArray[i];
			}
		}
		float tmpScale = scale + max * mLampePlafondAtenuation;
		tmpScale = tmpScale <= 2f ? tmpScale : 2f;
		Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

		mLampePlafondModel = mModelMatrix.clone();
	}

	private void updateLit2Places(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mLit2PlacesTranslateVector[0] * scale, mLit2PlacesTranslateVector[1] * scale, mLit2PlacesTranslateVector[2] * scale * -1f);

		float tmpScale;
		if ((freqArray[0] > 0.2f || freqArray[1] > 0.2f) && mCounter % 2 == 0) {
			tmpScale = scale + scale * mLitVibrationAtenuation;
		} else {
			tmpScale = scale;
		}
		Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

		mLit2PlacesModel = mModelMatrix.clone();
	}

	private void updateMurs() {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, mMursTranslateVector[0] * scale, mMursTranslateVector[1] * scale, mMursTranslateVector[2] * scale * -1f);
		Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);
		mMursModel = mModelMatrix.clone();
	}

	private void updateOreillerD(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mOreillerDTranslateVector[0] * scale, mOreillerDTranslateVector[1] * scale, mOreillerDTranslateVector[2] * scale * -1f);

		float max = 0f;
		for (int i = 3; i < 5; i++) {
			if (max < freqArray[i]) {
				max = freqArray[i];
			}
		}
		float tmpScale = scale + max * mOreillerFreqAtenuation * scale;
		tmpScale = tmpScale <= 2f ? tmpScale : 2f;
		Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

		mOreillerDModel = mModelMatrix.clone();
	}

	private void updateOreillerG(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mOreillerGTranslateVector[0] * scale, mOreillerGTranslateVector[1] * scale, mOreillerGTranslateVector[2] * scale * -1f);

		float max = 0f;
		for (int i = 5; i < 7; i++) {
			if (max < freqArray[i]) {
				max = freqArray[i];
			}
		}
		float tmpScale = scale + max * mOreillerFreqAtenuation * scale;
		tmpScale = tmpScale <= 2f ? tmpScale : 2f;
		Matrix.scaleM(mModelMatrix, 0, tmpScale, tmpScale, tmpScale);

		mOreillerGModel = mModelMatrix.clone();
	}

	private void updatePorte(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mPorteTranslateVector[0] * scale, mPorteTranslateVector[1] * scale, mPorteTranslateVector[2] * scale * -1f);

		float tmpScale;
		if ((freqArray[1] > 0.2f || freqArray[2] > 0.2f) && mCounter % 2 == 0) {
			tmpScale = scale + scale * mPorteVibrationAtenuation;
		} else {
			tmpScale = scale;
		}
		Matrix.scaleM(mModelMatrix, 0, tmpScale, scale, scale);

		mPorteModel = mModelMatrix.clone();
	}

	private void updateStores(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mStoresTranslateVector[0] * scale, mStoresTranslateVector[1] * scale, mStoresTranslateVector[2] * scale * -1f);

		float tmpScale;
		if ((freqArray[1] > 0.2f || freqArray[2] > 0.2f) && mCounter % 2 == 0) {
			tmpScale = scale + scale * mStoresVibrationAtenuation;
		} else {
			tmpScale = scale;
		}
		Matrix.scaleM(mModelMatrix, 0, tmpScale, scale, scale);

		mStoresModel = mModelMatrix.clone();
	}

	private void updateTableChevetD(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mTableChevetDTranslateVector[0] * scale, mTableChevetDTranslateVector[1] * scale, mTableChevetDTranslateVector[2] * scale * -1f);

		float max = 0f;
		for (int i = 22; i < 33; i++) {
			if (max < freqArray[i]) {
				max = freqArray[i];
			}
		}
		float tmpScale = scale + max * mTableChevetFreqAtenuation * scale;
		tmpScale = tmpScale <= 2f ? tmpScale : 2f;
		Matrix.scaleM(mModelMatrix, 0, tmpScale, scale, tmpScale);

		mTableChevetDModel = mModelMatrix.clone();
	}

	private void updateTableChevetG(float[] freqArray) {
		float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);

		Matrix.translateM(mModelMatrix, 0, mTableChevetGTranslateVector[0] * scale, mTableChevetGTranslateVector[1] * scale, mTableChevetGTranslateVector[2] * scale * -1f);

		float max = 0f;
		for (int i = 33; i < 44; i++) {
			if (max < freqArray[i]) {
				max = freqArray[i];
			}
		}
		float tmpScale = scale + max * mTableChevetFreqAtenuation * scale;
		tmpScale = tmpScale <= 2f ? tmpScale : 2f;
		Matrix.scaleM(mModelMatrix, 0, tmpScale, scale, tmpScale);

		mTableChevetGModel = mModelMatrix.clone();
	}

	private void count() {
		mCounter--;
		if (mCounter <= 0) {
			mCounter = mMaxCounter;
		}
	}
}
