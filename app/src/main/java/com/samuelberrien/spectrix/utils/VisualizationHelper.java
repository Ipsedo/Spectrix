package com.samuelberrien.spectrix.utils;

import com.samuelberrien.spectrix.visualizations.Explosion;
import com.samuelberrien.spectrix.visualizations.Icosahedron;
import com.samuelberrien.spectrix.visualizations.Room;
import com.samuelberrien.spectrix.visualizations.Snow;
import com.samuelberrien.spectrix.visualizations.Spectrum;

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
