package com.samuelberrien.spectrix.obj.vr.renderers;

import android.content.Context;

import com.google.vr.sdk.base.Eye;
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
            this.icosahedronVisualization.updateIcosahedrons(freqArray);
        }
        this.updateLight(0f, 0f, 0f);
    }

    public void onDrawEye(Eye eye) {
        super.onDrawEye(eye);
        this.icosahedronVisualization.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace, new float[]{this.mCameraX, this.mCameraY, this.mCameraZ});
    }
}
