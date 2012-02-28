package edu.illinois.geoalarm;

import java.io.IOException;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
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
	GeoAlarmDB database;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
		
		// Execute SQLite to retrieve lines
		Cursor theCursor = database.geoAlarmDB.query("Routes", null, null, null, null, null, null);

		if(theCursor != null)
		{
			theCursor.moveToFirst();
					
			String[] lineNames = theCursor.getColumnNames();
					
			for(int i = 0; theCursor.isAfterLast() != false; i++)
			{
				int nameColumn = theCursor.getColumnIndex("name");
				lineNames[i] = theCursor.getString(nameColumn);

				theCursor.moveToNext();
			}
		}

		theCursor.close();
		database.geoAlarmDB.close();
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