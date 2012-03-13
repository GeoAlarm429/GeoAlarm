package edu.illinois.geoalarm.test;

import edu.illinois.geoalarm.GeoAlarm;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.DigitalClock;
import com.jayway.android.robotium.solo.Solo;
import android.test.suitebuilder.annotation.Smoke;

public class GeoAlarmTest extends ActivityInstrumentationTestCase2<GeoAlarm> 
{
	 private GeoAlarm mActivity;
	 private DigitalClock mClock;
	 private Button mapButton;
	 private Button tripButton;
	 private Button optionsButton;
	 private Solo solo;
	 
	public GeoAlarmTest() 
	{
		super("edu.illinois.geoalarm", GeoAlarm.class);
	}
	
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		mActivity = this.getActivity();
		mClock = (DigitalClock) mActivity.findViewById(edu.illinois.geoalarm.R.id.digitalClock1);
		mapButton = (Button) mActivity.findViewById(edu.illinois.geoalarm.R.id.mapButton);
		tripButton = (Button) mActivity.findViewById(edu.illinois.geoalarm.R.id.tripButton);
		optionsButton = (Button) mActivity.findViewById(edu.illinois.geoalarm.R.id.optionsButton);
		solo = new Solo(getInstrumentation(), getActivity());
	}
	 
	 public void testPreconditions() 
	 {
	      assertNotNull(mClock);
	      assertNotNull(mapButton);
	      assertNotNull(tripButton);
	      assertNotNull(optionsButton);
	 }

	 public void testClock() 
	 {
		 String currentTime = mClock.getText().toString();
	     assertNotNull(currentTime);
	 }
	 
	 public void testMapsButton()
	 {
		 String mapButtonDisplayString = mapButton.getText().toString();
		 assertEquals("Map", mapButtonDisplayString);
	 }
	 
	 public void testTripButton()
	 {
		 String tripButtonDisplayString = tripButton.getText().toString();
		 assertEquals("Plan Trip", tripButtonDisplayString);
	 }
	 
	 public void testOptionsButton()
	 {
		 String optionsButtonDisplayString = optionsButton.getText().toString();
		 assertEquals("Options", optionsButtonDisplayString);
	 }
	 
	 @Smoke
	 public void testMapsTransition()
	 {
		 solo.assertCurrentActivity("Expected GeoAlarm activity", "GeoAlarm"); 
		 solo.clickOnButton("Map");
		 solo.assertCurrentActivity("Expected RouteMap activity", "RouteMap"); 
		 solo.goBack();
		 solo.assertCurrentActivity("Expected GeoAlarm activity", "GeoAlarm");
	 }
	 
	 @Smoke
	 public void testPlanTripTransition()
	 {
		 solo.assertCurrentActivity("Expected GeoAlarm activity", "GeoAlarm"); 
		 solo.clickOnButton("Plan Trip");
		 solo.assertCurrentActivity("Expected TripPlannerBus activity", "TripPlannerBus"); 
		 solo.goBack();
		 solo.assertCurrentActivity("Expected GeoAlarm activity", "GeoAlarm");		 
	 }
	 
	 @Smoke
	 public void testOptionsTransition()
	 {
		 solo.assertCurrentActivity("Expected GeoAlarm activity", "GeoAlarm");
		 solo.clickOnButton("Options");
		 solo.assertCurrentActivity("Expected Options activity", "Options"); 
		 solo.goBack();
		 solo.assertCurrentActivity("Expected GeoAlarm activity", "GeoAlarm");
	 }
}
