package edu.illinois.geoalarm.test;

import edu.illinois.geoalarm.*;
import com.jayway.android.robotium.solo.Solo;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;

public class GeoAlarmUsability extends ActivityInstrumentationTestCase2<GeoAlarm>{

	private Solo solo;

	public GeoAlarmUsability() {
		super("edu.illinois.geoalarm", GeoAlarm.class);

	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	
	@Smoke
	public void testPlanTrip1Usability() throws Exception {
		solo.clickOnText("Plan Trip");
		solo.assertCurrentActivity("Expected TripPlannerBus activity", "TripPlannerBus"); 

	}

	@Smoke
	public void testPlanTrip2Usability() throws Exception {
		solo.clickOnText("Plan Trip");
		solo.clickOnButton("Alarm Options");
		solo.clickOnText("At Stop");
		
		boolean expected = true;
		boolean actual = solo.searchText("Ring");

		assertEquals("Correct menu opened", expected, actual);

	}

	@Smoke
	public void testTimeUsability() throws Exception {
		solo.clickOnText("Plan Trip");
		solo.clickOnButton("Alarm Options");
		solo.clickOnText("At Time");

		solo.setTimePicker(0, 7, 33);
		boolean expected1 = true;
		boolean actual1 = solo.searchText("7:33 AM");
		assertEquals("Correct time set", expected1, actual1);

		solo.clickOnButton("Set");

		boolean expected2 = true;
		boolean actual2 = solo.searchText("Pick a notification");
		assertEquals(expected2, actual2);

		solo.clickOnText("Vibrate");
		solo.assertCurrentActivity("Expected TripPlannerBus activity", "TripPlannerBus"); 
	}

	
	@Override
	public void tearDown() throws Exception {
		//Robotium will finish all the activities that have been opened
		solo.finishOpenedActivities();
	}
}
