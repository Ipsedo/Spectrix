package com.samuelberrien.spectrix.test.vr;

import android.content.Context;

import com.google.vr.sdk.base.GvrView;
import com.samuelberrien.spectrix.test.utils.UpdateThread;
import com.samuelberrien.spectrix.test.utils.Visualization;

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
