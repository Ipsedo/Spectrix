package com.samuelberrien.spectrix.obj.vr.renderers;

import android.content.Context;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.HeadTransform;
import com.samuelberrien.spectrix.obj.visualization.Explosion;
import com.samuelberrien.spectrix.obj.vr.ObjStereoRenderer;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by samuel on 04/02/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class ObjStereoRendererExplosion extends ObjStereoRenderer {

	private Explosion explosionVisualization;

	public ObjStereoRendererExplosion(Context context) {
		super(context);
	}

	public void onSurfaceCreated(EGLConfig config) {
		super.onSurfaceCreated(config);
		this.explosionVisualization = new Explosion(this.context, 128, 2, 5, 1.25f, 5f, 2.5f);
	}

	public void update(float[] freqArray) {
		if (this.explosionVisualization != null) {
			this.explosionVisualization.update(freqArray, 0f, 0f, 0.5f);
		}
		this.updateLight(0f, 0f, 0f);
	}

	public void onNewFrame(HeadTransform headTransform) {
		super.onNewFrame(headTransform);
		this.explosionVisualization.updateVisualization();
	}

	public void onDrawEye(Eye eye) {
		super.onDrawEye(eye);
		this.explosionVisualization.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace);
	}
}
