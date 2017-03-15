package com.samuelberrien.spectrix.spectrum;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.view.KeyEvent;

import com.samuelberrien.spectrix.R;

/**
 * Created by samuel on 15/12/16.
 */

public class SpectrumGLSurfaceView extends GLSurfaceView {

    private SpectrumGLRendererLand mRendererLand;
    private SpectrumGLRenderer mRenderer;

    private Visualizer mVisualizer;
    private MediaPlayer mPlayer;
    private AudioManager aManager;

    private float currVol;
    private float maxVol;

    private GetFFT getFft;

    private boolean useSample;

    private boolean isPortrait;

    public SpectrumGLSurfaceView(Context context, boolean useSample, boolean isPortrait) {
        super(context);
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        this.useSample = useSample;

        this.isPortrait = isPortrait;

        // Set the Renderer for drawing on the GLSurfaceView
        if (this.isPortrait) {
            this.mRenderer = new SpectrumGLRenderer();
            setRenderer(this.mRenderer);
        } else {
            this.mRendererLand = new SpectrumGLRendererLand();
            setRenderer(this.mRendererLand);
        }

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        //setPreserveEGLContextOnPause(true);

        this.aManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (this.useSample) {
            this.mPlayer = MediaPlayer.create(context, R.raw.crea_session_8);
            this.mPlayer.start();
        }
        this.currVol = this.aManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        this.maxVol = this.aManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //this.isStart = true;
        this.setupVisualizerAndAsyncTask();
    }

    private void setupVisualizerAndAsyncTask() {
        if (this.mVisualizer == null) {
            this.mVisualizer = new Visualizer(this.useSample ? this.mPlayer.getAudioSessionId() : 0);
            this.mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            this.mVisualizer.setEnabled(true);
        }
        this.getFft = new GetFFT();
        this.getFft.execute();
    }

    public void onPause() {
        this.getFft.cancel(true);
        this.mVisualizer.setEnabled(false);
        if (this.useSample) {
            this.mPlayer.pause();
        }
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        if (this.useSample) {
            this.mPlayer.start();
        }
        if (this.getFft.isCancelled()) {
            this.getFft.cancel(false);
        }
        if (this.getFft.getStatus() == AsyncTask.Status.FINISHED) {
            this.getFft = new GetFFT();
            this.getFft.execute();
        }
    }

    public void updateVolume(int keycode) {
        if (keycode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (this.currVol < this.maxVol) {
                this.currVol += 1.0f;
            }
        } else if (keycode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (this.currVol > 0) {
                this.currVol -= 1.0f;
            }
        }
    }

    private class GetFFT extends AsyncTask<String, Void, Void> {
        @Override
        public Void doInBackground(String[] param) {
            while (!this.isCancelled()) {
                byte[] bytes = new byte[SpectrumGLSurfaceView.this.mVisualizer.getCaptureSize()];
                SpectrumGLSurfaceView.this.mVisualizer.getFft(bytes);
                float[] fft = new float[bytes.length / 2];
                for (int i = 0; i < fft.length; i++) {
                    float real = (float) (bytes[(i * 2) + 0]) / 128.0f;
                    float imag = (float) (bytes[(i * 2) + 1]) / 128.0f;
                    fft[i] = ((real * real) + (imag * imag));// * maxVol) / (currVol >= 5 ? currVol : 5);
                }
                if (SpectrumGLSurfaceView.this.isPortrait) {
                    SpectrumGLSurfaceView.this.mRenderer.updateSquaresMoveMatrix(fft);
                } else {
                    SpectrumGLSurfaceView.this.mRendererLand.updateSquaresMoveMatrix(fft);
                }
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
