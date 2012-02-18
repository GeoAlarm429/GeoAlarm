package edu.illinois.geoalarm;

import android.app.Activity;
import android.os.Bundle;

/**
 * This Activity is used to set various user options
 * for the GeoAlarm app
 * @author deflume1
 *
 */

public class Options extends Activity {	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.options_screen);
    }

}
