package edu.illinois.geoalarm.test;

import edu.illinois.geoalarm.GeoAlarm;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;

import com.jayway.android.robotium.solo.Solo;
import android.test.suitebuilder.annotation.Smoke;

/**
 * Tests the functionality of the main menu
 * @author GeoAlarm
 *
 */

public class GeoAlarmTest extends ActivityInstrumentationTestCase2<GeoAlarm> 
{
	 private GeoAlarm mActivity;
	 private ImageView mapButton;
	 private ImageView tripButton;
	 private Solo solo;
	 
	public GeoAlarmTest() 
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
		mapButton = (ImageView) mActivity.findViewById(edu.illinois.geoalarm.R.id.mapButton);
		tripButton = (ImageView) mActivity.findViewById(edu.illinois.geoalarm.R.id.tripButton);
		solo = new Solo(getInstrumentation(), getActivity());
	}
	 
	/**
	 * Test that Views exist
	 */
	 public void testPreconditions() 
	 {
	      assertNotNull(mapButton);
	      assertNotNull(tripButton);
	 }
	 
	 /**
	  * Test map button display
	  */
	 public void testMapsButton()
	 {
		 assertTrue(solo.searchText("Map"));
	 }
	 
	 /**
	  * Test PlanTrip button display
	  */
	 public void testTripButton()
	 {
		 assertTrue(solo.searchText("Plan Trip"));
	 }
	 
	 /**
	  * Test a transition to the map and back
	  */
	 @Smoke
	 public void testMapsTransition()
	 {
		 solo.assertCurrentActivity("Expected GeoAlarm activity", "GeoAlarm"); 
		 solo.clickOnImage(1); // Corresponds to map button
		 solo.assertCurrentActivity("Expected RouteMap activity", "RouteMap"); 
		 solo.goBack();
		 solo.assertCurrentActivity("Expected GeoAlarm activity", "GeoAlarm");
	 }
	 
	 /**
	  * Test a transition to the trip planner and back
	  */
	 @Smoke
	 public void testPlanTripTransition()
	 {
		 solo.assertCurrentActivity("Expected GeoAlarm activity", "GeoAlarm"); 
		 solo.clickOnImage(0); // Corresponds to plan trip button
		 solo.assertCurrentActivity("Expected TripPlannerBus activity", "TripPlanner"); 
		 solo.goBack();
		 solo.goBack();
		 solo.assertCurrentActivity("Expected GeoAlarm activity", "GeoAlarm");		 
	 }
	 
	 /**
	  * Test a transition to the options and back
	  */
	 @Smoke
	 public void testOptionsTransition()
	 {
		 solo.assertCurrentActivity("Expected GeoAlarm activity", "GeoAlarm");
		 solo.sendKey(Solo.MENU);
		 solo.clickOnText("Options");
		 solo.assertCurrentActivity("Expected Options activity", "Options"); 
		 solo.goBack();
		 solo.assertCurrentActivity("Expected GeoAlarm activity", "GeoAlarm");
	 }
}
