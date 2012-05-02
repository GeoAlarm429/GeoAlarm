package edu.illinois.geoalarm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

/**
 * @author GeoAlaem
 * 
 * GeoAlarmDB dynamically loads an existing database from the assets folder.
 */
public class GeoAlarmDB extends SQLiteOpenHelper
{

	private static final int DATABASE_VERSION = 1;
	// Android's default system path for databases
	private static final String DB_PATH = "/data/data/edu.illinois.geoalarm/databases/";
	// The name of the database
	private static String DB_NAME = "geoAlarmDB.sqlite";
	
    // Table name
    private static String DB_TABLE_NAME = "Station";
    
    // Field name
    private final static String DB_ID = "stationID";
    private final static String DB_BUSSTOPS_NAME = "name";
    private final static String DB_LONGITUDE = "lng";
    private final static String DB_LATITUDE = "lat";
    
    public final static String DB_TX = "tx";
    public final static String DB_RX = "rx";
    public final static String DB_TX_SESSION = "tx_session";
    public final static String DB_RX_SESSION = "rx_session";
    public final static String DB_TX_TARE_SESSION = "tx_tare";
    public final static String DB_RX_TARE_SESSION = "rx_tare";
    public final static String DB_RING_LENGTH = "ring_length";
    public final static String DB_VIBRATE_LENGTH = "vibrate_length";
    public final static String DB_BACKGROUND_COLOR = "background_color";

    // SQL command to create a table
    private static final String DB_CREATE_SQL = "CREATE TABLE " + DB_TABLE_NAME +  
												" (" + DB_ID + " INTEGER PRIMARY KEY, " + DB_LONGITUDE + 
												" DOUBLE, " + DB_LATITUDE + " DOUBLE, " +
												DB_BUSSTOPS_NAME + " VARCHAR);)";
    
    private ArrayList<StopInfo> nearStops;	
	public SQLiteDatabase geoAlarmDB;
	private final Context myContext;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public GeoAlarmDB(Context context)
	{
		super(context, DB_NAME, null, DATABASE_VERSION);
		this.myContext = context;
	}

