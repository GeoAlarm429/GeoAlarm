package edu.illinois.geoalarm.test;

import edu.illinois.geoalarm.TripPlanner;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.Spinner;
import android.widget.TextView;

public class TripPlannerTest extends ActivityInstrumentationTestCase2<TripPlanner> 
{
	TextView serviceTextView;
	Spinner serviceSpinner;
	Activity mActivity;
	
	public TripPlannerTest() 
	{
		super("edu.illinois.geoalarm", TripPlanner.class);
	}
	
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();		
		mActivity = this.getActivity();
		serviceTextView = (TextView) mActivity.findViewById(edu.illinois.geoalarm.R.id.serviceTextView);
		serviceSpinner = (Spinner) mActivity.findViewById(edu.illinois.geoalarm.R.id.tripServiceSelectSpinner);
	}
	
	 public void testPreconditions() 
	 {
		 assertNotNull(serviceTextView);
		 assertNotNull(serviceSpinner);
	 }
	 
	 public void testTextView()
	 {
		 String displayedText = serviceTextView.getText().toString();
		 assertEquals("Service:", displayedText);
	 }
	 
	 public void testSpinner()
	 {		 
		 String selectedText = serviceSpinner.getItemAtPosition(0).toString();
		 String busText = serviceSpinner.getItemAtPosition(1).toString();
		 String trainText = serviceSpinner.getItemAtPosition(2).toString();
		 assertEquals("Select", selectedText);
		 assertEquals("Bus", busText);
		 assertEquals("Train", trainText);
	 }
	 

}
