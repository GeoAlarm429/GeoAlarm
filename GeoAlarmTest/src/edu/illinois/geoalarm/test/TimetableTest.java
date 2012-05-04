package edu.illinois.geoalarm.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import edu.illinois.geoalarm.*;

import com.jayway.android.robotium.solo.Solo;
import android.test.suitebuilder.annotation.Smoke;

/**
 * Tests that the application time table works properly
 * @author GeoAlarm
 *
 */

public class TimetableTest extends ActivityInstrumentationTestCase2<Timetable> 
{
	Activity mActivity;
	Activity mCurrentActivity;
	Solo solo;
	
	public TimetableTest()
	{
		super("edu.illinois.geoalarm", Timetable.class);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		mActivity = this.getActivity();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		mActivity.finish();
		super.tearDown();
	}
	
	/**
	 * Tests selecting a line
	 * @throws InterruptedException
	 */
	@Smoke
	public void testSelectLine() throws InterruptedException
	{
		solo.pressSpinnerItem(0, 2); // Corresponds to "Blue" line
		assertTrue(solo.searchText("Blue"));  // Make sure Blue displays
		Thread.sleep(1000);
		assertTrue(solo.isSpinnerTextSelected(1, "State & Ells (NW Corner)")); // This is the first Blue stop
	}
	
	/**
	 * Tests selecting a line and a stop
	 * @throws InterruptedException
	 */
	@Smoke
	public void testSelectLineAndStop() throws InterruptedException
	{
		solo.pressSpinnerItem(0, 8); // Corresponds to  "Gold" line
		solo.pressSpinnerItem(1, 10); // Corresponds to "First & Gregory (NE Corner)
		Thread.sleep(1000);
		// Make sure correct lines were picked
		assertTrue(solo.isSpinnerTextSelected(0, "Gold"));
		assertTrue(solo.isSpinnerTextSelected(1,"First & Gregory (NE Corner)"));		
	}
	
	/**
	 * Tests that correct times display for a line and stop
	 * @throws InterruptedException
	 */
	@Smoke
	public void testCorrectTimesDisplay() throws InterruptedException
	{
		solo.pressSpinnerItem(0, 8); // Corresponds to  "Gold" line
		solo.pressSpinnerItem(1, 10); // Corresponds to "First & Gregory (NE Corner)
		Thread.sleep(1000);
		// Make sure correct lines were picked
		assertTrue(solo.isSpinnerTextSelected(0, "Gold"));
		assertTrue(solo.isSpinnerTextSelected(1,"First & Gregory (NE Corner)"));
		
		// Make sure correct times display
		assertTrue(solo.searchText("6:49 AM"));
		assertTrue(solo.searchText("7:12 AM"));
		assertTrue(solo.searchText("9:31 AM"));
		assertTrue(solo.searchText("12:31 PM"));
		
	}
}
