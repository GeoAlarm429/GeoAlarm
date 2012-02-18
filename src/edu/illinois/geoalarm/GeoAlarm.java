package edu.illinois.geoalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GeoAlarm extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.first_screen);
    }

	public void showMapScreen(View view)
	{
		Intent intent = new Intent(view.getContext(), RouteMap.class);
		startActivityForResult(intent, 0);		
	}
	
	public void showTripScreen(View view)
	{
		Intent intent = new Intent(view.getContext(), TripPlanner.class);
		startActivityForResult(intent, 0);
	}
	
	public void showOptionsScreen(View view)
	{
		Intent intent = new Intent(view.getContext(), Options.class);
		startActivityForResult(intent, 0);
	}

}