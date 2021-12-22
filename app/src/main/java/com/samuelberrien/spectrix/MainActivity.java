package com.samuelberrien.spectrix;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.samuelberrien.spectrix.normal.MyGLSurfaceView;
import com.samuelberrien.spectrix.threads.VisualizationThread;
import com.samuelberrien.spectrix.ui.expand.ExpandButton;
import com.samuelberrien.spectrix.ui.expand.RadioExpand;
import com.samuelberrien.spectrix.ui.main.ExpandCollapseView;
import com.samuelberrien.spectrix.ui.main.ShowToolBarButton;
import com.samuelberrien.spectrix.ui.main.SpectrixToolBar;
import com.samuelberrien.spectrix.utils.Visualization;
import com.samuelberrien.spectrix.utils.VisualizationHelper;
import com.samuelberrien.spectrix.visualizations.Spectrum;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpDrawer();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    showSorryDialog();
                } else {
                    showEndDialog();
                }
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
        boolean res = super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        subMenu = menu.getItem(0).getSubMenu();
        return res;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
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
        if (item.getItemId() == android.R.id.home) {
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
        TextView message = new TextView(this);

        final SpannableString s =
                new SpannableString(getText(R.string.about_spectrix));
        Linkify.addLinks(s, Linkify.WEB_URLS);

        message.setText(s);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        float dip = 20f;
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        message.setPadding(px, px, px, px);
        message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);

        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.SpectrixDialogTheme)
                .setView(message)
                .setPositiveButton("Ok", (DialogInterface dialog, int which) -> {
                })
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

        private final int limitSwipeSpeed;

        ToolBarGestureListener() {
            limitSwipeSpeed = 10;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float yDelta = e2.getY() - e1.getY();
            if (yDelta < 0 && Math.abs(velocityY) > limitSwipeSpeed) {
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

        private final int limitSwipeSpeed;

        ButtonGestureListener() {
            limitSwipeSpeed = 10;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float yDelta = e2.getY() - e1.getY();
            if (yDelta > 0 && Math.abs(velocityY) > limitSwipeSpeed) {
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
