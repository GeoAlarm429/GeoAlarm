package edu.illinois.geoalarm;

import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Process;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * The main Activity for the GeoAlarm app
 * When the app is launched, this Activity sets the content view
 * to main, and initializes the primary UI elements.
 * @author GeoAlarm
 *
 */
public class GeoAlarm extends Activity 
{
	GeoAlarmDB database;
	SharedPreferences myPrefs;
	Activity thisActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);     
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
       
		myPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
		View v = findViewById(R.id.optionsTopLayout);
		v.setBackgroundResource(settings.getInt("color_value", R.color.Blue));      	
		thisActivity = this;
		
		boolean firstRun = settings.getBoolean("geo_alarm_first_run", true);
		if(firstRun)
		{
			if(!isOnline())
			{
				AlertDialog.Builder failureBuilder = new AlertDialog.Builder(this);
				failureBuilder.setMessage("Network connection failure! GeoAlarm must be connected to the internet on first run!");
				failureBuilder.setTitle("Sorry!");
				failureBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) 
					{
						thisActivity.finish();
					}
					
				});
				AlertDialog failure = failureBuilder.create();
				failure.show();
			}
			else
			{							
				AlertDialog.Builder downloadDB = new AlertDialog.Builder(this);
				downloadDB.setMessage("You need to download the database! Click OK to go to the Options screen and download it!");
				downloadDB.setTitle("Welcome!");
				downloadDB.setPositiveButton("Go to Options", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) 
					{					
						Intent optionIntent = new Intent(GeoAlarm.this, Options.class);
						startActivityForResult(optionIntent, 0);					
					}
				
				});
				AlertDialog success = downloadDB.create();
				success.show();
			}
		}
		else
		{
			loadDatabase();
			tareSessionDataValues();
		}
    }
    
    @Override
	public void onResume()
	{		
		super.onResume();
		
		SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
		View v = findViewById(R.id.optionsTopLayout);
		v.setBackgroundResource(settings.getInt("color_value", R.color.Blue));
		
		boolean firstRun = settings.getBoolean("geo_alarm_first_run", true);
		
		if(!firstRun)
		{
			loadDatabase();
		}		
	}
    
    @Override
    protected void onPause()
    {
    	if(database != null)
    	{
    		database.close();
    	}
    	super.onPause();
    }
    
    /**
	 * Checks whether we have a network connection
	 * @return true if connected, false otherwise
	 */
	public boolean isOnline() 
	{
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
    
    
    /**
	 * Helper function to load the database
	 */
	public void loadDatabase()
	{
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
	}            
   
    /**
     * This method gets the tare data values for the session, and stores them in the DB
     */
    public void tareSessionDataValues()
    {
    	/* Get tare data values for this session and store them */        
        long numBytesReceivedAtStart = 0;
        numBytesReceivedAtStart = TrafficStats.getUidRxBytes(Process.myUid());	
        long numBytesTransmittedAtStart = 0;
        numBytesTransmittedAtStart = TrafficStats.getUidTxBytes(Process.myUid());   
        
        database.setupUsageDataTable();
        database.setBytes(GeoAlarmDB.DB_RX_TARE_SESSION, numBytesReceivedAtStart);
        database.setBytes(GeoAlarmDB.DB_TX_TARE_SESSION, numBytesTransmittedAtStart);    	
    }

    /** This method is called when the Map button is clicked.
     *  It launches the RouteMap activity.
 	 *  We use the onClick XML attribute in main.xml to bind the method to the click event.
     */
	public void showMapScreen(View view)
	{
		Intent intent = new Intent(view.getContext(), RouteMap.class);
		intent.putExtra("edu.illinois.geoalarm.isPlannedTrip", false);
		startActivityForResult(intent, 0);		
	}
	
	/** This method is called when the Trip button is clicked.
	 *  It launches the TripPlanner activity.
	 *  We use the onClick XML attribute in main.xml to bind the method to the click event.
	 *  @param view The button that was clicked
	 */
	public void showTripScreen(View view)
	{
		Intent intent = new Intent(view.getContext(), TripPlanner.class);
		startActivityForResult(intent, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
		case R.id.options:
			Intent optionIntent = new Intent(GeoAlarm.this, Options.class);
			startActivityForResult(optionIntent, 0);
			return true;
		case R.id.contact:
			Intent contactIntent = new Intent(GeoAlarm.this, Contact.class);
			startActivityForResult(contactIntent, 0);
			return true;
		}
		return false;
	}

}