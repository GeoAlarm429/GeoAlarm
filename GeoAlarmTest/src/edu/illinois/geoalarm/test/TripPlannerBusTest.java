package edu.illinois.geoalarm.test;

import edu.illinois.geoalarm.TripPlannerBus;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Spinner;

public class TripPlannerBusTest extends ActivityInstrumentationTestCase2<TripPlannerBus> 
{
	Activity mActivity;
	Spinner startingSpinner;
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
	}
	
	public void testPreconditions() 
	{
		assertNotNull(startingSpinner);
	}
	
	public void testLoadNames()
	{
		String firstStopName = startingSpinner.getItemAtPosition(0).toString();
		assertNotNull(firstStopName);
	}

}
