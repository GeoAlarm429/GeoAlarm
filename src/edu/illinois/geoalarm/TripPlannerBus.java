package edu.illinois.geoalarm;

import java.io.IOException;

import android.app.Activity;
import android.database.SQLException;
import android.os.Bundle;

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
	
}
