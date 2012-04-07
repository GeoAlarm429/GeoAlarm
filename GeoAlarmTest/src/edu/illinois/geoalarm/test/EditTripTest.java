package edu.illinois.geoalarm.test;

import java.util.Calendar;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import edu.illinois.geoalarm.*;

import com.jayway.android.robotium.solo.Solo;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;

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
	protected void setUp() throws Exception
	{
		super.setUp();
		mActivity = this.getActivity();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Smoke
	public void testChangeTrip()
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
		planNewTrip();
	}
	
	public void planNewTrip()
	{
		solo.goBack();
		solo.assertCurrentActivity("Expected TripPlannerBus Activity", TripPlannerBus.class);
		mCurrentActivity = solo.getCurrentActivity();	
		selectBronzeLine();
	}
	
	public void selectBronzeLine()
	{
		solo.pressSpinnerItem(0, -5); // (0,3) Corresponds to "Bronze" in the Line Spinner
		assertTrue("Selected Bronze", solo.searchText("Bronze")); // make sure Bronze was selected
		selectSecondStart();
	}
	
	public void selectSecondStart()
	{
		solo.pressSpinnerItem(1, 2); // Corresponds to "Vine & Green (NW Corner)
		assertTrue("Selected Vine & Green", solo.searchText("Vine & Green"));
		selectSecondDestination();
	}
	
	public void selectSecondDestination()
	{
		solo.pressSpinnerItem(2, 7); // Corresponds to "Chemical & Life Sciences
		assertTrue("Selected Chemical & Life", solo.searchText("Chemical & Life"));
		setSecondAlarmOptions();
	}
	
	public void setSecondAlarmOptions()
	{
		solo.clickOnText("Alarm Options");
		solo.clickOnText("At Stop");
		solo.clickOnText("Ring");
		setSecondAlarm();
	}
	
	public void setSecondAlarm()
	{
		solo.clickOnText("Set Alarm");
		solo.assertCurrentActivity("Expected RouteMap Activity", RouteMap.class);
		mCurrentActivity = solo.getCurrentActivity();		
		this.getActivity().finish();
	}
}
