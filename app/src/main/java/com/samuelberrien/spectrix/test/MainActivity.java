package com.samuelberrien.spectrix.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.test.visualizations.icosahedron.Icosahedron;
import com.samuelberrien.spectrix.test.visualizations.spectrum.Spectrum;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	public static final String USE_SAMPLE = "USE_SAMPLE";
	public static final String SCREEN_PORTRAIT = "SCREEN_PORTRAIT";
	public static final String ID_RENDERER = "ID_RENDERER";

	private boolean useSample;
	private boolean useVR;

	private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0;

	private ArrayAdapter<CharSequence> adapter;
	private String idVusalisation;

	private Toolbar toolbar;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private Button showToolBarButton;

	private MyGLSurfaceView myGLSurfaceView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		this.setUpDrawer();
		this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		this.drawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, 0, 0);
		this.drawerLayout.addDrawerListener(this.drawerToggle);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		showToolBarButton = new Button(this);
		showToolBarButton.setText("⬇");
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		RelativeLayout.LayoutParams tmp = new RelativeLayout.LayoutParams(width / 10, width / 10);
		showToolBarButton.setVisibility(View.GONE);
		showToolBarButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showToolBarButton.setVisibility(View.GONE);
				getSupportActionBar().show();
			}
		});
		showToolBarButton.setLayoutParams(tmp);

		RelativeLayout relativeLayout = new RelativeLayout(this);
		relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		relativeLayout.addView(showToolBarButton);
		relativeLayout.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

		/**FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/ace_futurism.ttf");**/
		addContentView(relativeLayout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
			} else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				drawerLayout.openDrawer(GravityCompat.START);
				return true;
			case R.id.hide_toolbar:
				/*toolbar.animate()
						.alpha(0) //la rendre invisible
						.translationY(-toolbar.getHeight())
						.start();*/
				getSupportActionBar().hide();
				showToolBarButton.setVisibility(View.VISIBLE);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpDrawer() {
		final LinearLayout linearLayoutSurfaceView = (LinearLayout) findViewById(R.id.layout_surface_view);

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_scroll_view_visualisations);

		Button button = new Button(this);
		button.setText("Spectrum");
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				myGLSurfaceView = (MyGLSurfaceView) getLayoutInflater().inflate(R.layout.gl_surface_view_layout, null);
				myGLSurfaceView.setVisualization(new Spectrum());
				linearLayoutSurfaceView.removeAllViews();
				linearLayoutSurfaceView.addView(myGLSurfaceView);
				drawerLayout.closeDrawers();
			}
		});
		linearLayout.addView(button);

		button = new Button(this);
		button.setText("Icosahedrons");
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				myGLSurfaceView = (MyGLSurfaceView) getLayoutInflater().inflate(R.layout.gl_surface_view_layout, null);
				myGLSurfaceView.setVisualization(new Icosahedron());
				linearLayoutSurfaceView.removeAllViews();
				linearLayoutSurfaceView.addView(myGLSurfaceView);
				drawerLayout.closeDrawers();
			}
		});
		linearLayout.addView(button);

		myGLSurfaceView = (MyGLSurfaceView) getLayoutInflater().inflate(R.layout.gl_surface_view_layout, null);
		myGLSurfaceView.setVisualization(new Spectrum());
		linearLayoutSurfaceView.removeAllViews();
		linearLayoutSurfaceView.addView(myGLSurfaceView);
	}

    /*private boolean getOrientationPortrait(String idSpectrumAnalyser) {
		return idSpectrumAnalyser.equals(this.adapter.getItem(0));
    }

    public void start(View v) {
        if (this.idVusalisation.equals(this.adapter.getItem(0)) || this.idVusalisation.equals(this.adapter.getItem(1))) {
            Intent intent = new Intent(this, SpectrumActivity.class);
            intent.putExtra(MainActivity.USE_SAMPLE, Boolean.toString(this.useSample));
            intent.putExtra(MainActivity.SCREEN_PORTRAIT, Boolean.toString(this.getOrientationPortrait(this.idVusalisation)));
            startActivity(intent);
        } else if (this.useVR) {
            Intent intent = new Intent(this, ObjVRActivity.class);
            intent.putExtra(MainActivity.USE_SAMPLE, Boolean.toString(this.useSample));
            intent.putExtra(MainActivity.ID_RENDERER, this.idVusalisation);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ObjActivity.class);
            intent.putExtra(MainActivity.USE_SAMPLE, Boolean.toString(this.useSample));
            intent.putExtra(MainActivity.SCREEN_PORTRAIT, Boolean.toString(false));
            intent.putExtra(MainActivity.ID_RENDERER, this.idVusalisation);
            startActivity(intent);
        }
    }

    public void useSample(View v) {
        this.useSample = ((CheckBox) findViewById(R.id.use_sample_check_box)).isChecked();
    }

    public void useVr(View v) {
        this.useVR = ((CheckBox) findViewById(R.id.use_vr_check_box)).isChecked();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        this.idVusalisation = (String) parent.getItemAtPosition(pos);
        if (pos > 1) {
            CheckBox useVRCheckBox = (CheckBox) findViewById(R.id.use_vr_check_box);
            useVRCheckBox.setEnabled(true);
            this.useVR = ((CheckBox) findViewById(R.id.use_vr_check_box)).isChecked();
        } else {
            CheckBox useVRCheckBox = (CheckBox) findViewById(R.id.use_vr_check_box);
            useVRCheckBox.setEnabled(false);
            useVRCheckBox.setChecked(false);
            this.useVR = ((CheckBox) findViewById(R.id.use_vr_check_box)).isChecked();
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }*/

	@Override
	protected void onPause() {
		myGLSurfaceView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		myGLSurfaceView.onResume();
	}
}
