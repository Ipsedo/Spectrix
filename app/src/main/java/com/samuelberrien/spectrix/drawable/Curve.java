package com.samuelberrien.spectrix.drawable;

public class Curve {

    private final int n;

    private final float[] x;
    private final float[] y;

    public Curve(int n) {
        this.n = n;

        this.x = new float[this.n];
        this.y = new float[this.n];

        float min = -1.f;
        float max = 1.f;

        float range = max - min;

        for (int i = 0; i < x.length; i++)
            x[i] = min + i * range / this.n;
    }

    public void update(float[] y) {
        System.arraycopy(y, 0, this.y, 0, this.y.length);
    }

    public void draw(float[] mvpMatrix) {

    }
}
