package edu.illinois.geoalarm.test;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import edu.illinois.geoalarm.*;

import com.jayway.android.robotium.solo.Solo;
import android.test.suitebuilder.annotation.Smoke;

public class OptionsTest extends ActivityInstrumentationTestCase2<Options>
{
	Activity mActivity;
	Activity mCurrentActivity;
	Solo solo;
	
	public OptionsTest()
	{
		super("edu.illinois.geoalarm", Options.class);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		mActivity = this.getActivity();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Smoke
	public void testSetRingtoneLength()
	{
		solo.clearEditText(0); // Corresponds to ringtone length edit text
		String text = solo.getEditText(0).getText().toString();
		assertTrue(text.equals(""));
		solo.enterText(solo.getEditText(0), "5");
		text = solo.getEditText(0).getText().toString();
		assertTrue(text.equals("5"));
		solo.clickOnText("Save");
		SharedPreferences settings = mActivity.getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);	
		int ringtoneLength = settings.getInt("ring_length", -1);
		assertEquals(ringtoneLength, 5);
	}
	
	@Smoke
	public void testSetVibrateLength()
	{
		solo.clearEditText(1); // Corresponds to vibrate length edit text
		String text = solo.getEditText(1).getText().toString();
		assertTrue(text.equals(""));
		solo.enterText(solo.getEditText(1), "5");
		text = solo.getEditText(1).getText().toString();
		assertTrue(text.equals("5"));
		solo.clickOnText("Save");
		SharedPreferences settings = mActivity.getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);	
		int vibrateLength = settings.getInt("vibrate_length", -1);
		assertEquals(vibrateLength, 5);
	}
	
	@Smoke
	public void testSetAppColor()
	{
		solo.pressSpinnerItem(0, 3); // Corresponds to red
		solo.waitForText("Red");
		SharedPreferences settings = mActivity.getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);	
		String colorName = settings.getString("color_name", null);
		assertEquals("Red", colorName);
		int color = settings.getInt("color_value", -1);
		assertEquals(color, Color.parseColor("red"));		
	}
	
	@Smoke
	public void testToggleSplashScreen()
	{
		solo.clickOnToggleButton("OFF");
		SharedPreferences settings = mActivity.getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);	
		boolean isOn = settings.getBoolean("splash_screen", false);
		assertEquals(isOn, true);
	}
	
	

}
