package com.samuelberrien.spectrix.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.normal.MyGLSurfaceView;
import com.samuelberrien.spectrix.threads.VisualizationThread;
import com.samuelberrien.spectrix.utils.core.Visualization;
import com.samuelberrien.spectrix.utils.core.VisualizationHelper;
import com.samuelberrien.spectrix.visualizations.spectrum.Spectrum;
import com.samuelberrien.spectrix.vr.MyGvrActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	public static final String IS_STREAM = "IS_STREAM";
	public static final String ID_RENDERER = "ID_RENDERER";

	private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0;

	private Toolbar toolbar;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private Button showToolBarButton;

	private MyGLSurfaceView myGLSurfaceView;

	private Menu menu;

	private MediaPlayer mPlayer;

	private int idVusalisation = 0;

	ArrayList<Button> buttonsDrawer;

	private int currentListeningId;

	//private DoubleTapGesture doubleTapGesture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		buttonsDrawer = new ArrayList<>();
		setContentView(R.layout.activity_main);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0) {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				if (slideOffset != 0) {
					showToolBarButton.setVisibility(View.GONE);
					getSupportActionBar().show();
				}
				super.onDrawerSlide(drawerView, slideOffset);
				//drawerLayout.bringChildToFront(drawerView);
				drawerLayout.requestLayout();
			}
		};
		drawerLayout.addDrawerListener(drawerToggle);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		showToolBarButton = new Button(this);
		showToolBarButton.setBackground(ContextCompat.getDrawable(this, R.drawable.show_button));
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		RelativeLayout.LayoutParams tmp = new RelativeLayout.LayoutParams(width / 15, width / 15);
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

		setUpDrawer();

		currentListeningId = VisualizationThread.STREAM_MUSIC;

		mPlayer = MediaPlayer.create(this, R.raw.crea_session_8);
		mPlayer.setLooping(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		this.menu = menu;
		this.menu.getItem(1).setVisible(false);
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
			case R.id.play_pause_toolbar:
				if (mPlayer.isPlaying()) {
					menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.play_icon));
					mPlayer.pause();
				} else {
					menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.pause_icon));
					mPlayer.start();
				}
				return true;
			case R.id.cardboard_toolbar:
				Intent intent = new Intent(this, MyGvrActivity.class);
				intent.putExtra(MainActivity.ID_RENDERER, idVusalisation);
				intent.putExtra(MainActivity.IS_STREAM, currentListeningId == VisualizationThread.STREAM_MUSIC);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpDrawer() {
		/*DoubleTapGesture doubleTapGesture = new DoubleTapGesture();
		final GestureDetector gestureDetector = new GestureDetector(this, doubleTapGesture);*/
		final LinearLayout linearLayoutSurfaceView = (LinearLayout) findViewById(R.id.layout_surface_view);
		Visualization startVisu = new Spectrum();
		myGLSurfaceView = new MyGLSurfaceView(this, startVisu, currentListeningId);
		/*myGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				gestureDetector.onTouchEvent(motionEvent);
				return view.onTouchEvent(motionEvent);
			}
		});*/
		getSupportActionBar().setTitle(startVisu.getName());
		linearLayoutSurfaceView.removeAllViews();
		linearLayoutSurfaceView.addView(myGLSurfaceView);

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_scroll_view_visualisations);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
		layoutParams.setMargins(margin, margin, margin, 0);
		layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

		for (int i = 0; i < VisualizationHelper.NB_VISUALIZATIONS; i++) {
			LinearLayout expandButton = (LinearLayout) getLayoutInflater().inflate(R.layout.expand_button, null);

			TextView levelName = (TextView) expandButton.findViewById(R.id.expand_text);
			final String name = VisualizationHelper.getVisualization(i).getName();
			levelName.setText(name);

			final int index = i;
			final Button start = (Button) expandButton.findViewById(R.id.expand_button);
			buttonsDrawer.add(start);
			start.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					idVusalisation = index;
					myGLSurfaceView.onPause();
					myGLSurfaceView = new MyGLSurfaceView(getApplicationContext(), VisualizationHelper.getVisualization(index), currentListeningId);
					linearLayoutSurfaceView.removeAllViews();
					linearLayoutSurfaceView.addView(myGLSurfaceView);
					/*myGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
						@Override
						public boolean onTouch(View view, MotionEvent motionEvent) {
							gestureDetector.onTouchEvent(motionEvent);
							return view.onTouchEvent(motionEvent);
						}
					});*/
					if (index == 0) {
						menu.getItem(1).setVisible(false);
					} else {
						menu.getItem(1).setVisible(true);
					}
					getSupportActionBar().setTitle(name);
					drawerLayout.closeDrawers();
				}
			});

			levelName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					for (Button b : buttonsDrawer)
						b.setVisibility(View.GONE);
					start.setVisibility(View.VISIBLE);
				}
			});
			expandButton.setLayoutParams(layoutParams);
			linearLayout.addView(expandButton);
		}
	}

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

	public void stream(View v) {
		currentListeningId = VisualizationThread.STREAM_MUSIC;
		myGLSurfaceView.setListening(VisualizationThread.STREAM_MUSIC);
	}

	public void mic(View view) {
		currentListeningId = VisualizationThread.MIC_MUSIC;
		myGLSurfaceView.setListening(VisualizationThread.MIC_MUSIC);
	}

	/*private class DoubleTapGesture extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if(showToolBarButton.getVisibility() == View.VISIBLE) {
				showToolBarButton.setVisibility(View.GONE);
				getSupportActionBar().show();
			}
			return true;
		}
	}*/
}
