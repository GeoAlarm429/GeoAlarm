package edu.illinois.geoalarm.test;

import edu.illinois.geoalarm.TripPlannerBus;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Spinner;
import com.jayway.android.robotium.solo.Solo;
import android.test.suitebuilder.annotation.Smoke;

public class TripPlannerBusTest extends ActivityInstrumentationTestCase2<TripPlannerBus> 
{
	Activity mActivity;
	Spinner startingSpinner;
	Spinner lineSpinner;
	Solo solo;
	
	public TripPlannerBusTest() 
	{
		super("edu.illinois.geoalarm", TripPlannerBus.class);		
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
	
	@Smoke
	public void testAlarmOptionsOne()
	{
		solo.clickOnButton("Alarm Options");
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
		solo.clickOnText("At Stop");		
		solo.clickOnText("Ring");
	}
	
	@Smoke
	public void testAlarmOptionsTwo()
	{
		solo.clickOnButton("Alarm Options");
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
		solo.clickOnText("Station Before Stop");
		solo.clickOnText("Vibrate");
	}
	
	@Smoke
	public void testAlarmOptionsThree()
	{
		solo.clickOnButton("Alarm Options");
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
		
		solo.setTimePicker(0, 7, 33);
		boolean expected1 = true;
		boolean actual1 = solo.searchText("7:33 AM");
		assertEquals("Correct time set", expected1, actual1);
		
	}	
	
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
}
