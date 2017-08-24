package com.samuelberrien.spectrix.obj.normal.renderers;

import android.content.Context;

import com.samuelberrien.spectrix.obj.normal.ObjGLRenderer;
import com.samuelberrien.spectrix.obj.visualization.Icosahedron;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 10/01/17.
 */

public class ObjGLRendererIcosahedron extends ObjGLRenderer {

	private Icosahedron icosahedronVisualization;

	public ObjGLRendererIcosahedron(Context context) {
		super(context);
	}

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		super.onSurfaceCreated(unused, config);
		this.icosahedronVisualization = new Icosahedron(this.context, 256, 2, 20f, 20f);
	}

	public void update(float[] freqArray) {
		if (this.icosahedronVisualization != null) {
			this.icosahedronVisualization.updateFreq(freqArray);
		}
		this.updateLight(0f, 0f, 0f);
	}

	public void onDrawFrame(GL10 unused) {
		super.onDrawFrame(unused);
		this.icosahedronVisualization.updateIcosahedrons();
		this.icosahedronVisualization.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace, super.mCameraPosition);
	}
}
