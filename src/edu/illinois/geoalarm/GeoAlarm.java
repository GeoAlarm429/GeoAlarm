package edu.illinois.geoalarm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.SQLException;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * The main Activity for the GeoAlarm app
 * When the app is launched, this Activity sets the content view
 * to main, and initializes the primary UI elements.
 *
 */
public class GeoAlarm extends Activity 
{
	GeoAlarmDB database;
	SharedPreferences myPrefs;
	private static final long UPDATE_INTERVAL = 1000 * 60 * 60 * 24 * 1; // 1 day

	// Android's default system path for databases
	private static final String DB_PATH = "/data/data/edu.illinois.geoalarm/databases";
	// The name of the database
	private static String DB_NAME = "geoAlarmDB.sqlite";

    @Override
	public void onResume()
	{
		SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
		View v = findViewById(R.id.optionsTopLayout);
		v.setBackgroundColor(settings.getInt("color_value", R.color.Blue));
		super.onResume();
	}

    /** Called when the activity is first created. */
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
		v.setBackgroundColor(settings.getInt("color_value", R.color.Blue));

        if (settings.getBoolean("splash_screen", false))
        {
           // Intent intent = new Intent (this, Splash.class);
            //startActivity(intent);            	
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
		
/////////////////////////////////////////////////////////////////////////////
/////////////  ALL RELATED TO DB UPDATE /////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

        checkForUpdate(); // CALLS DB UPDATE
    }

    public void checkForUpdate()
    {
    	// checks when updated last time
    	if(myPrefs.getFloat("last_update", 0) + UPDATE_INTERVAL < System.currentTimeMillis())
    	{
    		// downloads information data from server
    		String data = ServerConnection.checkForCurrentVersion();
    		double version = 0.0;
    		String address = null;
    		try
			{
				JSONObject userObject = new JSONObject(data);
				version = userObject.getDouble("version");
				address = userObject.getString("address");
				
				String s1 = Environment.getExternalStorageDirectory().toString();
				String s2 =  Environment.getDataDirectory().toString();
				Toast.makeText(this, s1 + " " + s2, Toast.LENGTH_LONG).show();  // DEBUG INFO

				Toast.makeText(this, version + " " + address, Toast.LENGTH_LONG).show();  // DEBUG INFO
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			//editor.putDouble("version", version);
			editor.putFloat("last_update", System.currentTimeMillis());
			editor.commit();

			if (version > 0.0 && address != null) // CHANGE 0.0 TO SOME STORED VALUE
			{
				if(isDownloadManagerAvailable(this))
				{
					try
					{
						downloadAndCopyDB(address, version);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

				}
			}
		}
    }

    /**
     * @param context used to check the device version and DownloadManager information
     * @return true if the download manager is available
     */
    public static boolean isDownloadManagerAvailable(Context context) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public void downloadAndCopyDB(String url, double version) throws IOException
    {
    	DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
    	request.setTitle("GeoAlarm");
    	request.setDescription("DataBase update file");
    	request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "db.sqlite");
    	request.setAllowedOverRoaming(false);
    	request.setShowRunningNotification(true);

    	// get download service and enqueue file
		DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		long id = manager.enqueue(request);

		// gets saved file - IM AFRAID THEAT IT DOESNT WAIT FOR THE FILE TO BE FULLY DOWNLOADED
		ParcelFileDescriptor file = manager.openDownloadedFile(id);

		// copies the db to our db
		File dbFile = getDatabasePath(DB_PATH + DB_NAME);

		InputStream fileStream = new FileInputStream(file.getFileDescriptor());

		OutputStream newDatabase = new FileOutputStream(dbFile);

		byte[] buffer = new byte[1024];
		int length;

		while ((length = fileStream.read(buffer)) > 0)
		{
			newDatabase.write(buffer, 0, length);
		}

        newDatabase.flush();
        fileStream.close();
        newDatabase.close();
    }

    
/////////////////////////////////////////////////////////////////////////////
/////////////  END ALL RELATED TO DB UPDATE /////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
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