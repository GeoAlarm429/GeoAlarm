package edu.illinois.geoalarm;

import java.io.IOException;

import android.app.Activity;
import android.database.SQLException;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class TripPlannerBus extends Activity 
{
	GeoAlarmDB database;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_cta_bus);
        
        	// Instantiate the database
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
     		
     		database.geoAlarmDB.close();
     		
     		// do a query
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
	
}
