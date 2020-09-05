package com.samuelberrien.spectrix.threads;

import android.media.audiofx.Visualizer;

import com.samuelberrien.spectrix.utils.Visualization;

/**
 * Created by samuel on 23/08/17.
 */

public class StreamThread extends VisualizationThread {

    protected Visualizer visualizer;


    public StreamThread(Visualization visualization) {
        super("StreamThread", visualization);

        visualizer = new Visualizer(0);
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setEnabled(true);
    }

    @Override
    protected void work(Visualization visualization) {
        visualization.update(getFrequencyMagns());
    }

    @Override
    protected void onEnd() {
        visualizer.release();
    }

    @Override
    protected float[] getFrequencyMagns() {
        byte[] bytes = new byte[visualizer.getCaptureSize()];
        visualizer.getFft(bytes);
        float[] fft = new float[bytes.length / 2];

        float inv128f = 1f / 128f;
        for (int i = 0; i < fft.length; i++) {
            float real = (float) (bytes[i * 2]) * inv128f;
            float imag = (float) (bytes[i * 2 + 1]) * inv128f;
            fft[i] = (real * real) + (imag * imag);
            fft[i] += fft[i] * i * 0.3f;
        }
        return fft;
    }

    @Override
    protected Long getTimeToWait() {
        return 30L;
    }
}
