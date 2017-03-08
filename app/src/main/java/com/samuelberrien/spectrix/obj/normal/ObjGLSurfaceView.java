package com.samuelberrien.spectrix.obj.normal;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.opengl.GLSurfaceView;

import android.os.AsyncTask;
import android.view.GestureDetector;
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

    private final float TOUCH_SCALE_FACTOR_MOVE = 0.001f;
    private final float TOUCH_SCALE_FACTOR_ZOOM = 0.05f;
    private float mPreviousX;
    private float mPreviousY;
    private float mPreviousZoom;
    private boolean isZooming;
    GestureDetector gestureDetector;
    private boolean isZoomingMore;

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

        this.isZooming = false;
        this.isZoomingMore = true;
        this.gestureDetector = new GestureDetector(context, new GestureListener());

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

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(e.getPointerCount() == 1) {
            if(this.isZooming){
                mPreviousX = e.getX() + 1f;
                mPreviousY = e.getY() + 1f;
                this.isZooming = false;
            }
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPreviousX = e.getX() + 1f;
                    mPreviousY = e.getY() + 1f;
                case MotionEvent.ACTION_MOVE:
                    float dx = e.getX() + 1f - mPreviousX;
                    float dy = e.getY() + 1f - mPreviousY;
                    mRenderer.updateCameraOrientation(dy * TOUCH_SCALE_FACTOR_MOVE, dx * TOUCH_SCALE_FACTOR_MOVE);
                    requestRender();
            }
            mPreviousX = e.getX() + 1f;
            mPreviousY = e.getY() + 1f;
        } else if(e.getPointerCount() == 2) {
            this.isZooming = true;
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPreviousZoom = (float) Math.sqrt(Math.pow(e.getX(e.getPointerId(0)) - e.getX(e.getPointerId(1)), 2d) + Math.pow(e.getY(e.getPointerId(0)) - e.getY(e.getPointerId(1)), 2d));
                case MotionEvent.ACTION_MOVE:
                    mRenderer.updateZoom(- ((float) Math.sqrt(Math.pow(e.getX(e.getPointerId(0)) - e.getX(e.getPointerId(1)), 2d) + Math.pow(e.getY(e.getPointerId(0)) - e.getY(e.getPointerId(1)), 2d)) - mPreviousZoom) * TOUCH_SCALE_FACTOR_ZOOM);
                    requestRender();
            }
            mPreviousZoom = (float) Math.sqrt(Math.pow(e.getX(e.getPointerId(0)) - e.getX(e.getPointerId(1)), 2d) + Math.pow(e.getY(e.getPointerId(0)) - e.getY(e.getPointerId(1)), 2d));
        }
        return gestureDetector.onTouchEvent(e);
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
                    Thread.sleep(1000L / 120L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if(ObjGLSurfaceView.this.isZoomingMore) {
                ObjGLSurfaceView.this.mRenderer.updateZoom(-50);
            } else {
                ObjGLSurfaceView.this.mRenderer.updateZoom(50);
            }
            ObjGLSurfaceView.this.requestRender();
            ObjGLSurfaceView.this.isZoomingMore = !ObjGLSurfaceView.this.isZoomingMore;
            return true;
        }
    }
}
