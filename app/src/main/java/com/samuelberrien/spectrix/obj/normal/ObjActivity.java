package com.samuelberrien.spectrix.obj.normal;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.samuelberrien.spectrix.test.main.MainActivity;

public class ObjActivity extends AppCompatActivity {

	private ObjGLSurfaceView mGLView;

	private boolean useSample;
	private boolean isPortrait;
	private String id_Visualisation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.useSample = Boolean.parseBoolean(getIntent().getStringExtra(MainActivity.USE_SAMPLE));
		this.isPortrait = Boolean.parseBoolean(getIntent().getStringExtra(MainActivity.SCREEN_PORTRAIT));
		this.id_Visualisation = getIntent().getStringExtra(MainActivity.ID_RENDERER);

		if (this.isPortrait) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		this.mGLView = new ObjGLSurfaceView(this, this.useSample, this.id_Visualisation);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(this.mGLView);
	}

	@Override
	protected void onPause() {
		this.mGLView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.mGLView.onResume();
	}
}
