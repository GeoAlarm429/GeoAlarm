package edu.illinois.geoalarm;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

public class RouteMap extends MapActivity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.map_screen);
    }
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
