package edu.illinois.geoalarm.test;

import java.util.Calendar;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import edu.illinois.geoalarm.*;

import com.jayway.android.robotium.solo.Solo;
import android.test.suitebuilder.annotation.Smoke;

public class TimedAlarmPopupTest extends ActivityInstrumentationTestCase2<GeoAlarm> 
{
	Activity mActivity;
	Activity mCurrentActivity;
	Solo solo;
	
	public TimedAlarmPopupTest()
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
		solo.clickOnButton("Plan Trip");
		solo.assertCurrentActivity("Expected TripPlannerBus Activity", TripPlannerBus.class);		
		mCurrentActivity = solo.getCurrentActivity();
		selectGoldLine();
		selectStart();
		selectDestination();
		setAlarmOptionsPopUp();
		setAlarm();
		waitForAlarm();
	}	
	
	public void selectGoldLine()
	{		
		solo.pressSpinnerItem(0, 8); // (0,8) Corresponds to "Gold" in the Line Spinner
		assertTrue("Selected Gold", solo.searchText("Gold")); // make sure Gold was selected
	}	
	
	public void selectStart()
	{		
		solo.pressSpinnerItem(1, 10); // Corresponds to "First & Gregory (NE Corner)
		assertTrue("Selected First & Gregory", solo.searchText("First & Gregory"));		
	}	
	
	public void selectDestination()
	{		
		solo.pressSpinnerItem(2, 18); // Corresponds to "Springfield & Gregory St. (NE Corner)
		assertTrue("Selected Springfield & Gregory", solo.searchText("Springfield & Gregory"));			
	}
	
	public void setAlarmOptionsPopUp()
	{
		solo.clickOnText("Alarm Options");
		solo.clickOnText("At Time");
		Calendar c = Calendar.getInstance();		
		solo.setTimePicker(0, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE) + 1); // Set alarm for two minutes from now
		solo.clickOnButton("Set");
		solo.clickOnText("PopUp Message");		
	}
	
	public void setAlarm()
	{
		solo.clickOnText("Set Alarm");
		solo.assertCurrentActivity("Expected RouteMap Activity", RouteMap.class);
		mCurrentActivity = solo.getCurrentActivity();			
	}
	
	public void waitForAlarm()
	{
		assertTrue(solo.waitForText("YOU HAVE ARRIVED", 0, 180000)); // Wait three minutes for arrival
		solo.goBackToActivity("GeoAlarm");
	}
}
