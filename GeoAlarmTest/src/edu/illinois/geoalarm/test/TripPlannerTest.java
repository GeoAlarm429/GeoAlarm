package edu.illinois.geoalarm.test;

import edu.illinois.geoalarm.TripPlanner;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Spinner;
import com.jayway.android.robotium.solo.Solo;
import android.test.suitebuilder.annotation.Smoke;

/**
 * Tests the functionality of the trip planner
 * @author GeoAlarm
 *
 */
public class TripPlannerTest extends ActivityInstrumentationTestCase2<TripPlanner> 
{
	Activity mActivity;
	Spinner startingSpinner;
	Spinner lineSpinner;
	Solo solo;
	
	public TripPlannerTest() 
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
		
	@Smoke
	public void testSelectLine()
	{
		solo.clearEditText(0);
		solo.clickOnEditText(0);
		solo.enterText(0, "Gold");
		solo.sendKey(Solo.ENTER);
		assertTrue("Selected Gold", solo.searchText("Gold")); // make sure Gold was selected		
	}

	
	/**
	 * Tests selection of a starting location, by typing the location into the box,
	 * and verifying it
	 */
	@Smoke
	public void testSelectStartingLocation()
	{
		testSelectLine();
		solo.clearEditText(1);
		solo.clickOnEditText(1);
		solo.enterText(1, "First & Gregory (NE Corner)");
		solo.sendKey(Solo.ENTER);
		assertTrue("Selected First & Gregory", solo.searchText("First & Gregory"));	
	}
	
	/**
	 * Tests selection of a destination location, by typing the location into the box,
	 * and verifying it
	 */
	@Smoke
	public void testSelectDestinationLocation()
	{
		testSelectLine();
		solo.clearEditText(2);
		solo.clickOnEditText(2);
		solo.enterText(2, "Springfield & Gregory St. (NE Corner)");
		solo.sendKey(Solo.ENTER);		
		assertTrue("Selected Springfield & Gregory", solo.searchText("Springfield & Gregory"));		
	}
	
	/**
	 * Tests the At Stop and Ring alarm options, by clicking on them
	 */
	@Smoke
	public void testAlarmOptionsOne()
	{
		solo.clickOnButton("Alarm Options");
		thisWait(1000);
		solo.clickInList(0);
		thisWait(2000);
		solo.clickInList(0);			
		solo.clickOnText("At Stop");		
		solo.clickOnText("Ring");
	}
	
	/**
	 * Tests the Station Before Stop and Vibrate options, by clicking on them
	 */
	@Smoke
	public void testAlarmOptionsTwo()
	{
		solo.clickOnButton("Alarm Options");
		thisWait(1000);
		solo.clickInList(0);
		thisWait(2000);
		solo.clickInList(0);
		solo.clickOnText("Station Before Stop");
		solo.clickOnText("Vibrate");
	}
	
	/**
	 * Tests the At Time option, by clicking on them
	 */
	@Smoke
	public void testAlarmOptionsThree()
	{
		solo.clickOnButton("Alarm Options");
		thisWait(1000);
		solo.clickInList(0);
		thisWait(2000);
		solo.clickInList(0);
		solo.clickOnText("At Time");
		
		solo.setTimePicker(0, 7, 33);
		boolean expected1 = true;
		boolean actual1 = solo.searchText("7:33 AM");
		assertEquals("Correct time set", expected1, actual1);
		
	}	
	
	/**
	 * Tests the auto complete function for lines, by entering the first letter of a line, 
	 * and checking that correct lines display
	 */
	@Smoke
	public void testAutoCompleteLine()
	{
		solo.clearEditText(0);
		solo.clickOnEditText(0);
		solo.enterText(0, "g");
		
		assertTrue(solo.searchText("Gold"));
		assertTrue(solo.searchText("Gold Alternate"));
		assertTrue(solo.searchText("Goldhopper"));		
	}
	
	/**
	 * Tests the auto complete function for starting locations, by entering the first letter of a location, 
	 * and checking that correct locations display
	 */
	@Smoke
	public void testAutoCompleteStart()
	{
		solo.clearEditText(0);
		solo.clickOnEditText(0);
		solo.enterText(0, "g");
		solo.clickOnText("Gold");
		solo.sendKey(Solo.ENTER);
		
		solo.clearEditText(1);
		solo.clickOnEditText(1);
		solo.enterText(1, "k");
		
		assertTrue(solo.searchText("Kirby & First"));
		assertTrue(solo.searchText("Kirby & Arrow"));		
	}
	
	/**
	 * Tests the auto complete functions for destination locations, by entering the first letter of a location, 
	 * and checking that correct locations display
	 */
	@Smoke
	public void testAutoCompleteDest()
	{		
		solo.clearEditText(0);
		solo.clickOnEditText(0);
		solo.enterText(0, "g");
		solo.clickOnText("Gold");
		solo.sendKey(Solo.ENTER);
		
		solo.clearEditText(1);
		solo.clickOnEditText(1);
		solo.enterText(1, "k");
		solo.clickOnText("Kirby & First");
		solo.sendKey(Solo.ENTER);
		
		solo.clearEditText(1);
		solo.clickOnEditText(1);
		solo.enterText(1, "l");
		assertTrue(solo.searchText("Chemical & Life"));
		assertTrue(solo.searchText("Devonshire"));
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
