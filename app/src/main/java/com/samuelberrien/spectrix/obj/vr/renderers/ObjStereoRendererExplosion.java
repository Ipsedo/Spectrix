package com.samuelberrien.spectrix.obj.vr.renderers;

import android.content.Context;

import com.google.vr.sdk.base.Eye;
import com.samuelberrien.spectrix.obj.visualization.Explosion;
import com.samuelberrien.spectrix.obj.visualization.Icosahedron;
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

    public ObjStereoRendererExplosion(Context context){
        super(context);
        this.mCameraY = 2f;
    }

    public void onSurfaceCreated(EGLConfig config) {
        super.onSurfaceCreated(config);
        this.explosionVisualization = new Explosion(this.context, 128, 2, 5, 10f, 10f);
    }

    public void update(float[] freqArray) {
        if(this.explosionVisualization != null) {
            this.explosionVisualization.update(freqArray);
        }
        this.updateLight(0f, 0f, 0f);
    }

    public void onDrawEye(Eye eye) {
        super.onDrawEye(eye);
        this.explosionVisualization.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace);
    }
}
