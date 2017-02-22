package com.samuelberrien.spectrix.obj.normal.renderers;

import android.content.Context;

import com.samuelberrien.spectrix.obj.normal.ObjGLRenderer;
import com.samuelberrien.spectrix.obj.visualization.Snow;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 07/01/17.
 */

public class ObjGLRendererSnow extends ObjGLRenderer {

    private Snow snowVisualization;

    public ObjGLRendererSnow(Context context){
        super(context);
        this.mCameraY = 2f;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);
        this.snowVisualization = new Snow(this.context, 16, 16, 3, 16);
    }

    public void update(float[] freqArray) {
        if(this.snowVisualization != null) {
            this.snowVisualization.update(freqArray);
        }
        this.updateLight(0f, 0f, 0f);
    }

    public void onDrawFrame(GL10 unused) {
        super.onDrawFrame(unused);
        this.snowVisualization.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace, new float[]{this.mCameraX, this.mCameraY, this.mCameraZ});
    }

}
