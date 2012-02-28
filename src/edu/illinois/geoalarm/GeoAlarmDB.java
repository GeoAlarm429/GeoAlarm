package edu.illinois.geoalarm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author GeoAlaem
 * 
 * GeoAlarmDB dynamically loads an existing database from the assets folder.
 */
public class GeoAlarmDB extends SQLiteOpenHelper
{

	private static final int DATABASE_VERSION = 1;
	// Android's default system path for databases
	private static final String DB_PATH = "/data/data/edu.illinois.geoalarm/databases";
	// The name of the database
	private static String DB_NAME = "geoAlarmDB.sqlite";
	public SQLiteDatabase geoAlarmDB;

	private final Context myContext;

	/**
	 * Constructor
	 *
	 * @param context
	 */
	public GeoAlarmDB(Context context)
	{
		super(context, DB_NAME, null, 1);
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
		geoAlarmDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	}

	@Override
	public synchronized void close()
	{
		if(geoAlarmDB != null)
		{
			geoAlarmDB.close();
		}

		super.close();
	}
	
	/**
	 * To collect bus stops around current location
	 * @param current
	 * @return bus stops near current location
	 * @author Hyung Joo Kim and Seung Mok Lee
	 */
/*	private ArrayList<StopInfo> getAroundMe(Location current){
		// Get all data
	   	Cursor result = myDb.query(DB_TABLE_NAME, new String[] { DB_BUSSTOPS_NAME, DB_BUSSTOPS_SHORT, DB_LONGITUDE, DB_LATITUDE }, 
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
    		longitude = result.getDouble(2);
    		latitude = result.getDouble(3);
    		
    		tempLocation = new Location("gps");
    		tempLocation.setLatitude(latitude);
    		tempLocation.setLongitude(longitude);
    		
    		float distance = current.distanceTo(tempLocation);
    		
    		// if the point is within 400 meters, store it to the array list
    		if ((int)distance < 400){
    			tempStop = new StopInfo(result.getString(0), latitude, longitude);
    			nearStops.add(tempStop);
    		}
    		
    		result.moveToNext();
    	}
    	
    	result.close();
    	return nearStops;
	}*/

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

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
