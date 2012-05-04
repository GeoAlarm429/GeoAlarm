package edu.illinois.geoalarm.test;

import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import edu.illinois.geoalarm.RouteMap;

/**
 * Tests clicking on a nearby bus stop icon, and ensuring that it displays the correct information
 * @author GeoAlarm
 *
 */

public class AdvanceRouteMapInfoTest  extends ActivityInstrumentationTestCase2<RouteMap>
{
	Activity mActivity;
	Activity mCurrentActivity;
	Solo solo;
	LocationManager manager;
	
	public AdvanceRouteMapInfoTest()
	{
		super("edu.illinois.geoalarm", RouteMap.class);
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		mActivity.finish();
		super.tearDown();
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();		
		mActivity = this.getActivity();
		manager = (LocationManager)mActivity.getSystemService(Context.LOCATION_SERVICE);
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	/**
	 * Sends a fake location to the app, simulates a click on the first overlay item, 
	 * and verifies display information
	 */
	@Smoke
	public void testAdvancedInfoOne()
	{
		
		double latitude = 40.11282333;
		double longitude = -88.22055;	
		Location fakeLocation = new Location(LocationManager.GPS_PROVIDER);		
		
		try
		{
			manager.addTestProvider(LocationManager.GPS_PROVIDER, true, true, true, true, true, true, true, 0, 5);
			manager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
			manager.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null,System.currentTimeMillis());
		}
		catch (Exception ex) {}
		
		fakeLocation.setLatitude(latitude);
		fakeLocation.setLongitude(longitude);
		manager.setTestProviderLocation(LocationManager.GPS_PROVIDER, fakeLocation);	
		((RouteMap)mActivity).setMapCenter((int)(latitude * 1E6), (int)(longitude * 1E6));
		
		thisWait(2000);
		
		((RouteMap)mActivity).clickOnOverlay(); // springfield and gregory ne corner
		
		assertTrue(solo.searchText("Routes Servicing"));
		assertTrue(solo.searchText("Gold"));
		assertTrue(solo.searchText("Goldhopper"));
		assertTrue(solo.searchText("Silver")); 
		assertTrue(solo.searchText("Silver Evening"));
		assertTrue(solo.searchText("Silver Late Night"));
		assertTrue(solo.searchText("Silver Weekend"));		
		
	}
	
	/**
	 * Sleeps the thread for milliseconds
	 * @param millis milliseconds to sleep
	 */
	private void thisWait(long millis)
	{
		try 
		{
			Thread.sleep(millis);
		} 
		catch (InterruptedException e) 
		{			
			e.printStackTrace();
		}
		
	}

}
