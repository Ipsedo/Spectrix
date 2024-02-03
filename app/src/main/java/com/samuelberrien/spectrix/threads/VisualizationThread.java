package com.samuelberrien.spectrix.threads;

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

    //private long lastTime;

    VisualizationThread(String name, Visualization visualization) {
        super(name);
        this.visualization = visualization;
        isCanceled = false;
        //lastTime = System.currentTimeMillis();
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

    private static double arcsinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1));
    }

    protected float getBarkScale(int frequencyIndex, int frequencyNumber) {

        int minHertz = 0;
        int maxHertz = getSampleRate() / 2;

        double currHertz = frequencyIndex * (maxHertz - minHertz) / (double) frequencyNumber;
        double barkScale = 6. * arcsinh(currHertz / 600.);

        return (float) barkScale;
    }

    protected abstract void work(Visualization visualization);

    protected abstract void onEnd();

    protected abstract float[] getFrequencyMagns();

    protected abstract Long getTimeToWait();

    protected abstract int getSampleRate();

}
