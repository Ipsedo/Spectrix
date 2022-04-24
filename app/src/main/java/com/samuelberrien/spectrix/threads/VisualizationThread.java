package com.samuelberrien.spectrix.threads;

import androidx.core.util.Pair;

import com.samuelberrien.spectrix.utils.Visualization;

/**
 * Created by samuel on 30/08/17.
 */

public abstract class VisualizationThread extends Thread {

    //private static long TIME_TO_WAIT = 30L;

    public static final int STREAM_MUSIC = 0;
    public static final int MIC_MUSIC = 1;
    public static final int NONE = 3;

    private final Visualization visualization;

    private boolean isCanceled;

    VisualizationThread(String name, Visualization visualization) {
        super(name);
        this.visualization = visualization;
        isCanceled = false;
    }

    public void cancel() {
        isCanceled = true;
    }

    @Override
    public void run() {
        while (!isCanceled && !visualization.isInit()) {
            try {
                Thread.sleep(getTimeToWait());
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

        while (!isCanceled) {
            long t1 = System.currentTimeMillis();
            work(visualization);
            try {
                long toWait = getTimeToWait() - (System.currentTimeMillis() - t1);
                Thread.sleep(toWait >= 0 ? toWait : 0);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

        onEnd();
    }

    protected abstract void work(Visualization visualization);

    protected abstract void onEnd();

    protected abstract Pair<float[], float[]> getFrequency();

    protected abstract Long getTimeToWait();

}
