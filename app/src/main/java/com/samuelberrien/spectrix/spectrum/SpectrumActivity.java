package com.samuelberrien.spectrix.spectrum;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.samuelberrien.spectrix.test.main.MainActivity;


/**
 * Created by samuel on 15/12/16.
 */

public class SpectrumActivity extends Activity {

	private SpectrumGLSurfaceView mGLView;

	private boolean useSample;

	private boolean isPortrait;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.useSample = Boolean.parseBoolean(getIntent().getStringExtra(MainActivity.USE_SAMPLE));

		this.isPortrait = Boolean.parseBoolean(getIntent().getStringExtra(MainActivity.SCREEN_PORTRAIT));

		if (this.isPortrait) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		this.mGLView = new SpectrumGLSurfaceView(this);//, this.useSample, this.isPortrait);

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			this.mGLView.updateVolume(keyCode);
			return super.onKeyDown(keyCode, event);
		} else
			return super.onKeyDown(keyCode, event);
	}

}
