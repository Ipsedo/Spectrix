package com.samuelberrien.spectrix.obj.normal.renderers;

import android.content.Context;
import android.opengl.GLES20;

import com.samuelberrien.spectrix.obj.normal.ObjGLRenderer;
import com.samuelberrien.spectrix.obj.visualization.Canyon;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 03/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class ObjGLRendererCanyon extends ObjGLRenderer {

	private Canyon canyonVisualization;
	private float mapScale = 100f;

	public ObjGLRendererCanyon(Context context) {
		super(context);
		super.mCameraPosition[1] = 4f;
	}

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		super.onSurfaceCreated(unused, config);
		GLES20.glClearColor(0.5f, 0.9f, 0.9f, 1.0f);
		this.canyonVisualization = new Canyon(this.context, this.mapScale);
	}

	public void update(float[] freqArray) {
		if (this.canyonVisualization != null) {
			//this.explosionVisualization.update(freqArray, this.mCameraDirection[0], this.mCameraDirection[1], this.mCameraDirection[2]);
			this.canyonVisualization.update(freqArray);
		}
		super.updateLight(0f, 2.5f * this.mapScale, 0f);
	}

	public void onDrawFrame(GL10 unused) {
		super.onDrawFrame(unused);
		this.canyonVisualization.updateCanyon();
		this.canyonVisualization.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace, super.mCameraPosition);
	}
}
