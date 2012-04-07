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
		solo.clickOnButton("Plan Trip");
		solo.assertCurrentActivity("Expected TripPlannerBus Activity", TripPlannerBus.class);		
		mCurrentActivity = solo.getCurrentActivity();
		selectGoldLine();
	}	
	
	public void selectGoldLine()
	{		
		solo.pressSpinnerItem(0, 8); // (0,8) Corresponds to "Gold" in the Line Spinner
		assertTrue("Selected Gold", solo.searchText("Gold")); // make sure Gold was selected
		selectStart();
	}	
	
	public void selectStart()
	{		
		solo.pressSpinnerItem(1, 10); // Corresponds to "First & Gregory (NE Corner)
		assertTrue("Selected First & Gregory", solo.searchText("First & Gregory"));
		selectDestination();
	}	
	
	public void selectDestination()
	{		
		solo.pressSpinnerItem(2, 18); // Corresponds to "Springfield & Gregory St. (NE Corner)
		assertTrue("Selected Springfield & Gregory", solo.searchText("Springfield & Gregory"));	
		setAlarmOptions();
	}
	
	public void setAlarmOptions()
	{
		solo.clickOnText("Alarm Options");
		solo.clickOnText("At Time");
		Calendar c = Calendar.getInstance();		
		solo.setTimePicker(0, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE) + 2); // Set alarm for two minutes from now
		solo.clickOnButton("Set");
		solo.clickOnText("PopUp Message");
		setAlarm();
	}
	
	public void setAlarm()
	{
		solo.clickOnText("Set Alarm");
		solo.assertCurrentActivity("Expected RouteMap Activity", RouteMap.class);
		mCurrentActivity = solo.getCurrentActivity();		
		numberOfStopsAroundMe();
	}
	
	public void numberOfStopsAroundMe()
	{
		/* Latitude and Longitude of First & Gregory (NE Corner) stop */
		int latitude = -88238511;
		int longitude = 40104150;		
		((RouteMap)mCurrentActivity).setMapCenter(longitude, latitude);
		int numStopsAround = ((RouteMap)mCurrentActivity).getNearStops().size();
		assertEquals(numStopsAround, 29); // Verified by counting
		this.getActivity().finish();
	}
	
	
}
