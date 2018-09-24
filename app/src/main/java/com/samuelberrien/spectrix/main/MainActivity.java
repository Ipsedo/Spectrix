package com.samuelberrien.spectrix.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.normal.MyGLSurfaceView;
import com.samuelberrien.spectrix.threads.VisualizationThread;
import com.samuelberrien.spectrix.utils.core.Visualization;
import com.samuelberrien.spectrix.utils.core.VisualizationHelper;
import com.samuelberrien.spectrix.utils.ui.expand.ExpandButton;
import com.samuelberrien.spectrix.utils.ui.expand.RadioExpand;
import com.samuelberrien.spectrix.utils.ui.main.ExpandCollapseView;
import com.samuelberrien.spectrix.utils.ui.main.ShowToolBarButton;
import com.samuelberrien.spectrix.utils.ui.main.SpectrixToolBar;
import com.samuelberrien.spectrix.visualizations.spectrum.Spectrum;
import com.samuelberrien.spectrix.vr.MyGvrActivity;

public class MainActivity extends AppCompatActivity
		implements MyGLSurfaceView.OnVisualizationInitFinish {

	public static final String IS_STREAM = "IS_STREAM";
	public static final String ID_RENDERER = "ID_RENDERER";

	private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0;

	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private ShowToolBarButton showToolBarButton;

	private MyGLSurfaceView myGLSurfaceView;

	private SubMenu subMenu;

	private MediaPlayer mPlayer;

	private int idVisualisation = 0;

	private int currentListeningId;

	private RelativeLayout mainRelativeLayout;
	private ProgressBar progressBar;

	private GestureDetector toolBarGestureDetector;
	private GestureDetector showToolBarGestureDetector;

	private SpectrixToolBar toolbar;

	private ExpandCollapseView expandCollapseView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ExpandCollapseView.AnimationListener animationListener =
				new ExpandCollapseView.AnimationListener() {
					@Override
					public void onCollapseEnd(View v) {
						showToolBarButton.setVisibility(View.VISIBLE);
					}

					@Override
					public void onExpandEnd(View v) {

					}

					@Override
					public void onCollapseStart(View v) {

					}

					@Override
					public void onExpandStart(View v) {
						showToolBarButton.setVisibility(View.GONE);
					}
				};
		expandCollapseView = new ExpandCollapseView(toolbar, 200,
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
				animationListener);

		drawerLayout = findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
		drawerLayout.addDrawerListener(drawerToggle);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mainRelativeLayout = findViewById(R.id.layout_surface_view);

		showToolBarButton = findViewById(R.id.show_toolbar_button);

		progressBar = findViewById(R.id.progress_bar_visu);

		currentListeningId = VisualizationThread.NONE;
		requestRecordPermission();

		mPlayer = MediaPlayer.create(this, R.raw.crea_session_8);
		mPlayer.setLooping(false);
		mPlayer.setOnCompletionListener((MediaPlayer mediaPlayer) ->
				subMenu.getItem(2).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.play_icon))
		);

		switchOrientation(getResources().getConfiguration().orientation);

		toolBarGestureDetector = new GestureDetector(this, new ToolBarGestureListener());
		showToolBarGestureDetector = new GestureDetector(this, new ButtonGestureListener());

		toolbar.setOnTouchListener((View view, MotionEvent motionEvent) -> {
				view.performClick();
				return toolBarGestureDetector.onTouchEvent(motionEvent);
		});
		showToolBarButton.setOnTouchListener((View view, MotionEvent motionEvent) -> {
				view.performClick();
				return showToolBarGestureDetector.onTouchEvent(motionEvent);
		});
	}

	private void hideToolBar() {
		expandCollapseView.collapse();
	}

	private void showToolBar() {
		expandCollapseView.expand();
	}

	private void requestRecordPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
		} else {
			setUpDrawer();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					setUpDrawer();
				} else {
					if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
						showSorryDialog();
					} else {
						showEndDialog();
					}
				}
				break;
			}
		}
	}

	private void showEndDialog() {
		AlertDialog endDialog = new AlertDialog.Builder(this, R.style.SpectrixDialogTheme)
				.setMessage("Spectrix can't work without audio record, Sorry...")
				.setPositiveButton("Exit", (DialogInterface dialogInterface, int i) -> finish())
				.create();
		endDialog.setCancelable(false);
		endDialog.setCanceledOnTouchOutside(false);
		endDialog.show();
	}

	private void showSorryDialog() {
		AlertDialog sorryDialog = new AlertDialog.Builder(this, R.style.SpectrixDialogTheme)
				.setMessage("Spectrix needs to record audio,\n" +
						"Please accept the permission !")
				.setNegativeButton("No, Exit", (DialogInterface dialogInterface, int i) -> finish())
				.setPositiveButton("Yes", (DialogInterface dialogInterface, int i) -> requestRecordPermission())
				.create();
		sorryDialog.setCancelable(false);
		sorryDialog.setCanceledOnTouchOutside(false);
		sorryDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		subMenu = menu.getItem(0).getSubMenu();
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
		switchOrientation(newConfig.orientation);
	}

	private void switchOrientation(int orientation) {
		LinearLayout menuDrawer = findViewById(R.id.layout_menu_drawer);
		LinearLayout layoutToggle = findViewById(R.id.layout_toggle);
		LinearLayout layoutScroll = findViewById(R.id.layout_scroll);

		LinearLayout.LayoutParams layoutPortraitParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams layoutLandParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);

		if (subMenu != null)
			subMenu.close();

		switch (orientation) {
			case Configuration.ORIENTATION_LANDSCAPE:
				menuDrawer.setOrientation(LinearLayout.HORIZONTAL);
				layoutScroll.setLayoutParams(layoutLandParams);
				layoutToggle.setLayoutParams(layoutLandParams);
				findViewById(R.id.about_spectrix_text_view).setVisibility(View.VISIBLE);
				break;
			case Configuration.ORIENTATION_PORTRAIT:
				menuDrawer.setOrientation(LinearLayout.VERTICAL);
				layoutToggle.setLayoutParams(layoutPortraitParams);
				layoutScroll.setLayoutParams(layoutPortraitParams);
				findViewById(R.id.about_spectrix_text_view).setVisibility(View.GONE);
				break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				drawerLayout.openDrawer(GravityCompat.START);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void vr(MenuItem menuItem) {
		Intent intent = new Intent(this, MyGvrActivity.class);
		intent.putExtra(MainActivity.ID_RENDERER, idVisualisation);
		intent.putExtra(MainActivity.IS_STREAM, currentListeningId == VisualizationThread.STREAM_MUSIC);
		startActivity(intent);
	}

	public void playSample(MenuItem menuItem) {
		if (mPlayer.isPlaying()) {
			menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.play_icon));
			mPlayer.pause();
		} else {
			menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.pause_icon));
			mPlayer.start();
		}
	}

	public void hideToolbar(MenuItem menuItem) {
		hideToolBar();
	}

	public void share(MenuItem menuItem) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_SUBJECT, "Spectrix");
		String sAux = "https://play.google.com/store/apps/details?id=com.samuelberrien.spectrix";
		i.putExtra(Intent.EXTRA_TEXT, sAux);
		startActivity(Intent.createChooser(i, "Choose one"));
	}

	public void aboutSpectrix(MenuItem menuItem) {
		final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.SpectrixDialogTheme)
				.setMessage(R.string.about_spectrix)
				.setPositiveButton("Ok", (DialogInterface dialog, int which) -> {})
				.create();
		alertDialog.show();
	}

	private void setUpDrawer() {
		Visualization startVisualization = new Spectrum();

		myGLSurfaceView = new MyGLSurfaceView(this, startVisualization, currentListeningId, this);

		findViewById(R.id.stream_radio_button).performClick();

		toolbar.setTitle(startVisualization.getName());

		final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		mainRelativeLayout.addView(myGLSurfaceView, 0, layoutParams);

		RadioExpand radioExpand = findViewById(R.id.radio_expand_scroll_view_visualisations);

		for (int i = 0; i < VisualizationHelper.NB_VISUALIZATIONS; i++) {
			final int index = i;
			final String name = VisualizationHelper.getVisualization(i).getName();
			Runnable onConfirm = () -> {
					idVisualisation = index;
					myGLSurfaceView.onPause();

					mainRelativeLayout.removeView(myGLSurfaceView);

					progressBar.setVisibility(View.VISIBLE);

					Visualization visualization = VisualizationHelper.getVisualization(index);

					myGLSurfaceView = new MyGLSurfaceView(
							getApplicationContext(), visualization,
							currentListeningId, MainActivity.this);
					mainRelativeLayout.addView(myGLSurfaceView, 0, layoutParams);

					toolbar.setTitle(name);

					drawerLayout.closeDrawers();

			};
			ExpandButton expandButton = new ExpandButton(this, onConfirm);
			expandButton.setText(name);
			radioExpand.addExpandButton(expandButton);
		}
	}

	@Override
	protected void onPause() {
		if (myGLSurfaceView != null)
			myGLSurfaceView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (myGLSurfaceView != null)
			myGLSurfaceView.onResume();
	}

	@Override
	protected void onDestroy() {
		mPlayer.stop();
		super.onDestroy();
	}

	public void stream(View v) {
		currentListeningId = VisualizationThread.STREAM_MUSIC;
		myGLSurfaceView.setListening(VisualizationThread.STREAM_MUSIC);
	}

	public void mic(View view) {
		currentListeningId = VisualizationThread.MIC_MUSIC;
		myGLSurfaceView.setListening(VisualizationThread.MIC_MUSIC);
	}

	@Override
	public void onFinish() {
		runOnUiThread(() -> progressBar.setVisibility(View.GONE));
	}

	private class ToolBarGestureListener extends GestureDetector.SimpleOnGestureListener {

		private int LimitSwipeSpeed;

		ToolBarGestureListener() {
			LimitSwipeSpeed = 10;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			float yDelta = e2.getY() - e1.getY();
			if (yDelta < 0 && Math.abs(velocityY) > LimitSwipeSpeed) {
				hideToolBar();
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			hideToolBar();
			return true;
		}
	}

	private class ButtonGestureListener extends GestureDetector.SimpleOnGestureListener {


		private int LimitSwipeSpeed;

		ButtonGestureListener() {
			LimitSwipeSpeed = 10;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			float yDelta = e2.getY() - e1.getY();
			if (yDelta > 0 && Math.abs(velocityY) > LimitSwipeSpeed) {
				showToolBar();
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			showToolBar();
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			showToolBar();
			return true;
		}
	}
}
