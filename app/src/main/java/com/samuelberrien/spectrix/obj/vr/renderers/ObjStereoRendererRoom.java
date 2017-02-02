package com.samuelberrien.spectrix.obj.vr.renderers;

import android.content.Context;

import com.google.vr.sdk.base.Eye;
import com.samuelberrien.spectrix.obj.visualization.Room;
import com.samuelberrien.spectrix.obj.vr.ObjStereoRenderer;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by samuel on 14/01/17.
 */

public class ObjStereoRendererRoom extends ObjStereoRenderer{

    private final float SCALE = 1f;

    private Room roomVisualization;

    public ObjStereoRendererRoom(Context context){
        super(context);
        this.mCameraZ = -2f * SCALE;
        this.mCameraY = 6f * SCALE;
    }

    public void onSurfaceCreated(EGLConfig config) {
        super.onSurfaceCreated(config);
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

    public void onDrawEye(Eye eye) {
        super.onDrawEye(eye);
        this.roomVisualization.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace);
    }
}
