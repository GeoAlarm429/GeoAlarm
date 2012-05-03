package edu.illinois.geoalarm.test;

import java.util.Calendar;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import edu.illinois.geoalarm.*;
import com.jayway.android.robotium.solo.Solo;
import android.test.suitebuilder.annotation.Smoke;


/**
 * This class tests a full trip made by the trip planner
 * @author deflume1
 *
 */
public class FullTripTest extends ActivityInstrumentationTestCase2<GeoAlarm> 
{
	Activity mActivity;
	Activity mCurrentActivity;
	Solo solo;
	
	public FullTripTest()
	{
		super("edu.illinois.geoalarm", GeoAlarm.class);
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
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Smoke
	public void testPlanTrip()
	{
		mActivity = this.getActivity();
		solo.clickOnImage(0); // Corresponds to plan trip button
		solo.assertCurrentActivity("Expected TripPlannerBus Activity", TripPlanner.class);		
		mCurrentActivity = solo.getCurrentActivity();
		selectGoldLine();
		selectStart();
		selectDestination();
		setAlarmOptions();
		setAlarm();
		numberOfStopsAroundMe();
	}	
	
	public void selectGoldLine()
	{		
		solo.clickOnEditText(0);
		solo.enterText(0, "Gold");
		solo.sendKey(Solo.ENTER);
		assertTrue("Selected Gold", solo.searchText("Gold")); // make sure Gold was selected		
	}	
	
	public void selectStart()
	{		
		solo.clickOnEditText(1);
		solo.enterText(1, "First & Gregory (NE Corner)");
		solo.sendKey(Solo.ENTER);
		assertTrue("Selected First & Gregory", solo.searchText("First & Gregory"));			
	}	
	
	public void selectDestination()
	{		
		solo.clickOnEditText(2);
		solo.enterText(2, "Springfield & Gregory St. (NE Corner)");
		solo.sendKey(Solo.ENTER);		
		solo.goBack();
		solo.goBack();
		assertTrue("Selected Springfield & Gregory", solo.searchText("Springfield & Gregory"));		
	}
	
	public void setAlarmOptions()
	{
		solo.clickOnText("Alarm Options");
		solo.clickInList(0);
		try 
		{
			Thread.sleep(1000);
		} 
		catch (InterruptedException e) 
		{			
			e.printStackTrace();
		}		
		solo.clickInList(0);
		solo.clickOnText("At Time");
		Calendar c = Calendar.getInstance();		
		solo.setTimePicker(0, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE) + 2); // Set alarm for two minutes from now
		solo.clickOnButton("Set");
		solo.clickOnText("PopUp Message");
	}
	
	public void setAlarm()
	{
		solo.clickOnText("Set Alarm");
		solo.assertCurrentActivity("Expected RouteMap Activity", RouteMap.class);
		mCurrentActivity = solo.getCurrentActivity();			
	}
	
	public void numberOfStopsAroundMe()
	{
		/* Latitude and Longitude of First & Gregory (NE Corner) stop */
		int latitude = -88238511;
		int longitude = 40104150;		
		((RouteMap)mCurrentActivity).setMapCenter(longitude, latitude);
		int numStopsAround = ((RouteMap)mCurrentActivity).getNearStops().size();
		assertEquals(numStopsAround, 29); // Verified by counting
		solo.goBackToActivity("GeoAlarm");
	}
	
	
}
