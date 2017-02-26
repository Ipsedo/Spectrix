package com.samuelberrien.spectrix;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.samuelberrien.spectrix.obj.normal.ObjActivity;
import com.samuelberrien.spectrix.obj.vr.ObjVRActivity;
import com.samuelberrien.spectrix.spectrum.SpectrumActivity;
import com.samuelberrien.spectrix.utils.FontsOverride;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private boolean useSample;
    private boolean useVR;

    private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0;

    private ArrayAdapter<CharSequence> adapter;
    private String idVusalisation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/ace_futurism.ttf");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        this.useSample = ((CheckBox) findViewById(R.id.use_sample_check_box)).isChecked();
        CheckBox useVRCheckBox = (CheckBox) findViewById(R.id.use_vr_check_box);
        useVRCheckBox.setEnabled(false);
        this.useVR = useVRCheckBox.isChecked();

        Spinner spinner = (Spinner) findViewById(R.id.select_visualisation);
        this.adapter = ArrayAdapter.createFromResource(this, R.array.visualisation_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        this.idVusalisation = (String) this.adapter.getItem(0);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            }
        }
    }

    private boolean getOrientationPortrait(String idSpectrumAnalyser){
        return idSpectrumAnalyser.equals(this.adapter.getItem(0));
    }

    public void start(View v){
        if(this.idVusalisation.equals(this.adapter.getItem(0)) || this.idVusalisation.equals(this.adapter.getItem(1))) {
            Intent intent = new Intent(this, SpectrumActivity.class);
            intent.putExtra("USE_SAMPLE", Boolean.toString(this.useSample));
            intent.putExtra("SCREEN_PORTRAIT", Boolean.toString(this.getOrientationPortrait(this.idVusalisation)));
            startActivity(intent);
        }else if(this.useVR){
            Intent intent = new Intent(this, ObjVRActivity.class);
            intent.putExtra("USE_SAMPLE", Boolean.toString(this.useSample));
            intent.putExtra("ID_RENDERER", this.idVusalisation);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, ObjActivity.class);
            intent.putExtra("USE_SAMPLE", Boolean.toString(this.useSample));
            intent.putExtra("SCREEN_PORTRAIT", Boolean.toString(false));
            intent.putExtra("ID_RENDERER", this.idVusalisation);
            startActivity(intent);
        }
    }

    public void sample(View v){
        this.useSample = ((CheckBox) findViewById(R.id.use_sample_check_box)).isChecked();
    }

    public void useVr(View v){
        this.useVR = ((CheckBox) findViewById(R.id.use_vr_check_box)).isChecked();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        this.idVusalisation = (String) parent.getItemAtPosition(pos);
        if(pos > 1){
            CheckBox useVRCheckBox = (CheckBox) findViewById(R.id.use_vr_check_box);
            useVRCheckBox.setEnabled(true);
            this.useVR = ((CheckBox) findViewById(R.id.use_vr_check_box)).isChecked();
        }else{
            CheckBox useVRCheckBox = (CheckBox) findViewById(R.id.use_vr_check_box);
            useVRCheckBox.setEnabled(false);
            useVRCheckBox.setChecked(false);
            this.useVR = ((CheckBox) findViewById(R.id.use_vr_check_box)).isChecked();
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.useSample = ((CheckBox) findViewById(R.id.use_sample_check_box)).isChecked();
        this.useVR = ((CheckBox) findViewById(R.id.use_vr_check_box)).isChecked();
    }
}
