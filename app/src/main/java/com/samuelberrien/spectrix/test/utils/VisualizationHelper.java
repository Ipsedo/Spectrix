package com.samuelberrien.spectrix.test.utils;

import com.samuelberrien.spectrix.test.visualizations.snow.Snow;
import com.samuelberrien.spectrix.test.visualizations.explosion.Explosion;
import com.samuelberrien.spectrix.test.visualizations.icosahedron.Icosahedron;
import com.samuelberrien.spectrix.test.visualizations.spectrum.Spectrum;

/**
 * Created by Jean-François on 25/08/2017.
 */

public final class VisualizationHelper {

	public static int NB_VISUALIZATIONS = 4;

	public static Visualization getVisualization(int index) {
		switch (index) {
			case 0:
				return new Spectrum();
			case 1:
				return new Icosahedron();
			case 2:
				return new Explosion();
			case 3:
				return new Snow();
			default:
				return new Spectrum();
		}
	}
}
