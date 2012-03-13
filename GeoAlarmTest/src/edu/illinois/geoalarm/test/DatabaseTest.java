package edu.illinois.geoalarm.test;

import edu.illinois.geoalarm.*;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;

public class DatabaseTest extends ActivityInstrumentationTestCase2<TripPlannerBus>
{
	GeoAlarmDB database;
	
	public DatabaseTest() 
	{
		super("edu.illinois.geoalarm", TripPlannerBus.class);
	}

	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();		
		database = this.getActivity().getDatabase();
	}
	
	public void testPreconditions() 
	{
		assertNotNull(database);
	}
	
	/**
	 * Try to query the Routes table, and see if we can pull any data
	 */
	public void testQueryRoutes()
	{
		Cursor aCursor = database.geoAlarmDB.query("Routes", new String[] {"name"}, null, null, null, null, null);
		assertNotNull(aCursor);
		assertTrue(aCursor.moveToFirst());
		assertFalse(aCursor.isAfterLast());
		aCursor.close();
	}
	
	/**
	 * Try to query the Station table, and see if we can pull any data
	 */
	public void testQueryStations()
	{
		Cursor aCursor = database.geoAlarmDB.query("Station", new String[] {"name"}, null, null, null, null, null);
		assertNotNull(aCursor);
		assertTrue(aCursor.moveToFirst());
		assertFalse(aCursor.isAfterLast());
		aCursor.close();
	}
	
	/**
	 * Query the Station table, find a route corresponding to the station, then find the route name
	 */
	public void testQueryRoutesStations()
	{
		Cursor aCursor = database.geoAlarmDB.query("Station", new String[] {"stationID"}, null, null, null, null, null);
		assertNotNull(aCursor);
		assertTrue(aCursor.moveToFirst());
		assertFalse(aCursor.isAfterLast());
		int stationID = aCursor.getInt(0);
		aCursor.close();
		
		aCursor = database.geoAlarmDB.query("Route_Station", new String[] {"routeID"}, "stationID = " + stationID, null, null, null, null);
		assertNotNull(aCursor);
		assertTrue(aCursor.moveToFirst());
		assertFalse(aCursor.isAfterLast());
		int routeID = aCursor.getInt(0);
		aCursor.close();
		
		aCursor = database.geoAlarmDB.query("Routes", new String[] {"name"}, "routeID = " + routeID, null, null, null, null);
		assertNotNull(aCursor);
		assertTrue(aCursor.moveToFirst());
		assertFalse(aCursor.isAfterLast());
		String routeName = aCursor.getString(0);
		aCursor.close();
		
		assertNotNull(routeName);
	}
	
	
}
