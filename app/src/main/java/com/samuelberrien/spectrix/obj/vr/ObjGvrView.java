package com.samuelberrien.spectrix.obj.vr;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;

import com.google.vr.sdk.base.GvrView;
import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.obj.vr.renderers.ObjStereoRendererCanyon;
import com.samuelberrien.spectrix.obj.vr.renderers.ObjStereoRendererExplosion;
import com.samuelberrien.spectrix.obj.vr.renderers.ObjStereoRendererIcosahedron;
import com.samuelberrien.spectrix.obj.vr.renderers.ObjStereoRendererRoom;
import com.samuelberrien.spectrix.obj.vr.renderers.ObjStereoRendererSnow;

/**
 * Created by samuel on 11/01/17.
 */

public class ObjGvrView extends GvrView {

    private ObjStereoRenderer mRenderer;

    private Visualizer mVisualizer;
    private MediaPlayer mPlayer;

    private GetFFT getFft;

    private boolean useSample;

    /**
     *
     * @param context
     * @param useSample
     * @param idVisualisation
     */
    public ObjGvrView(Context context, boolean useSample, String idVisualisation){
        super(context);
        setEGLContextClientVersion(2);
        setTransitionViewEnabled(true);

        if(idVisualisation.equals(context.getString(R.string.icosahedron))) {
            this.mRenderer = new ObjStereoRendererIcosahedron(context);
        }else if(idVisualisation.equals(context.getString(R.string.room))){
            this.mRenderer = new ObjStereoRendererRoom(context);
        }else if(idVisualisation.equals(context.getString(R.string.snow))){
            this.mRenderer = new ObjStereoRendererSnow(context);
        }else if(idVisualisation.equals(context.getString(R.string.explosion))){
            this.mRenderer = new ObjStereoRendererExplosion(context);
        } else if(idVisualisation.equals(context.getString(R.string.canyon))) {
            this.mRenderer = new ObjStereoRendererCanyon(context);
        }

        setRenderer(this.mRenderer);

        this.useSample = useSample;
        if(this.useSample) {
            this.mPlayer = new MediaPlayer().create(context, R.raw.crea_session_8);
            this.mPlayer.setLooping(true);
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
                byte[] bytes = new byte[ObjGvrView.this.mVisualizer.getCaptureSize()];
                ObjGvrView.this.mVisualizer.getFft(bytes);
                float[] fft = new float[bytes.length / 2];
                for (int i = 0; i < fft.length; i++) {
                    float real = (float) (bytes[(i * 2) + 0]) / 128.0f;
                    float imag = (float) (bytes[(i * 2) + 1]) / 128.0f;
                    fft[i] = (real * real) + (imag * imag);
                }
                ObjGvrView.this.mRenderer.update(fft);
                try {
                    Thread.sleep(1000L / 120L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
