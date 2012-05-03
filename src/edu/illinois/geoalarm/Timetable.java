package edu.illinois.geoalarm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public class Timetable extends Activity
{
	private Spinner timetableLineSpinner;
	private Spinner timetableStopSpinner;
	private LinearLayout timetableLinearLayout;
	private GeoAlarmDB database;
	private String selectedLine;
	private String selectedStop;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable);
        
        timetableLineSpinner = (Spinner)findViewById(R.id.timetableLineSpinner);
    	timetableStopSpinner = (Spinner)findViewById(R.id.timetableStopSpinner); 
    	timetableLinearLayout = (LinearLayout)findViewById(R.id.timetableLinearLayout);
    	
    	setLineSpinnerEventListeners();
    	setStopSpinnerEventListeners(); 	
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	
    }
    
    @Override
    public void onResume()
    {
    	loadDatabase();
    	populateLineSpinner();
    	super.onResume();
    }
    
    @Override
    public void onPause()
    {
    	database.close();
    	database = null;
    	super.onPause();
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
	 * This method populates the timetableLineSpinner with data from the database
	 */
    public void populateLineSpinner()
    {	
		ArrayList<String> linesList = database.getBusLines();
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_spinner_item, linesList);		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		timetableLineSpinner.setAdapter(adapter);	
    }
    
    /**
	 * This method populates the timetableStopSpinner with data from the database
	 */
	public void populateStopSpinner()
	{		
		List<String> locationList = database.getLineStops(selectedLine);														   
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_spinner_item, locationList);		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		timetableStopSpinner.setAdapter(adapter);		
	}	
	
	/**
	 * This method sets the click event listener for the lineSpinner selection action.
	 * When a line is selected, we retrieve the selected line, then we populate the stop
	 * Spinner with the appropriate stops.
	 */
	public void setLineSpinnerEventListeners()
	{
		/* Set a new event listener for the Spinner item selection */
		timetableLineSpinner.setOnItemSelectedListener(new OnItemSelectedListener() 
    	{    
    		/* Implement the onItemSelected method to handle item selections */
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
    	    {
    	    	int selectionPosition = timetableLineSpinner.getSelectedItemPosition();
    	    	if(selectionPosition != Spinner.INVALID_POSITION)
    	    	{
    	    		selectedLine = timetableLineSpinner.getSelectedItem().toString();
    	    		populateStopSpinner();
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
	 * This method sets the event listener for the timetableStopSpinner
	 */
	public void setStopSpinnerEventListeners()
	{
		timetableStopSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			/* Implement the onItemSelected method to handle item selections */
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
    	    {    	    	
    	    	if(timetableStopSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION)
    	    	{
    	    		selectedStop = timetableStopSpinner.getSelectedItem().toString(); 	
    	    		populateScrollView();
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
	 * This method populates the LinearLayout contained in the ScrollView
	 * with the stop times corresponding to the selected line and stops
	 */
	public void populateScrollView()
	{
		ArrayList<String> stopTimes = database.getStoptimes(selectedStop, selectedLine);
		timetableLinearLayout.removeAllViews();
		
		for(String stopTime : stopTimes)
		{
			TextView newView = new TextView(this);
			newView.setText(stopTime);
			newView.setTextSize(30);
			timetableLinearLayout.addView(newView);
		}		
	}
}
