package edu.illinois.geoalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;

/** The TripPlanner class is responsible for displaying the Spinner
 *  that determines the type of transit.  It then transitions to the
 *  appropriate transit activity
 *  @author deflume1
 *
 */

public class TripPlanner extends Activity {

	Spinner serviceSelectSpinner;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                
        setContentView(R.layout.trip);
        
        /* Here, we populate the Spinner with the "Bus" and "Train" choices */
        serviceSelectSpinner = (Spinner) findViewById(R.id.tripServiceSelectSpinner);        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.travel_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSelectSpinner.setAdapter(adapter);
    }   
    
    /**
     * This method is called when the activity is going to become visible to the user.
     * We setup the selection event listener for the Spinner here.
     */
    @Override
    public void onStart()
    {   	
    	/* Call superclass constructor.  Required */
    	super.onStart();
    	
    	/* Set a new event listener for the Spinner item selection */
    	serviceSelectSpinner.setOnItemSelectedListener(new OnItemSelectedListener() 
    	{    
    		/* Implement the onItemSelected method to handle item selections */
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
    	    {
    	    	int selectionPosition = serviceSelectSpinner.getSelectedItemPosition();
    	    	if(selectionPosition != Spinner.INVALID_POSITION)
    	    	{
    	    		if(selectionPosition == 1) // "Bus"
    	    		{
    	    			showTripPlannerBus(selectedItemView);
    	    		}
    	    		else if(selectionPosition == 2) // "Train"
    	    		{
    	    			showTripPlannerTrain(selectedItemView);
    	    		}
    	    	}
    	    }

    	    /* We do nothing here.  May want to change behavior so the last selected item behavior is used */
    	    public void onNothingSelected(AdapterView<?> parentView) 
    	    {
    	        // do nothing
    	    }

    	});
    }

    
    public void showTripPlannerBus(View view)
    {    	
    	Intent intent = new Intent(view.getContext(), TripPlannerBus.class);    	
		startActivityForResult(intent, 0);		
    }
    
    public void showTripPlannerTrain(View view)
    {
    	Intent intent = new Intent(view.getContext(), TripPlannerTrain.class);    	
		startActivityForResult(intent, 0);		
    }

}