	// Creates an empty database on the system and rewrites it with the specified database table
	public void createDataBase() throws IOException
	{
		boolean dbExist = checkDataBase();

		if (dbExist)
		{
			// do nothing - database already exist, which is what we want
		}
		else
		{
			// By calling this method an empty database will be created into the default system path
			// of the application so application will be able to overwrite that database with our database
			this.getReadableDatabase();
			try
			{
				copyDataBase();
			}
			catch (IOException e)
			{
				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time user opens the application.
	 * 
	 * @return true if db exists, false otherwise
	 */
	private boolean checkDataBase()
	{
		SQLiteDatabase checkDB = null;

		try
		{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		}
		catch(SQLiteException e) {
			//database does't exist yet
		}

		if(checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies database from local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transferring byte stream.
	 * 
	 * We followed the outline on:
	 * http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
	 */
	private void copyDataBase() throws IOException
	{
		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
 
		// Transfer bytes from the input file to the output file
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0)
		{
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/**
	 * Opens the database from existing SQLite database file and loads it into geoAlarmDB global
	 * 
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLException
	{
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		geoAlarmDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	}

	@Override
	public synchronized void close()
	{
		try
		{
			
			if(geoAlarmDB != null)
			{
				geoAlarmDB.close();
			}

			super.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * To collect bus stops around current location
	 * @param current
	 * @return bus stops near current location
	 * @author Hyung Joo Kim and Seung Mok Lee
	 */
	public ArrayList<StopInfo> getAroundMe(Location current){
		// Get all data
	   	Cursor result = geoAlarmDB.query(DB_TABLE_NAME, null, 
									null, null, null, null, null);
	   	
    	if (result != null){
    		result.moveToFirst();
    	}
    	
    	nearStops = new ArrayList<StopInfo>();
    	double latitude = 0.0;
    	double longitude = 0.0;
    	Location tempLocation = null;
    	StopInfo tempStop = null;
    	
    	// Save near stops to array list
    	while(!result.isAfterLast()){
    		longitude = result.getDouble(1);
    		latitude = result.getDouble(2);
    		
    		tempLocation = new Location("gps");
    		tempLocation.setLatitude(latitude);
    		tempLocation.setLongitude(longitude);
    		
    		float distance = current.distanceTo(tempLocation);
    		
    		// if the point is within 400 meters, store it to the array list
    		if ((int)distance < 400){
    			tempStop = new StopInfo(result.getString(3), latitude, longitude);
    			nearStops.add(tempStop);
    		}
    		
    		result.moveToNext();
    	}
    	
    	result.close();
    	return nearStops;
	}
	
	/**
	 * This method returns an ArrayList<<String>> containing the names of the bus lines,
	 * ordered by spelling
	 * in the database.
	 * @return An ArrayList<<String>> of the bus lines in the database
	 */
	public ArrayList<String> getBusLines()
	{
		ArrayList<String> lineList = new ArrayList<String>();
		
		Cursor result = geoAlarmDB.query("Routes", new String[] {"name"}, null, null, null, null, "name");
		
		if(result.moveToFirst())
		{
			while(result.isAfterLast() == false)
			{
				int nameColumn = result.getColumnIndex("name");
			
				String newName = result.getString(nameColumn);
				lineList.add(newName);
			
				result.moveToNext();
			}
		}
		
		return lineList;
	}
	
	/**
	 * This method returns an ArrayList<<String>> containing the stops on a particular
	 * bus line. It first queries the Routes table to get the routeID corresponding to
	 * the selectedLine.  Then it queries the Route_Station table to get all the stationIDs
	 * associated with that route.  Finally, it queries the Station table to get the 
	 * station names.
	 * @param selectedLine The line selected by the user
	 * @return A list of the stations associated with a particular line
	 */
	public ArrayList<String> getLineStops(String selectedLine) 
	{		
		/* Get corresponding routeID from Routes table */
		int routeID = getRouteIDfromRouteName(selectedLine);
		
		/* Get list of stationIDs from Route_Station table */
		ArrayList<Integer> stationIDList = getStationIDsfromRouteID(routeID);
		
		/* Get stationID names from Station table */		
		ArrayList<String> stopList = getStationNamesFromStationIDs(stationIDList);
		
		return stopList;
	}
	
	/**
	 * This method queries the Routes table, and returns the routeID corresponding to the given route name
	 * @param name A string representing the name of a Route
	 * @return The routeID corresponding to the route
	 */
	public int getRouteIDfromRouteName(String name)
	{
		Cursor result = geoAlarmDB.query("Routes", new String[] {"routeID"}, "name = '" + name + "'", null, null, null, null);
		int routeID = 0;		
		if(result.moveToFirst())
		{
			int columnIndex = result.getColumnIndex("routeID");
			routeID = result.getInt(columnIndex);
		}
		result.close();
		return routeID;	 
	}
	
	/**
	 * This method queries the Route_Station table, and returns the list of stations on a route
	 * @param routeID An integer routeID
	 * @return An ArrayList of the stationIDs on the route
	 */
	public ArrayList<Integer> getStationIDsfromRouteID(int routeID)
	{
		Cursor result = geoAlarmDB.query("Route_Station", new String[] {"stationID"}, "routeID = " + routeID, null, null, null, null);		
		ArrayList<Integer> stationIDList = new ArrayList<Integer>();		
		if(result.moveToFirst())
		{
			while(result.isAfterLast() == false)
			{
				int columnIndex = result.getColumnIndex("stationID");
				stationIDList.add(new Integer(result.getInt(columnIndex)));
				result.moveToNext();
			}
		}
		return stationIDList;		
	}
	
	/**
	 * This method queries the Station table, and returns the a list of station names corresponding to the
	 * stationIDs in the stationID list
	 * @param stationIDList A list of stationIDs
	 * @return A list of station names
	 */
	public ArrayList<String> getStationNamesFromStationIDs(ArrayList<Integer> stationIDList)
	{
		ArrayList<String> stopList = new ArrayList<String>();
		for(int stationIDIndex = 0; stationIDIndex < stationIDList.size(); stationIDIndex++)
		{
			Cursor result = geoAlarmDB.query("Station", new String[]{"name"}, "stationID = " + stationIDList.get(stationIDIndex), null, null, null, null);
			if(result.moveToFirst())
			{
				while(result.isAfterLast() == false)
				{
					int columnIndex = result.getColumnIndex("name");
					stopList.add(result.getString(columnIndex));
					result.moveToNext();
				}
			}
			result.close();
		}
		return stopList;
	}
	
	/**
	 * This method returns the latitude for a given station name
	 * @param stationName The name of a station (from the name column in the database)
	 * @return A double representing the latitude
	 */
	public double getLatitude(String stationName)
	{		
		Cursor result = geoAlarmDB.query("Station", new String[]{"lat"}, "name = \"" + stationName + "\"", null, null, null, null, null);
		double latitude = 0.0;
		if(result.moveToFirst())
		{
			latitude = result.getDouble(result.getColumnIndex("lat"));
		}
		return latitude;
	}
	
	/**
	 * This method returns the longitude for a given station name	
	 * @param stationName The name of a station (from the name column in the database)
	 * @return A double representing the longitude
	 */
	public double getLongitude(String stationName)
	{
		Cursor result = geoAlarmDB.query("Station", new String[]{"lng"}, "name = \"" + stationName + "\"", null, null, null, null, null);
		double longitude = 0.0;
		if(result.moveToFirst())
		{
			longitude = result.getDouble(result.getColumnIndex("lng"));
		}
		return longitude;
	}
	
	/**
	 * This method makes sure the table for data usage storage is setup.  Specifically, it creates the
	 * table rows if they don't exist, and resets the temp rows to zero.
	 */
	public void setupUsageDataTable()
	{
		Cursor result = geoAlarmDB.query("UsageData", new String[]{"name"}, null, null, null, null, null);
		
		if(result.getCount() < 4)
		{
			result.close();			
			ContentValues values = new ContentValues();
			
			values.put("name", DB_TX);
			values.put("bytes", 0);
			geoAlarmDB.insert("UsageData", null, values);
			values.clear();
			
			values.put("name", DB_RX);
			values.put("bytes", 0);
			geoAlarmDB.insert("UsageData", null, values);
			values.clear();
			
			values.put("name", DB_TX_SESSION);
			values.put("bytes", 0);
			geoAlarmDB.insert("UsageData", null, values);
			values.clear();
			
			values.put("name", DB_RX_SESSION);
			values.put("bytes", 0);
			geoAlarmDB.insert("UsageData", null, values);
			values.clear();
			
			values.put("name", DB_TX_TARE_SESSION);
			values.put("bytes", 0);
			geoAlarmDB.insert("UsageData", null, values);
			values.clear();			
			
			values.put("name", DB_RX_TARE_SESSION);
			values.put("bytes", 0);
			geoAlarmDB.insert("UsageData", null, values);
			values.clear();			
			
		}
		else
		{
			result.close();
			ContentValues values = new ContentValues();
			values.put("name", DB_TX_SESSION);
			values.put("bytes", 0);			
			geoAlarmDB.update("UsageData", values, "name = \"" + DB_TX_SESSION + "\"", null);
			values.clear();
			
			values.put("name", DB_RX_SESSION);
			values.put("bytes", 0);
			geoAlarmDB.update("UsageData", values, "name = \"" + DB_RX_SESSION + "\"", null);
			values.clear();
			
			values.put("name", DB_TX_TARE_SESSION);
			values.put("bytes", 0);
			geoAlarmDB.update("UsageData", values, "name = \"" + DB_TX_TARE_SESSION + "\"", null);
			
			values.put("name", DB_RX_TARE_SESSION);
			values.put("bytes", 0);
			geoAlarmDB.update("UsageData", values, "name = \"" + DB_RX_TARE_SESSION + "\"", null);
		}
		
	}
	
	/**
	 * This function sets row (name, bytes) of UsageData 
	 * @param name The name of the row
	 * @param bytes The number of bytes to set
	 */
	public void setBytes(String name, long bytes)
	{
		if(geoAlarmDB != null)
		{		
			ContentValues values = new ContentValues();
			values.put("name", name);
			values.put("bytes", bytes);
			geoAlarmDB.update("UsageData", values, "name = \"" + name + "\"", null);			
		}
	}	
	
	/**
	 * Returns the number of bytes in row name of the table UsageData
	 * @param name The name of the row
	 * @return The number of bytes associated with name
	 */
	public long getBytes(String name)
	{
		long numBytes = 0;
		if(geoAlarmDB != null)
		{
			Cursor result = geoAlarmDB.query("UsageData", new String[] {"bytes"}, "name = \"" + name + "\"", null, null, null, null);
			if(result.moveToFirst())
			{
				numBytes = result.getLong(result.getColumnIndex("bytes"));
			}
			result.close();
		}
		return numBytes;
	}	
	
	/**
	 * Returns the stationID corresponding to the given station name
	 * @param stationName A station name
	 * @return The stationID
	 */
	public int getStationIDFromStationName(String stationName)
	{
		Cursor result = geoAlarmDB.query("Station", new String[] {"stationID"}, "name = \"" + stationName + "\"", null, null, null, null);
		int stationID = 0;
		if(result.moveToFirst())
		{
			stationID = result.getInt(result.getColumnIndex("stationID"));
		}
		result.close();
		return stationID;
	}
	
	/**
	 * Returns the list of stop times corresponding to the given station name and
	 * route name
	 * @param stationName The name of a station
	 * @param routeName The name of a route
	 * @return An ArrayList<<String>> containing the stop times sorted in ascending order
	 */
	public ArrayList<String> getStoptimes(String stationName, String routeName)
	{		
		ArrayList<String> stopTimes = new ArrayList<String>();
		
		int stationID = getStationIDFromStationName(stationName);
		int routeID = getRouteIDfromRouteName(routeName);		
		
		Cursor result = geoAlarmDB.query("Timetable", new String[] {"arrivalTime"}, "stationID = " + stationID + " and routeID = " + routeID , null, null, null, "arrivalTime asc");			
		
		if(result.moveToFirst())
		{
			DateFormat d = DateFormat.getTimeInstance(DateFormat.SHORT);
			
			while(!result.isAfterLast())
			{
				Time arr = Time.valueOf(result.getString(0));				
				arr.setSeconds(0);					
				stopTimes.add(d.format(arr));				
				result.moveToNext();
			}
			
		}		
		result.close();
		return stopTimes;		
	}
	
	/**
	 * Returns the list of stops that service a particular station
	 * @param stationName The name of the station
	 * @return An array list of strings representing the station
	 */
	public ArrayList<String> getLinesForStopName(String stationName)
	{
		ArrayList<String> lines = new ArrayList<String>();
		
		int stationID = getStationIDFromStationName(stationName);
		
		Cursor result = geoAlarmDB.query("Route_Station", new String[] {"routeID"}, "stationID = " + stationID, null, null, null, null);
		ArrayList<Integer> routeIDs = new ArrayList<Integer>();
		if(result.moveToFirst())
		{
			while(!result.isAfterLast())
			{				
				routeIDs.add(result.getInt(0));
				result.moveToNext();
			}
		}
		result.close();
		
		for(int routeIDIndex = 0; routeIDIndex < routeIDs.size(); routeIDIndex++)
		{
			result = geoAlarmDB.query("Routes", new String[]{"name"}, "routeID = " + routeIDs.get(routeIDIndex), null, null, null, null);
			if(result.moveToFirst())
			{
					int columnIndex = result.getColumnIndex("name");
					lines.add(result.getString(columnIndex));					
			}
			result.close();
		}		
		
		return lines;		
	}

	public Context getMyContext()
	{
		return myContext;
	}

	public static String getDB_NAME()
	{
		return DB_NAME;
	}

	public static void setDB_NAME(String dB_NAME)
	{
		DB_NAME = dB_NAME;
	}

	public SQLiteDatabase getGeoAlarmDB()
	{
		return geoAlarmDB;
	}

	public void setProfessorDroidDB(SQLiteDatabase geoAlarmDB)
	{
		this.geoAlarmDB = geoAlarmDB;
	}

	public static int getDatabaseVersion() {
		return DATABASE_VERSION;
	}

	public static String getDbPath() {
		return DB_PATH;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	       Log.i("onCreate", "Creating the database...");
	       db.execSQL(DB_CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	
}
