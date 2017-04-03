package com.samuelberrien.spectrix.obj.vr.renderers;

import android.content.Context;
import android.opengl.Matrix;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.HeadTransform;
import com.samuelberrien.spectrix.obj.visualization.Icosahedron;
import com.samuelberrien.spectrix.obj.vr.ObjStereoRenderer;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by samuel on 11/01/17.
 */

public class ObjStereoRendererIcosahedron extends ObjStereoRenderer {

    private Icosahedron icosahedronVisualization;

    public ObjStereoRendererIcosahedron(Context context){
        super(context);
    }

    public void onSurfaceCreated(EGLConfig config) {
        super.onSurfaceCreated(config);
        this.icosahedronVisualization = new Icosahedron(this.context, 128, 2, 10f, 10f);
    }

    public void update(float[] freqArray) {
        if(this.icosahedronVisualization != null) {
            this.icosahedronVisualization.updateFreq(freqArray);
        }
        this.updateLight(0f, 0f, 0f);
    }

    public void onNewFrame(HeadTransform headTransform) {
        super.onNewFrame(headTransform);
        this.icosahedronVisualization.updateIcosahedrons();
    }

    public void onDrawEye(Eye eye) {
        super.onDrawEye(eye);
        float[] cam = new float[4];
        Matrix.multiplyMV(cam, 0, eye.getEyeView(), 0, new float[]{0.0f, 0.0f, 0.0f, 1.0f}, 0);
        this.icosahedronVisualization.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace, new float[]{cam[0], cam[1], cam[2]});
    }
}
