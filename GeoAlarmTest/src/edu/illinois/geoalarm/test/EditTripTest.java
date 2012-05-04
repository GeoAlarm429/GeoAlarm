package edu.illinois.geoalarm.test;

import java.util.Calendar;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import edu.illinois.geoalarm.*;

import com.jayway.android.robotium.solo.Solo;
import android.test.suitebuilder.annotation.Smoke;

/**
 * Tests editing a trip in progress, by setting up a trip, then going back
 * and setting up a second trip
 * @author GeoAlarm
 *
 */

public class EditTripTest extends ActivityInstrumentationTestCase2<GeoAlarm>
{	
	Activity mActivity;
	Activity mCurrentActivity;
	Solo solo;
	
	public EditTripTest()
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
	 * Start planning a trip
	 */
	@Smoke
	public void testChangeTrip()
	{
		mActivity = this.getActivity();
		solo.clickOnImage(0); // Corresponds to plan trip button
		solo.assertCurrentActivity("Expected TripPlannerBus Activity", TripPlanner.class);		
		mCurrentActivity = solo.getCurrentActivity();
		selectGoldLine();
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
		selectStart();
	}
	
	/**
	 * Select starting location
	 */
	public void selectStart()
	{	
		solo.clickOnEditText(1);
		solo.enterText(1, "First & Gregory (NE Corner)");
		solo.sendKey(Solo.ENTER);
		assertTrue("Selected First & Gregory", solo.searchText("First & Gregory"));
		selectDestination();
	}	
	
	/**
	 * Select destination location
	 */
	public void selectDestination()
	{		
		solo.clickOnEditText(2);
		solo.enterText(2, "Springfield & Gregory St. (NE Corner)");
		solo.sendKey(Solo.ENTER);		
		solo.goBack();
		solo.goBack();
		assertTrue("Selected Springfield & Gregory", solo.searchText("Springfield & Gregory"));	
		setAlarmOptions();
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
		setAlarm();
	}
	
	/**
	 * Set the alarm
	 */
	public void setAlarm()
	{
		solo.clickOnText("Set Alarm");
		solo.assertCurrentActivity("Expected RouteMap Activity", RouteMap.class);
		mCurrentActivity = solo.getCurrentActivity();		
		planNewTrip();
	}
	
	/**
	 * Go back and start a new trip
	 */
	public void planNewTrip()
	{
		solo.goBack();
		solo.assertCurrentActivity("Expected TripPlannerBus Activity", TripPlanner.class);
		mCurrentActivity = solo.getCurrentActivity();	
		selectBronzeLine();
	}
	
	/**
	 * Select a second line
	 */
	public void selectBronzeLine()
	{		
		solo.clearEditText(0);
		solo.clickOnEditText(0);
		solo.enterText(0, "Bronze");
		solo.sendKey(Solo.ENTER);
		assertTrue("Selected Bronze", solo.searchText("Bronze")); // make sure Bronze was selected
		selectSecondStart();
	}
	
	/**
	 * Select a second starting location
	 */
	public void selectSecondStart()
	{		
		solo.clearEditText(1);
		solo.clickOnEditText(1);
		solo.enterText(1, "Vine & Green (NW Corner)");
		solo.sendKey(Solo.ENTER);
		assertTrue("Selected Vine & Green", solo.searchText("Vine & Green"));		
		selectSecondDestination();
	}
	
	/**
	 * Select a second destination location
	 */
	public void selectSecondDestination()
	{		
		solo.clearEditText(2);
		solo.clickOnEditText(2);
		solo.enterText(2, "Chemical & Life Sciences");
		solo.sendKey(Solo.ENTER);
		solo.goBack();
		assertTrue("Selected Chemical & Life", solo.searchText("Chemical & Life"));	
		setSecondAlarmOptions();
	}
	
	/**
	 * Select a second set of alarm options
	 */
	public void setSecondAlarmOptions()
	{
		solo.clickOnText("Alarm Options");
		thisWait(1000);
		solo.clickInList(0);
		thisWait(2000);
		solo.clickInList(0);
		solo.clickOnText("At Stop");
		solo.clickOnText("Ring");
		setSecondAlarm();
	}
	
	/**
	 * Set a second alarm
	 */
	public void setSecondAlarm()
	{
		solo.clickOnText("Set Alarm");
		solo.assertCurrentActivity("Expected RouteMap Activity", RouteMap.class);
		mCurrentActivity = solo.getCurrentActivity();	
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
