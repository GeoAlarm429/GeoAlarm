package edu.illinois.geoalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * The main Activity for the GeoAlarm app
 * When the app is launched, this Activity sets the content view
 * to main, and initializes the primary UI elements.
 * @author deflume1
 *
 */

public class GeoAlarm extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
    }

    /* This method is called when the Map button is clicked.
     * It launches the RouteMap activity.
     */
	public void showMapScreen(View view)
	{
		Intent intent = new Intent(view.getContext(), RouteMap.class);
		startActivityForResult(intent, 0);		
	}
	
	/* This method is called when the Trip button is clicked.
	 * It launches the TripPlanner activity.
	 */
	public void showTripScreen(View view)
	{
		Intent intent = new Intent(view.getContext(), TripPlanner.class);
		startActivityForResult(intent, 0);
	}
	
	/* This method is called when the Options button is clicked.
	 * It launched the Options activity.
	 */
	public void showOptionsScreen(View view)
	{
		Intent intent = new Intent(view.getContext(), Options.class);
		startActivityForResult(intent, 0);
	}

}