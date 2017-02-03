package com.samuelberrien.spectrix.obj.visualization;

import android.content.Context;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.obj.ObjModel;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by samuel on 03/02/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Explosion {

    private Context context;

    private final float LIGHTAUGMENTATION = 10f;

    private Random rand;

    private float minDist;
    private float rangeDist;

    private int nbCenter;
    private int nbSameCenter;
    private float[][] mCenterPoint;

    private ObjModel octagone;
    private ArrayList<Float> mOctagoneAngle;
    private ArrayList<float[]> mOctagoneRotationOrientation;
    private ArrayList<float[]> mOctagoneRotationMatrix;
    private ArrayList<float[]> mOctagoneTranslateVector;
    private ArrayList<float[]> mOctagoneSpeedVector;
    private ArrayList<float[]> mOctagoneModelMatrix;

    public Explosion(Context context, float minDist, float rangeDist){
        this.context = context;
        this.minDist = minDist;
        this.rangeDist = rangeDist;
        this.octagone = new ObjModel(this.context, R.raw.octagone, 1f, 1f, 1f, LIGHTAUGMENTATION);
        this.mOctagoneAngle = new ArrayList<>();
        this.mOctagoneRotationOrientation = new ArrayList<>();
        this.mOctagoneRotationMatrix = new ArrayList<>();
        this.mOctagoneTranslateVector = new ArrayList<>();
        this.mOctagoneSpeedVector = new ArrayList<>();
        this.mOctagoneModelMatrix = new ArrayList<>();
    }
}
