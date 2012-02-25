package edu.illinois.geoalarm;

import android.app.Activity;
import android.os.Bundle;

/**
 * The TripPlannerBus activity handles planning a bus trip.
 * @author deflume1
 *
 */

public class TripPlannerBus extends Activity 
{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_cta_bus);
    }
}
