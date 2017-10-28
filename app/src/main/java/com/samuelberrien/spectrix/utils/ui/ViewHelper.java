package com.samuelberrien.spectrix.utils.ui;

import android.app.Activity;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;

/**
 * Created by Samuel on 04/10/2017.
 */

public final class ViewHelper {

	public static void makeViewTransition(final Activity activity, final View view) {
		final TransitionDrawable transition = (TransitionDrawable) view.getBackground();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				transition.startTransition(200);
			}
		});
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				transition.reverseTransition(200);
			}
		});
	}
}
