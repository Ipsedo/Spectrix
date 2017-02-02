package com.samuelberrien.spectrix.spectrum;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;

import com.samuelberrien.spectrix.R;

/**
 * Created by samuel on 29/12/16.
 */

public class SpectrumGLSurfaceViewTest extends GLSurfaceView {

    private final SpectrumGLRendererLand mRendererLand = new SpectrumGLRendererLand();
    private final SpectrumGLRenderer mRenderer = new SpectrumGLRenderer();

    private GetFFT getFft;

    private MediaPlayer mPlayer;

    private boolean useSample;

    private boolean isPortrait;

    public SpectrumGLSurfaceViewTest(Context context, boolean useSample, boolean isPortrait){
        super(context);

        setEGLContextClientVersion(2);

        this.useSample = useSample;

        this.isPortrait = isPortrait;

        // Set the Renderer for drawing on the GLSurfaceView
        if(this.isPortrait) {
            setRenderer(mRenderer);
        }else{
            setRenderer(mRendererLand);
        }

        if(this.useSample) {
            this.mPlayer = MediaPlayer.create(context, R.raw.crea_session_8);
            this.mPlayer.start();
        }

        this.getFft = new GetFFT();
        this.getFft.execute();

    }

    public void onPause(){
        this.getFft.cancel(true);
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
        if(this.getFft.getStatus() == AsyncTask.Status.FINISHED){
            this.getFft = new GetFFT();
            this.getFft.execute();
        }
    }

    public void updateVolume(int keycode){
    }

    private class GetFFT extends AsyncTask<String,Void,Void> {

        private static final int RECORDER_SAMPLERATE = 8000;
        private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
        private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
        private AudioRecord recorder;
        private int bufferElements;
        private int bytesPerElement = 2; // 2 bytes in 16bit format

        private byte[] short2byte(short[] sData) {
            int shortArrsize = sData.length;
            byte[] bytes = new byte[shortArrsize * 2];
            for (int i = 0; i < shortArrsize; i++) {
                bytes[i * 2] = (byte) (sData[i] & 0x00FF);
                bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
                sData[i] = 0;
            }
            return bytes;
        }

        @Override
        protected Void doInBackground(String[] param) {
            this.bufferElements = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
            this.recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferElements * bytesPerElement);

            this.recorder.startRecording();

            short sData[] = new short[bufferElements];

            while(!this.isCancelled()) {
                this.recorder.read(sData, 0, bufferElements);
                System.out.println(sData.length + " " + sData[(int) (Math.random() * sData.length)]);
                try{
                    Thread.sleep(1000L / 120L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            this.recorder.stop();
            this.recorder.release();
            this.recorder = null;

            return null;
        }
    }
}
