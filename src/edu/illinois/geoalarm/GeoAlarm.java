package edu.illinois.geoalarm;

import java.io.IOException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.Toast;

/**
 * The main Activity for the GeoAlarm app
 * When the app is launched, this Activity sets the content view
 * to main, and initializes the primary UI elements.
 * @author deflume1
 *
 */
public class GeoAlarm extends Activity 
{
	GeoAlarmDB database;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);       
        
        SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
        View v = findViewById(R.id.mapButton);
        View root = v.getRootView();
        root.setBackgroundColor(settings.getInt("color_value", Color.BLACK));
        if (settings.getBoolean("splash_screen", false))
        {
            Intent intent = new Intent (this, Splash.class);
            startActivity(intent);            	
        }

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
		      
		tareSessionDataValues();
		
        database.close();                
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
	 */
	public void showTripScreen(View view)
	{
		Intent intent = new Intent(view.getContext(), TripPlannerBus.class);
		startActivityForResult(intent, 0);
	}
	
	/** This method is called when the Options button is clicked.
	 *  It launched the Options activity.
	 *  We use the onClick XML attribute in main.xml to bind the method to the click event.
	 */
	public void showOptionsScreen(View view)
	{
		Intent intent = new Intent(view.getContext(), Options.class);
		startActivityForResult(intent, 0);
	}

}