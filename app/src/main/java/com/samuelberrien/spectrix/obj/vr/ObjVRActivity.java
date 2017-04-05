package com.samuelberrien.spectrix.obj.vr;

import android.os.Bundle;
import com.google.vr.sdk.base.GvrActivity;
import com.samuelberrien.spectrix.MainActivity;

public class ObjVRActivity extends GvrActivity {

    private ObjGvrView gvrView;

    private boolean useSample;
    private String id_Visualisation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.useSample = Boolean.parseBoolean(getIntent().getStringExtra(MainActivity.USE_SAMPLE));
        this.id_Visualisation = getIntent().getStringExtra(MainActivity.ID_RENDERER);

        this.gvrView = new ObjGvrView(this, this.useSample, this.id_Visualisation);
        setContentView(gvrView);
    }

    @Override
    protected void onPause(){
        this.gvrView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.gvrView.onResume();
    }
}
