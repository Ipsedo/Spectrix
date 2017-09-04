package com.samuelberrien.spectrix.vr;

import android.os.Bundle;

import com.google.vr.sdk.base.GvrActivity;
import com.samuelberrien.spectrix.main.MainActivity;
import com.samuelberrien.spectrix.utils.core.Visualization;
import com.samuelberrien.spectrix.utils.core.VisualizationHelper;

public class MyGvrActivity extends GvrActivity {

	private MyGvrView gvrView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Visualization visualization = VisualizationHelper.getVisualization(getIntent().getIntExtra(MainActivity.ID_RENDERER, 0));

		/*if (!visualization.is3D()) {
			throw new IllegalArgumentException("Unsuported Visualization : it is not a 3D visualization !");
		}*/

		this.gvrView = new MyGvrView(this,
				visualization,
				getIntent().getBooleanExtra(MainActivity.IS_STREAM, true));
		setContentView(gvrView);
	}

	@Override
	protected void onPause() {
		this.gvrView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.gvrView.onResume();
	}
}
