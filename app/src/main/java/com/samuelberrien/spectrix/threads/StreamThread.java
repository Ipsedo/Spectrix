package com.samuelberrien.spectrix.threads;

import android.media.audiofx.Visualizer;

import androidx.core.util.Pair;

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
        Pair<float[], float[]> magnPhase = getFrequency();
        visualization.update(magnPhase.first, magnPhase.second);
    }

    @Override
    protected void onEnd() {
        visualizer.release();
    }

    @Override
    protected Pair<float[], float[]> getFrequency() {
        byte[] bytes = new byte[visualizer.getCaptureSize()];
        visualizer.getFft(bytes);
        float[] magn = new float[bytes.length / 2];
        float[] phase = new float[bytes.length / 2];

        float inv128f = 1f / 128f;
        for (int i = 0; i < magn.length; i++) {
            float real = (float) (bytes[i * 2]) * inv128f;
            float imag = (float) (bytes[i * 2 + 1]) * inv128f;

            magn[i] = (real * real) + (imag * imag);
            magn[i] += magn[i] * i * 0.3f;

            phase[i] = (float) Math.atan(imag / real);
        }
        return new Pair<>(magn, phase);
    }

    @Override
    protected Long getTimeToWait() {
        return 30L;
    }
}
