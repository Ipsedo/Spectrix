package com.samuelberrien.spectrix.test.utils.threads;

import com.samuelberrien.spectrix.test.utils.Visualization;

/**
 * Created by samuel on 30/08/17.
 */

public class MicThread extends CancelableThread {

	public MicThread(Visualization visualization) {
		super("MicThread", visualization);
	}

	@Override
	protected void work(Visualization visualization) {
		visualization.update(new float[1024]);
	}

	@Override
	protected void onEnd() {

	}
}
