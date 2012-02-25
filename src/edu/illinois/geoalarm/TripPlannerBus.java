package edu.illinois.geoalarm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * The TripPlannerBus activity handles planning a bus trip.
 * @author deflume1
 *
 */

public class TripPlannerBus extends Activity 
{
	Spinner serviceSelectSpinner;
	Spinner startingLocationSpinner;
	Spinner destinationSpinner;
	GeoAlarmDB database;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_cta_bus);
        
        loadDatabase();
        populateServiceSpinner();
        populateStartingAndDestination();        
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
    	setServiceSelectListeners();    	
    }
	
	/**
	 * This function tries to load the existing SQLite DB
	 */
	public void loadDatabase()
	{
		database = new GeoAlarmDB(this.getApplicationContext());
		
		// Check the custom SQLite helper functions that load existing DB
		try
		{
			database.createDataBase();
		}
		catch (IOException e)
		{
			throw new Error("Unable to create/find database");
		}

		// Open the SQLite database
		try
		{
			database.openDataBase();
		}
		catch (SQLException sql)
		{
			throw new Error("Unable to execute sql in: " + sql.toString());
		}	
	}
	
	/**
	 * This method populates the serviceSpinner, and sets the choice to Bus, since that had to be chosen
	 * to start this Activity
	 */
	public void populateServiceSpinner()
	{	
		serviceSelectSpinner = (Spinner) findViewById(R.id.serviceSelectSpinner);        
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
			this, R.array.travel_type_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		serviceSelectSpinner.setAdapter(adapter);
		serviceSelectSpinner.setSelection(1);
	}
	
	/**
	 * This method populates the startingLocationSpinner and destinationSpinner with data from the database
	 */
	public void populateStartingAndDestination()
	{
		startingLocationSpinner = (Spinner) findViewById(R.id.startingLocationSpinner);  
		destinationSpinner = (Spinner) findViewById(R.id.destinationSpinner);
		List<String> locationList = new ArrayList<String>();
		
		/* Insert DB call to get all locations to populate Spinner */
		
		Cursor theCursor = database.geoAlarmDB.query("Routes", null, null, null, null, null, null);	
		
		if(theCursor != null)
		{
			theCursor.moveToFirst();
							
			Collections.addAll(locationList, theCursor.getColumnNames());
			
			
			for(int i = 0; theCursor.isAfterLast() != false; i++)
			{
				int nameColumn = theCursor.getColumnIndex("name");
				locationList.add(theCursor.getString(nameColumn));
				
				theCursor.moveToNext();
			}
			
		}

			theCursor.close();
			database.geoAlarmDB.close();    
													   
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_spinner_item, locationList);		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		startingLocationSpinner.setAdapter(adapter);				
	}
	
	/**
	 * This method sets up the event listeners for the serviceSelect Spinner item
	 */
	private void setServiceSelectListeners()
	{
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
    	    			// Do nothing, already in the bus view
    	    		}
    	    		else if(selectionPosition == 2) // "Train"
    	    		{
    	    			finish();
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
	
	/**
	 * This method is used to launch the alarm options dialog.  The method is bound to the button using
	 * the onClick XML attribute.
	 */
	public void configureAlarmOptions()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this.getBaseContext()).create();
				
	}
	
	/**
	 * This method is used to launch the trip setting intent.  The method is bound to the button using the
	 * onClick XML attribute.
	 */
	public void setAlarm()
	{		
		
	}
	
	
	
	
	
}
