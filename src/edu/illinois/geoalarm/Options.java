package edu.illinois.geoalarm;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ToggleButton;

/**
 * This Activity is used to set various user options
 * for the GeoAlarm app
 * @author deflume1
 *
 */

public class Options extends Activity 
{		
	GeoAlarmDB database;
	TextView sessionUsageTxView;
	TextView sessionUsageRxView;
	TextView totalUsageTxView;	 
	TextView totalUsageRxView;
	EditText ringLengthEdit;
	EditText vibrateLengthEdit;
	Spinner backgroundColorSelectSpinner;
	ToggleButton toggleSplashScreenButton;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.options);
        
        sessionUsageTxView = (TextView)findViewById(R.id.sessionDataTxShow);
        sessionUsageRxView = (TextView)findViewById(R.id.sessionDataRxShow);
        totalUsageTxView = (TextView)findViewById(R.id.totalDataTxShow);
        totalUsageRxView = (TextView)findViewById(R.id.totalDataRxShow);       
        backgroundColorSelectSpinner = (Spinner)findViewById(R.id.backgroundColorSelectSpinner);
        ringLengthEdit = (EditText)findViewById(R.id.ringtoneLengthEditText);
        vibrateLengthEdit = (EditText)findViewById(R.id.vibrationLengthEditText);
        toggleSplashScreenButton = (ToggleButton)findViewById(R.id.toggleSplashScreenButton);
        SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
        
        toggleSplashScreenButton.setChecked(settings.getBoolean("splash_screen", false));
        ringLengthEdit.setText(String.valueOf(settings.getInt("ring_length", 3)));
        vibrateLengthEdit.setText(String.valueOf(settings.getInt("vibrate_length", 3))); 
    }
    
    @Override 
    public void onStart()
    {
    	showUsageData();
    	populateBackgroundColorSelectSpinner();
    	setBackgroundColorSelectSpinnerEventListeners();
    	setToggleButtonEventListeners();
    	super.onStart();
    }
    
    @Override
    public void onResume()
    {
    	showUsageData();
    	super.onResume();
    }
    
    /**
     * This function updates the usage data labels when its launched.  It uses the data stored in the UsageTable of GeoAlarmDB
     */
   public void showUsageData()
   {
	   	database = new GeoAlarmDB(this);
       	try 
       	{
    	   	database.openDataBase();
       	} 
       	catch (SQLException e) 
       	{
    	   	e.printStackTrace();
    	   	throw e;
       	}
       
        long numBytesLastReceivedSession =  database.getBytes(GeoAlarmDB.DB_RX_SESSION);
		long numBytesLastTransmittedSession =  database.getBytes(GeoAlarmDB.DB_TX_SESSION);
		long numBytesReceived = database.getBytes(GeoAlarmDB.DB_RX);
		long numBytesTransmitted = database.getBytes(GeoAlarmDB.DB_TX);		
		database.close();
		
		double numMegaBytesReceivedSession = ((double) numBytesLastReceivedSession) / 1E6;		
		double numMegaBytesTransmittedSession = ((double) numBytesLastTransmittedSession) / 1E6;
		double numMegaBytesReceived = ((double) numBytesReceived) / 1E6;
		double numMegaBytesTransmitted = ((double) numBytesTransmitted) / 1E6;
		
		DecimalFormat df = new DecimalFormat("#.###");
		String displaySessionRx = " " + df.format(numMegaBytesReceivedSession) + " MB";
		String displaySessionTx = " " + df.format(numMegaBytesTransmittedSession) + " MB";
		String displayTotalRx = " " + df.format(numMegaBytesReceived) + " MB";
		String displayTotalTx = " " + df.format(numMegaBytesTransmitted) + " MB";
		
		sessionUsageRxView.setText(displaySessionRx);
		sessionUsageTxView.setText(displaySessionTx);		
		totalUsageRxView.setText(displayTotalRx);	   
		totalUsageTxView.setText(displayTotalTx);
   }
   
   /**
    * This function populates the color select spinner
    */
   public void populateBackgroundColorSelectSpinner()
   {	   
		String[] colorList = this.getResources().getStringArray(R.array.color_array);		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_spinner_item, colorList);		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		backgroundColorSelectSpinner.setAdapter(adapter);
   }
   
   /**
    * This function sets the event listener for the color select spinner, which sets the color and saves it when clicked
    */
   	public void setBackgroundColorSelectSpinnerEventListeners()
	{
		/* Set a new event listener for the Spinner item selection */
   		backgroundColorSelectSpinner.setOnItemSelectedListener(new OnItemSelectedListener() 
   		{    
   			/* Implement the onItemSelected method to handle item selections */
   			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
   			{   				
   				String selectedColor = (String) backgroundColorSelectSpinner.getSelectedItem();
   				
   				int color = Color.parseColor(selectedColor.toLowerCase());
   				SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
   				SharedPreferences.Editor editor = settings.edit();
   				editor.putInt("color_value", color);
   				editor.putString("color_name", selectedColor);   		
   				editor.apply();		   	
   				
   				View v = findViewById(R.id.optionsLinearLayout);
   				View root = v.getRootView();
   				root.setBackgroundColor(color);
   			}

   			/* We do nothing here.  May want to change behavior so the last selected item behavior is used */
   			public void onNothingSelected(AdapterView<?> parentView) 
   			{
   				// do nothing
   			}

   		});
	}
   	
   	/**
   	 * Method called when Save button is clicked, writes preferences
   	 */
   	public void saveButton(View view)
   	{
   		SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		int ringLength = 0;
		int vibrateLength = 0;
		
		try
		{
			ringLength = Integer.parseInt(ringLengthEdit.getText().toString());
			vibrateLength = Integer.parseInt(vibrateLengthEdit.getText().toString());
		}
		catch (NumberFormatException ex)
		{
			ex.printStackTrace();
			ringLength = 3;
			vibrateLength = 3;
		}
		
		if(ringLength < 0)
		{
			ringLength = 3;
		}
		
		if(vibrateLength < 0)
		{
			vibrateLength = 3;
		}
		
		editor.putInt("ring_length", ringLength);
		editor.putInt("vibrate_length", vibrateLength);
		editor.commit();
		
		ringLengthEdit.setText(String.valueOf(ringLength));
		vibrateLengthEdit.setText(String.valueOf(vibrateLength));
   	}
   	
   	/**
   	 * Method called to register event listener for toggle button
   	 */
   	public void setToggleButtonEventListeners()
   	{
   		toggleSplashScreenButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) 
			{
				SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("splash_screen", isChecked);
				editor.commit();
			}
   			
   		});
   	}

}
