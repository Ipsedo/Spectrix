package com.samuelberrien.spectrix.test.vr;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;

import com.google.vr.sdk.base.GvrView;
import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.obj.vr.ObjStereoRenderer;
import com.samuelberrien.spectrix.obj.vr.renderers.ObjStereoRendererCanyon;
import com.samuelberrien.spectrix.obj.vr.renderers.ObjStereoRendererExplosion;
import com.samuelberrien.spectrix.obj.vr.renderers.ObjStereoRendererIcosahedron;
import com.samuelberrien.spectrix.obj.vr.renderers.ObjStereoRendererRoom;
import com.samuelberrien.spectrix.obj.vr.renderers.ObjStereoRendererSnow;
import com.samuelberrien.spectrix.test.UpdateThread;
import com.samuelberrien.spectrix.test.Visualization;

/**
 * Created by samuel on 11/01/17.
 */

public class MyGvrView extends GvrView {

	private Visualization visualization;
	private UpdateThread updateThread;

	public MyGvrView(Context context, Visualization visualization) {
		super(context);
		setEGLContextClientVersion(2);
		setTransitionViewEnabled(true);

		this.visualization = visualization;

		setRenderer(new GLStereoRenderer(context, visualization));

		updateThread = new UpdateThread(this.visualization);
		updateThread.start();
	}

	@Override
	public void onPause() {
		updateThread.cancel();
		try {
			updateThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (updateThread != null) {
			updateThread.cancel();
			try {
				updateThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			updateThread = new UpdateThread(this.visualization);
			updateThread.start();
		}
	}
}
