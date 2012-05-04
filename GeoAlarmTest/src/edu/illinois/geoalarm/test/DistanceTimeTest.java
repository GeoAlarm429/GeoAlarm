package edu.illinois.geoalarm.test;

import java.util.Calendar;
import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.Spinner;
import edu.illinois.geoalarm.RouteMap;
import edu.illinois.geoalarm.TripPlanner;

/**
 * Tests that the distance and time data is correctly fetched and displayed
 * @author GeoAlarm
 *
 */

public class DistanceTimeTest extends ActivityInstrumentationTestCase2<TripPlanner> 
{
	Activity mActivity;
	Activity mCurrentActivity;
	Spinner startingSpinner;
	Spinner lineSpinner;
	Solo solo;
	
	public DistanceTimeTest() 
	{
		super("edu.illinois.geoalarm", TripPlanner.class);		
	}
	
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();		
		mActivity = this.getActivity();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		mActivity.finish();
		super.tearDown();
	}
	
	/**
	 * Setup a trip, and verify the distance and time data
	 */
	@Smoke
	public void testCheckDistanceTime()
	{
		selectGoldLine();
		selectStart();
		selectDestination();
		setAlarmOptions();
		setAlarm();		
		checkDistanceAndTime();
	}
	
	/**
	 * Select a line
	 */
	public void selectGoldLine()
	{		
		solo.clickOnEditText(0);
		solo.enterText(0, "Gold");
		solo.sendKey(Solo.ENTER);
		assertTrue("Selected Gold", solo.searchText("Gold")); // make sure Gold was selected		
	}	
	
	/**
	 * Select starting location
	 */
	public void selectStart()
	{		
		solo.clickOnEditText(1);
		solo.enterText(1, "First & Gregory (NE Corner)");
		((TripPlanner)mActivity).makeEditorAction();		
		assertTrue("Selected First & Gregory", solo.searchText("First & Gregory"));			
	}	
	
	/**
	 * Select destination location
	 */
	public void selectDestination()
	{		
		solo.clickOnEditText(2);
		solo.enterText(2, "Balboa & Southwest Dr. (NE Corner)");
		solo.sendKey(Solo.ENTER);		
		solo.goBack();
		solo.goBack();
		assertTrue("Selected Balboa & Southwest", solo.searchText("Balboa & Southwest"));		
	}
	
	/**
	 * Set some alarm options
	 */
	public void setAlarmOptions()
	{
		solo.clickOnText("Alarm Options");
		thisWait(1000);
		solo.clickInList(0);
		thisWait(2000);
		solo.clickInList(0);
		solo.clickOnText("At Time");
		Calendar c = Calendar.getInstance();		
		solo.setTimePicker(0, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE) + 2); // Set alarm for two minutes from now
		solo.clickOnButton("Set");
		solo.clickOnText("PopUp Message");
	}
	
	/**
	 * Set the alarm
	 */
	public void setAlarm()
	{
		solo.clickOnText("Set Alarm");
		solo.assertCurrentActivity("Expected RouteMap Activity", RouteMap.class);
		mCurrentActivity = solo.getCurrentActivity();			
	}
	
	/**
	 * Check for data
	 */
	public void checkDistanceAndTime()
	{		
		assertTrue(solo.searchText("6 mins"));
		assertTrue(solo.searchText("1.5 mi"));
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
