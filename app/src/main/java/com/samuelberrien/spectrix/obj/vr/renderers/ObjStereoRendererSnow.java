package com.samuelberrien.spectrix.obj.vr.renderers;

import android.content.Context;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.HeadTransform;
import com.samuelberrien.spectrix.obj.visualization.Snow;
import com.samuelberrien.spectrix.obj.vr.ObjStereoRenderer;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by samuel on 18/01/17.
 */

public class ObjStereoRendererSnow extends ObjStereoRenderer {

    private Snow snowVisualization;

    public ObjStereoRendererSnow(Context context){
        super(context);
        this.mCameraY = 2f;
    }

    public void onSurfaceCreated(EGLConfig config) {
        super.onSurfaceCreated(config);
        this.snowVisualization = new Snow(this.context, 10, 16, 3, 10);
    }

    public void update(float[] freqArray) {
        if(this.snowVisualization != null) {
            this.snowVisualization.update(freqArray);
        }
        this.updateLight(0f, 2f, 0f);
    }

    public void onNewFrame(HeadTransform headTransform) {
        super.onNewFrame(headTransform);
        this.snowVisualization.updateSnow();
    }

    public void onDrawEye(Eye eye) {
        super.onDrawEye(eye);
        this.snowVisualization.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace, new float[]{this.mCameraX, this.mCameraY, this.mCameraZ});
    }
}
