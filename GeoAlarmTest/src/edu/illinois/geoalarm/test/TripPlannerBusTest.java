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
		startingSpinner = (Spinner) mActivity.findViewById(edu.illinois.geoalarm.R.id.startingLocationSpinner);		
		lineSpinner = (Spinner) mActivity.findViewById(edu.illinois.geoalarm.R.id.lineSpinner);
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testPreconditions() 
	{
		assertNotNull(startingSpinner);
		assertNotNull(lineSpinner);
	}
	
	public void testLoadNames()
	{
		String firstStopName = startingSpinner.getItemAtPosition(0).toString();
		assertNotNull(firstStopName);
	}
	
	@Smoke
	public void testSelectLine()
	{
		solo.pressSpinnerItem(0, 1);
	}

	@Smoke
	public void testSelectStartingLocation()
	{
		solo.pressSpinnerItem(1, 1);
	}
	
	@Smoke
	public void testSelectDestinationLocation()
	{
		solo.pressSpinnerItem(2, 1);
	}
	
	@Smoke
	public void testAlarmOptionsOne()
	{
		solo.clickOnButton("Alarm Options");
		solo.clickOnText("At Stop");		
		solo.clickOnText("Ring");
	}
	
	@Smoke
	public void testAlarmOptionsTwo()
	{
		solo.clickOnButton("Alarm Options");
		solo.clickOnText("Station Before Stop");
		solo.clickOnText("Vibrate");
	}
	
	@Smoke
	public void testAlarmOptionsThree()
	{
		solo.clickOnButton("Alarm Options");
		solo.clickOnText("At Time");
		
		solo.setTimePicker(0, 7, 33);
		boolean expected1 = true;
		boolean actual1 = solo.searchText("7:33 AM");
		assertEquals("Correct time set", expected1, actual1);
		
		solo.clickOnButton("Set");		
		solo.clickOnText("PopUp Message");
	}
}
