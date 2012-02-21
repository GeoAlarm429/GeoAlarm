package edu.illinois.geoalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;

public class TripPlanner extends Activity {

	Spinner serviceSelectSpinner;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                
        setContentView(R.layout.trip);
        
        serviceSelectSpinner = (Spinner) findViewById(R.id.tripServiceSelectSpinner);        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.travel_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSelectSpinner.setAdapter(adapter);
    }   
    
    @Override
    public void onStart()
    {   	
    	super.onStart();
    	serviceSelectSpinner.setOnItemSelectedListener(new OnItemSelectedListener() 
    	{    		
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
    	    {
    	    	int selectionPosition = serviceSelectSpinner.getSelectedItemPosition();
    	    	if(selectionPosition != Spinner.INVALID_POSITION)
    	    	{
    	    		if(selectionPosition == 1)
    	    		{
    	    			showTripPlannerBus(selectedItemView);
    	    		}
    	    		else if(selectionPosition == 2)
    	    		{
    	    			showTripPlannerTrain(selectedItemView);
    	    		}
    	    	}
    	    }

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
