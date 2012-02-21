package edu.illinois.geoalarm;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

/**
 * A MapActivity class that will be responsible for displaying the transit map.
 * @author deflume1
 *
 */

public class RouteMap extends MapActivity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.map);
    }
	
    /**
     * This method returns whether routes are currently being displayed on the map.
     * Right now, they're not.
     */
	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}

}
