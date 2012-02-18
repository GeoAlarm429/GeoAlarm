package edu.illinois.geoalarm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class TripPlanner extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                
        setContentView(R.layout.trip_screen);        
        
        Spinner modeOfTravelSpinner = (Spinner) findViewById(R.id.modeOfTravelSpinner);
        ArrayAdapter<CharSequence> modeOfTravelAdapter = ArrayAdapter.createFromResource
        		(this, R.array.mode_of_travel, android.R.layout.simple_spinner_item);
        modeOfTravelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeOfTravelSpinner.setAdapter(modeOfTravelAdapter);
    }

}
