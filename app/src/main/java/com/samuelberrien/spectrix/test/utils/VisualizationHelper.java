package com.samuelberrien.spectrix.test.utils;

import com.samuelberrien.spectrix.test.visualizations.explosion.Explosion;
import com.samuelberrien.spectrix.test.visualizations.icosahedron.Icosahedron;
import com.samuelberrien.spectrix.test.visualizations.spectrum.Spectrum;

/**
 * Created by Jean-François on 25/08/2017.
 */

public final class VisualizationHelper {

	public static int NB_VISUALIZATIONS = 3;

	public static Visualization getVisualization(int index) {
		switch (index) {
			case 0:
				return new Spectrum();
			case 1:
				return new Icosahedron();
			case 2:
				return new Explosion();
			default:
				return new Spectrum();
		}
	}
}
