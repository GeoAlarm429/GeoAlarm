package edu.illinois.geoalarm.test;

import java.util.Calendar;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import edu.illinois.geoalarm.*;

import com.jayway.android.robotium.solo.Solo;
import android.test.suitebuilder.annotation.Smoke;

/**
 * Tests that a timed trip alarm signals properly
 * @author deflume1
 *
 */

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
	
	/**
	 * Setup a trip
	 */
	@Smoke
	public void testPlanTrip()
	{		
		solo.clickOnImage(0); // Corresponds to plan trip button
		solo.assertCurrentActivity("Expected TripPlannerBus Activity", TripPlanner.class);		
		mCurrentActivity = solo.getCurrentActivity();
		selectGoldLine();
		selectStart();
		selectDestination();
		setAlarmOptionsPopUp();
		setAlarm();
		waitForAlarm();
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
	 * Select a starting location
	 */
	public void selectStart()
	{		
		solo.clickOnEditText(1);
		solo.enterText(1, "First & Gregory (NE Corner)");
		solo.sendKey(Solo.ENTER);
		assertTrue("Selected First & Gregory", solo.searchText("First & Gregory"));		
	}	
	
	/**
	 * Select a destination location
	 */
	public void selectDestination()
	{		
		solo.clickOnEditText(2);
		solo.enterText(2, "Springfield & Gregory St. (NE Corner)");
		solo.sendKey(Solo.ENTER);		
		solo.goBack();
		solo.goBack();
		assertTrue("Selected Springfield & Gregory", solo.searchText("Springfield & Gregory"));		
	}
	
	/**
	 * Select some alarm options
	 */
	public void setAlarmOptionsPopUp()
	{
		solo.clickOnText("Alarm Options");
		thisWait(1000);
		solo.clickInList(0);
		thisWait(2000);
		solo.clickInList(0);
		
		solo.clickOnText("At Time");
		Calendar c = Calendar.getInstance();		
		solo.setTimePicker(0, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE) + 1); // Set alarm for a minute from now
		solo.clickOnButton("Set");
		solo.clickOnText("PopUp Message");		
	}
	
	/**
	 * Set an alarm
	 */
	public void setAlarm()
	{
		solo.clickOnText("Set Alarm");
		solo.assertCurrentActivity("Expected RouteMap Activity", RouteMap.class);
		mCurrentActivity = solo.getCurrentActivity();			
	}
	
	/**
	 * Wait for alarm to fire
	 */
	public void waitForAlarm()
	{
		assertTrue(solo.waitForText("YOU HAVE ARRIVED", 0, 180000)); // Wait three minutes for arrival
		solo.goBackToActivity("GeoAlarm");
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


