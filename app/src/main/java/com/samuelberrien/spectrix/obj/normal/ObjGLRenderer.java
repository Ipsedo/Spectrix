package com.samuelberrien.spectrix.obj.normal;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.obj.Light;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by samuel on 05/01/17.
 */

public abstract class ObjGLRenderer implements GLSurfaceView.Renderer {

    protected Context context;

    protected final float[] mProjectionMatrix = new float[16];
    protected final float[] mViewMatrix = new float[16];

    private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    protected final float[] mLightPosInEyeSpace = new float[4];
    private final float[] mLightModelMatrix = new float[16];
    private final float[] mLightPosInWorldSpace = new float[4];

    protected float mCameraX = 0f;
    protected float mCameraY = 0f;
    protected float mCameraZ = 0f;
    protected float[] mCameraDirection = new float[3];
    private float phi = 0f;
    private float theta = 0f;
    private float maxRange = 1f;
    private float projectionAngle = 40f;
    private float ratio = 1f;

    /**
     *
     * @param context
     */
    public ObjGLRenderer(Context context){
        this.context = context;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        this.mCameraDirection = new float[]{mCameraX, mCameraY, mCameraZ + 1f};
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * update the camera look point with orientation angles
     * @param phi angle phi
     * @param theta angle theta
     */
    public void updateCameraOrientation(float phi, float theta){
        this.phi += phi;
        this.theta += theta;
        /*if(Math.sin(90) == 1){
            if(this.phi > 360f){
                this.phi -= 360f;
            }
            if(this.phi < 0){
                this.phi += 360f;
            }
            if(this.theta > 360f){
                this.theta -= 360f;
            }
            if(this.theta < 0){
                this.theta += 360f;
            }
        }else{
            if(this.phi > Math.PI * 2){
                this.phi -= Math.PI * 2;
            }
            if(this.phi < 0){
                this.phi += Math.PI * 2;
            }
            if(this.theta > Math.PI * 2){
                this.theta -= Math.PI * 2;
            }
            if(this.theta < 0) {
                this.theta += Math.PI * 2;
            }
        }*/

        if(this.phi > Math.PI * 2){
            this.phi -= Math.PI * 2;
        }
        if(this.phi < 0){
            this.phi += Math.PI * 2;
        }
        if(this.theta > Math.PI * 2){
            this.theta -= Math.PI * 2;
        }
        if(this.theta < 0) {
            this.theta += Math.PI * 2;
        }
        if((this.phi > Math.toRadians(80) && this.phi < Math.toRadians(100)) || (this.phi > Math.toRadians(260) && this.phi < Math.toRadians(280))) {
            this.phi -= phi * 2;
        }

        this.mCameraDirection[0] = this.maxRange * (float) (Math.cos(this.phi) * Math.sin(this.theta)) + this.mCameraX;
        this.mCameraDirection[1] = this.maxRange * (float) Math.sin(this.phi) + this.mCameraY;
        this.mCameraDirection[2] = this.maxRange * (float) (Math.cos(this.phi) * Math.cos(this.theta)) + this.mCameraZ;
    }

    /**
     *
     * @param dist
     */
    public void updateZoom(float dist){
        this.projectionAngle += dist;
        if(this.projectionAngle < 10){
            this.projectionAngle = 10;
        }
        if(this.projectionAngle > 100){
            this.projectionAngle = 100;
        }
        this.updateProjection();
    }

    /**
     *
     * @param freqArray
     */
    protected abstract void update(float[] freqArray);

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    protected void updateLight(float x, float y, float z){
        Matrix.setIdentityM(this.mLightModelMatrix, 0);
        Matrix.translateM(this.mLightModelMatrix, 0, x, y, z);
        Matrix.multiplyMV(this.mLightPosInWorldSpace, 0, this.mLightModelMatrix, 0, this.mLightPosInModelSpace, 0);
        Matrix.multiplyMV(this.mLightPosInEyeSpace, 0, this.mViewMatrix, 0, this.mLightPosInWorldSpace, 0);
    }

    public void onDrawFrame(GL10 unused) {
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(this.mViewMatrix, 0, this.mCameraX, this.mCameraY, this.mCameraZ, this.mCameraDirection[0], this.mCameraDirection[1], this.mCameraDirection[2], 0f, 1f, 0f);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        this.ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        //Matrix.frustumM(mProjectionMatrix, 0, -this.ratio, this.ratio, -1, 1, 3, 50f);

        Matrix.perspectiveM(this.mProjectionMatrix, 0, this.projectionAngle, ratio, 1, 50f);
    }

    private void updateProjection(){
        Matrix.perspectiveM(this.mProjectionMatrix, 0, this.projectionAngle, this.ratio, 1, 50f);
    }
}
