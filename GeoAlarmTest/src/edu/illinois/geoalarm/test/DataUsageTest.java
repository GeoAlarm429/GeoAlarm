package edu.illinois.geoalarm.test;

import android.app.Activity;
import android.database.SQLException;
import android.test.ActivityInstrumentationTestCase2;
import edu.illinois.geoalarm.*;

import com.jayway.android.robotium.solo.Solo;
import android.test.suitebuilder.annotation.Smoke;

public class DataUsageTest extends ActivityInstrumentationTestCase2<GeoAlarm>
{
	Activity mActivity;
	Activity mCurrentActivity;
	Solo solo;
	GeoAlarmDB database;
	int latitude = -10238511;
	int longitude = 40104150;		
	
	public DataUsageTest()
	{
		super("edu.illinois.geoalarm", GeoAlarm.class);		
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		mActivity = this.getActivity();
		solo = new Solo(getInstrumentation(), getActivity());
		database = new GeoAlarmDB(mActivity);
       	try 
       	{
    	   	database.openDataBase();
       	} 
       	catch (SQLException e) 
       	{
    	   	e.printStackTrace();
    	   	throw e;
       	}
       	
       	database.setBytes(GeoAlarmDB.DB_TX_SESSION, 0);
		database.setBytes(GeoAlarmDB.DB_RX_SESSION, 0);
		database.setBytes(GeoAlarmDB.DB_TX, 0);
		database.setBytes(GeoAlarmDB.DB_RX, 0);
		database.setBytes(GeoAlarmDB.DB_RX_TARE_SESSION, 0);
		database.setBytes(GeoAlarmDB.DB_TX_TARE_SESSION, 0);
		
		solo.sendKey(Solo.MENU);
		solo.clickOnText("Options");
		solo.assertCurrentActivity("Expected Options Activity", Options.class);	
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		solo.goBack();
		database.close();
		mActivity.finish();
		super.tearDown();
	}
	
	@Smoke
	public void testTXSessionDataUsage() throws InterruptedException
	{				
		String sessionTX = solo.getText(12).getText().toString(); // Corresponds to sessionTX
		assertEquals(" 0 MB", sessionTX);
		long sessionTXbytes = database.getBytes(GeoAlarmDB.DB_TX_SESSION);
		assertEquals(sessionTXbytes, 0);		
				
		// Transition to Map activity to transfer some data, the transition back
		solo.goBack();		
		solo.clickOnImage(1); // Corresponds to map button
		
		// Animate the map to cause data transfer
		Thread.sleep(1000);
		((RouteMap)solo.getCurrentActivity()).setMapCenter(latitude, longitude);
		latitude -= 1000;
		longitude-= 1000;
		Thread.sleep(1000);
		
		//Transition to options activity
		solo.goBack();
		solo.sendKey(Solo.MENU);
		solo.clickOnText("Options");
		
		sessionTX = solo.getText(12).getText().toString(); // Corresponds to sessionTX
		assertFalse(sessionTX.equals(" 0 MB"));
		sessionTXbytes = database.getBytes(GeoAlarmDB.DB_TX_SESSION);
		assertTrue(sessionTXbytes > 0);		
	}
	
	@Smoke
	public void testRXSessionDataUsage() throws InterruptedException
	{
		String sessionRX = solo.getText(14).getText().toString(); // Corresponds to sessionRX
		assertEquals(" 0 MB", sessionRX);
		long sessionRXbytes = database.getBytes(GeoAlarmDB.DB_RX_SESSION);
		assertEquals(0, sessionRXbytes);	
		
		// Transition to Map activity to transfer some data, the transition back
		solo.goBack();
		solo.clickOnImage(1); // Corresponds to map button
		
		// Animate the map to cause data transfer
		Thread.sleep(1000);
		((RouteMap)solo.getCurrentActivity()).setMapCenter(latitude, longitude);
		latitude -= 1000;
		longitude-= 1000;
		Thread.sleep(1000);
		
		//Transition to options activity
		solo.goBack();
		solo.sendKey(Solo.MENU);
		solo.clickOnText("Options");
		
		sessionRX = solo.getText(14).getText().toString(); // Corresponds to sessionRX
		assertFalse(sessionRX.equals(" 0 MB"));
		sessionRXbytes = database.getBytes(GeoAlarmDB.DB_RX_SESSION);
		assertTrue(sessionRXbytes > 0);	
	}
		
	@Smoke
	public void testTotalTXDataUsage() throws InterruptedException
	{				
		String totalTX = solo.getText(16).getText().toString(); // Corresponds to totalTX
		assertEquals(" 0 MB", totalTX);
		long totalTXBytes = database.getBytes(GeoAlarmDB.DB_TX);
		assertEquals(0, totalTXBytes);
		
		// Transition to Map activity to transfer some data, the transition back
		solo.goBack();		
		solo.clickOnImage(1); // Corresponds to map button
		
		// Animate the map to cause data transfer
		Thread.sleep(1000);
		((RouteMap)solo.getCurrentActivity()).setMapCenter(latitude, longitude);
		latitude -= 1000;
		longitude-= 1000;
		Thread.sleep(1000);
		
		//Transition to options activity
		solo.goBack();
		solo.sendKey(Solo.MENU);
		solo.clickOnText("Options");		
		
		totalTX = solo.getText(16).getText().toString();
		assertFalse(totalTX.equals(" 0 MB"));
		totalTXBytes = database.getBytes(GeoAlarmDB.DB_TX);
		assertTrue(totalTXBytes > 0);
	}
	
	@Smoke
	public void testTotalRXDataUsage() throws InterruptedException
	{
		String totalRX = solo.getText(18).getText().toString(); // Corresponds to totalRX
		assertEquals(" 0 MB", totalRX);
		long totalRXBytes = database.getBytes(GeoAlarmDB.DB_RX);
		assertEquals(0, totalRXBytes);
		
		// Transition to Map activity
		solo.goBack();		
		solo.clickOnImage(1); // Corresponds to map button
		
		// Animate the map to cause data transfer
		Thread.sleep(1000);
		((RouteMap)solo.getCurrentActivity()).setMapCenter(latitude, longitude);
		latitude -= 1000;
		longitude-= 1000;
		Thread.sleep(1000);
		
		//Transition to options activity
		solo.goBack();
		solo.sendKey(Solo.MENU);
		solo.clickOnText("Options");
		
		totalRX = solo.getText(18).getText().toString();
		assertFalse(totalRX.equals(" 0 MB"));
		totalRXBytes = database.getBytes(GeoAlarmDB.DB_RX);
		assertTrue(totalRXBytes > 0);
	}
	

}
