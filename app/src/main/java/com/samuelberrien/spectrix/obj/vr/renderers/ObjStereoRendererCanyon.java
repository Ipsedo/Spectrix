package com.samuelberrien.spectrix.obj.vr.renderers;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.HeadTransform;
import com.samuelberrien.spectrix.obj.visualization.Canyon;
import com.samuelberrien.spectrix.obj.vr.ObjStereoRenderer;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by samuel on 03/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class ObjStereoRendererCanyon extends ObjStereoRenderer {

	private Canyon canyonVisualization;

	private float mapScale = 100f;

	public ObjStereoRendererCanyon(Context context) {
		super(context);
		this.mCameraY = 4f;
	}

	public void onSurfaceCreated(EGLConfig config) {
		super.onSurfaceCreated(config);
		this.canyonVisualization = new Canyon(this.context, this.mapScale);
	}

	public void update(float[] freqArray) {
		if (this.canyonVisualization != null) {
			this.canyonVisualization.update(freqArray);
		}
		this.updateLight(0f, 5 * this.mapScale, 0f);
	}

	public void onNewFrame(HeadTransform headTransform) {
		super.onNewFrame(headTransform);
		GLES20.glClearColor(0.5f, 0.9f, 0.9f, 1.0f);
		this.canyonVisualization.updateCanyon();
	}

	public void onDrawEye(Eye eye) {
		super.onDrawEye(eye);
		float[] cam = new float[4];
		Matrix.multiplyMV(cam, 0, this.mViewMatrix, 0, new float[]{this.mCameraX, this.mCameraY, this.mCameraZ, 1.0f}, 0);
		this.canyonVisualization.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace, new float[]{cam[0], cam[1], cam[2]});
	}
}
