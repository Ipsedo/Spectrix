package com.samuelberrien.spectrix.obj.normal.renderers;

import android.content.Context;

import com.samuelberrien.spectrix.obj.normal.ObjGLRenderer;
import com.samuelberrien.spectrix.obj.visualization.Room;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 07/01/17.
 */

public class ObjGLRendererRoom extends ObjGLRenderer {

    private Room roomVisualization;

    private final float SCALE = 1f;

    public ObjGLRendererRoom(Context context){
        super(context);
        this.mCameraZ = -2f * SCALE;
        this.mCameraY = 6f * SCALE;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);
        this.roomVisualization = new Room(this.context, SCALE);
    }

    public void update(float[] freqArray) {
        float tmp[] = new float[3];
        if(this.roomVisualization != null) {
            this.roomVisualization.update(freqArray);
            tmp = this.roomVisualization.getLightPos();
        }
        this.updateLight(tmp[0], tmp[1], tmp[2]);
    }

    public void onDrawFrame(GL10 unused) {
        super.onDrawFrame(unused);
        this.roomVisualization.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace);
    }
}
