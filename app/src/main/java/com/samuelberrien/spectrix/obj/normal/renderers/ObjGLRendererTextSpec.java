package com.samuelberrien.spectrix.obj.normal.renderers;

import android.content.Context;
import android.opengl.GLES20;

import com.samuelberrien.spectrix.obj.normal.ObjGLRenderer;
import com.samuelberrien.spectrix.obj.visualization.TestSpecular;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 21/02/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class ObjGLRendererTextSpec extends ObjGLRenderer {
	private TestSpecular test;

	public ObjGLRendererTextSpec(Context context) {
		super(context);
		super.mCameraPosition[1] = 4f;
	}

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		super.onSurfaceCreated(unused, config);
		GLES20.glClearColor(0.5f, 0.9f, 0.9f, 1.0f);
		this.test = new TestSpecular(this.context);
	}

	public void update(float[] freqArray) {
		if (this.test != null) {
			this.test.update();
		}
		this.updateLight(0f, 5f * 50f, 0f);
	}

	public void onDrawFrame(GL10 unused) {
		super.onDrawFrame(unused);
		this.test.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace, super.mCameraPosition);
	}
}
