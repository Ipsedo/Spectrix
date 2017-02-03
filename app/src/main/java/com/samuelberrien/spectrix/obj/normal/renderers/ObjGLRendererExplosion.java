package com.samuelberrien.spectrix.obj.normal.renderers;

import android.content.Context;

import com.samuelberrien.spectrix.obj.normal.ObjGLRenderer;
import com.samuelberrien.spectrix.obj.visualization.Explosion;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 03/02/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class ObjGLRendererExplosion extends ObjGLRenderer {

    private Explosion explosionVisualization;

    public ObjGLRendererExplosion(Context context){
        super(context);
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);
        this.explosionVisualization = new Explosion(this.context, 128, 2, 2, 20f, 20f);
    }

    public void update(float[] freqArray) {
        if(this.explosionVisualization != null) {
            this.explosionVisualization.update(freqArray);
        }
        this.updateLight(0f, 0f, 0f);
    }

    public void onDrawFrame(GL10 unused) {
        super.onDrawFrame(unused);
        this.explosionVisualization.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace);
    }
}
