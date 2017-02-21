package com.samuelberrien.spectrix.obj.normal;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.opengl.GLSurfaceView;

import android.os.AsyncTask;
import android.view.MotionEvent;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.obj.normal.renderers.ObjGLRendererExplosion;
import com.samuelberrien.spectrix.obj.normal.renderers.ObjGLRendererIcosahedron;
import com.samuelberrien.spectrix.obj.normal.renderers.ObjGLRendererRoom;
import com.samuelberrien.spectrix.obj.normal.renderers.ObjGLRendererSnow;
import com.samuelberrien.spectrix.obj.normal.renderers.ObjGLRendererTextSpec;

/**
 * Created by samuel on 05/01/17.
 */

public class ObjGLSurfaceView extends GLSurfaceView {

    private ObjGLRenderer mRenderer;

    private Visualizer mVisualizer;
    private MediaPlayer mPlayer;

    private GetFFT getFft;

    private boolean useSample;

    /**
     *
     * @param context
     * @param useSample
     * @param id_visualisation
     */
    public ObjGLSurfaceView(Context context, boolean useSample, String id_visualisation) {
        super(context);
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        if(id_visualisation.equals(context.getString(R.string.snow))){
            this.mRenderer = new ObjGLRendererSnow(context);
        } else if(id_visualisation.equals(context.getString(R.string.room))){
            this.mRenderer = new ObjGLRendererRoom(context);
        }else if(id_visualisation.equals(context.getString(R.string.icosahedron))){
            this.mRenderer = new ObjGLRendererIcosahedron(context);
        }else if(id_visualisation.equals(context.getString(R.string.explosion))){
            this.mRenderer = new ObjGLRendererExplosion(context);
        }else if(id_visualisation.equals(context.getString(R.string.test))){
            this.mRenderer = new ObjGLRendererTextSpec(context);
        }

        setRenderer(this.mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        this.useSample = useSample;
        if(this.useSample) {
            this.mPlayer = new MediaPlayer().create(context, R.raw.crea_session_8);
            this.mPlayer.start();
        }

        this.setupVisualizerAndAsyncTask();
    }

    /**
     *
     */
    private void setupVisualizerAndAsyncTask() {
        if(this.mVisualizer == null){
            this.mVisualizer = new Visualizer(this.useSample ? this.mPlayer.getAudioSessionId() : 0);
            this.mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            this.mVisualizer.setEnabled(true);
        }
        this.getFft = new GetFFT();
        this.getFft.execute();
    }

    private final float TOUCH_SCALE_FACTOR = 0.001f;
    private float mPreviousX;
    private float mPreviousY;
    private float mPreviousX1;
    private float mPreviousX2;
    private float mPreviousY1;
    private float mPreviousY2;
    private float mPreviousZoom;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(e.getPointerCount() == 1) {
            float x = e.getX() + 1f;
            float y = e.getY() + 1f;
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPreviousX = e.getX();
                    mPreviousY = e.getY();
                case MotionEvent.ACTION_MOVE:
                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;
                    mRenderer.updateCameraOrientation(dy * TOUCH_SCALE_FACTOR, dx * TOUCH_SCALE_FACTOR);
                    requestRender();
            }
            mPreviousX = x;
            mPreviousY = y;
        } else if(e.getPointerCount() == 2) {
            /*float x1 = e.getX(e.getPointerId(0));
            float x2 = e.getX(e.getPointerId(1));
            float y1 = e.getY(e.getPointerId(0));
            float y2 = e.getY(e.getPointerId(1));
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPreviousX1 = e.getX(e.getPointerId(0));
                    mPreviousX2 = e.getX(e.getPointerId(1));
                    mPreviousY1 = e.getY(e.getPointerId(0));
                    mPreviousY2 = e.getY(e.getPointerId(1));
                case MotionEvent.ACTION_MOVE:
                    float dx1 = x1 - mPreviousX1;
                    float dx2 = x2 - mPreviousX2;
                    float dy1 = y1 - mPreviousY1;
                    float dy2 = y2 - mPreviousY2;
                    mRenderer.updateZoom((float) Math.sqrt(dx1 * dx2 + dy1 * dy2) * TOUCH_SCALE_FACTOR);
            }
            mPreviousX1 = x1;
            mPreviousX2 = x2;
            mPreviousY1 = y1;
            mPreviousY2 = y2;*/

        }
        return true;
    }

    public void onPause(){
        this.getFft.cancel(true);
        this.mVisualizer.setEnabled(false);
        if(this.useSample) {
            this.mPlayer.pause();
        }
        super.onPause();
    }

    public void onResume(){
        super.onResume();
        if(this.useSample) {
            this.mPlayer.start();
        }
        if(this.getFft.isCancelled()){
            this.getFft.cancel(false);
        }
        if(!this.mVisualizer.getEnabled()){
            this.mVisualizer.setEnabled(true);
        }
        if(this.getFft.getStatus() == AsyncTask.Status.FINISHED){
            this.getFft = new GetFFT();
            this.getFft.execute();
        }
    }

    private class GetFFT extends AsyncTask<String,Void,Void> {
        @Override
        public Void doInBackground(String[] param) {
            while (!this.isCancelled()) {
                byte[] bytes = new byte[ObjGLSurfaceView.this.mVisualizer.getCaptureSize()];
                ObjGLSurfaceView.this.mVisualizer.getFft(bytes);
                float[] fft = new float[bytes.length / 2];
                for (int i = 0; i < fft.length; i++) {
                    float real = (float) (bytes[(i * 2) + 0]) / 128.0f;
                    float imag = (float) (bytes[(i * 2) + 1]) / 128.0f;
                    fft[i] = (real * real) + (imag * imag);
                }
                ObjGLSurfaceView.this.mRenderer.update(fft);
                try {
                    Thread.sleep(1000l / 120l);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

}
