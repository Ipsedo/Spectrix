package com.samuelberrien.spectrix.utils.core;

import com.samuelberrien.spectrix.visualizations.explosion.Explosion;
import com.samuelberrien.spectrix.visualizations.icosahedron.Icosahedron;
import com.samuelberrien.spectrix.visualizations.room.Room;
import com.samuelberrien.spectrix.visualizations.snow.Snow;
import com.samuelberrien.spectrix.visualizations.spectrum.Spectrum;

/**
 * Created by Jean-François on 25/08/2017.
 */

public final class VisualizationHelper {

	public static int NB_VISUALIZATIONS = 5;

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
			case 4:
				return new Room();
			default:
				return new Spectrum();
		}
	}
}
