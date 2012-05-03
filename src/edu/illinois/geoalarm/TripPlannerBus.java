package edu.illinois.geoalarm;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import edu.illinois.geoalarm.parser.XMLParser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.SQLException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;

/**
 * The TripPlannerBus activity handles planning a bus trip.
 * @author deflume1
 *
 */

public class TripPlannerBus extends Activity
{
	private AutoCompleteTextView lineSearchBar;
	private AutoCompleteTextView startingLocationSearchBar;
	private AutoCompleteTextView destinationLocationSearchBar;
	private Button setAlarmButton;
	private GeoAlarmDB database;
	private String selectedLine;
	private String selectedStartingStation;
	private String selectedDestinationStation;
	private String selectedNotification = POP_UP_NOTIFICATION;
	private String selectedNotificationTime = AT_STOP_CHOICE;
	private int hourSet = -1;
	private int minuteSet = -1;
	private int itineraryNum=0;
	
	public static final String AT_STOP_CHOICE = "At Stop";
	public static final String STATION_BEFORE_STOP_CHOICE = "Station Before Stop";
	public static final String AT_TIME_CHOICE = "At Time";
	
	public static final String RING_NOTIFICATION = "Ring";
	public static final String VIBRATE_NOTIFICATION = "Vibrate";
	public static final String POP_UP_NOTIFICATION = "PopUp Message";
	
	private static final int ITINERARY_OPTIONS_ID = 0;
	private static final int ALARM_OPTIONS_ID = 1;
	private static final int TIME_OPTIONS_ID = 2;
	private static final int INPUT_TIME_ID = 3;
	private static final int LEG_ID = 4;

	XMLParser parser;

	private int buttonVoice = 0;

	ArrayList<String> matches;
		
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_cta_bus);   
        
        SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
        View v = findViewById(R.id.startingLocationSearchBar);
        View root = v.getRootView();
        root.setBackgroundColor(settings.getInt("color_value", Color.BLACK));
        
		initializeHandles();
		loadDatabase();

		populateLineSpinner();

		ImageButton speakButton1 = (ImageButton) findViewById(R.id.voice1);
		ImageButton speakButton2 = (ImageButton) findViewById(R.id.voice2);
		ImageButton speakButton3 = (ImageButton) findViewById(R.id.voice3);

		PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
 
        if (activities.size() == 0)
        {
            speakButton1.setEnabled(false);
            speakButton2.setEnabled(false);
            speakButton3.setEnabled(false);
        }
        
        parser = new XMLParser();
        database.close();
    }	
    
    @Override
    public void onPause()
    {
    	database.close();
    	super.onPause();    	
    }
    
    @Override
    public void onResume()
    {
    	loadDatabase();
    	super.onResume();
    }
    
    /**
     * This method uses findViewById to initialized the object handles from the View elements
     */
    public void initializeHandles()
    {
		setAlarmButton = (Button) findViewById(R.id.setAlarmButton);
		setAlarmButton.setEnabled(false);
		lineSearchBar = (AutoCompleteTextView)findViewById(R.id.lineSearchBar);		
		startingLocationSearchBar = (AutoCompleteTextView)findViewById(R.id.startingLocationSearchBar);	
		startingLocationSearchBar.setEnabled(false);
		destinationLocationSearchBar = (AutoCompleteTextView)findViewById(R.id.destinationLocationSearchBar);
		destinationLocationSearchBar.setEnabled(false);
    }
	
	/**
	 * This function tries to load the existing SQLite DB
	 */
	public void loadDatabase()
	{
		database = new GeoAlarmDB(this.getApplicationContext());
		
		// Check the custom SQLite helper functions that load existing DB
		try
		{
			database.createDataBase();
		}
		catch (IOException e)
		{
			throw new Error("Unable to create/find database");
		}

		// Open the SQLite database
		try
		{
			database.openDataBase();
		}
		catch (SQLException sql)
		{
			throw new Error("Unable to execute sql in: " + sql.toString());
		}	
	}
	
	/**
	 * This method populates the startingLocationSearchBar and destinationLocationSearchBar with data from the database
	 */
	public void populateStartingAndDestination()
	{		
		if(!database.geoAlarmDB.isOpen()) loadDatabase();
		List<String> locationList = database.getLineStops(selectedLine);							
		
		ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_dropdown_item_1line, locationList);
		startingLocationSearchBar.setAdapter(locationAdapter);
		startingLocationSearchBar.setOnEditorActionListener(new OnEditorActionListener() 
		{
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) 
			{
				List<String> locationList = database.getLineStops(selectedLine);
				String text = arg0.getText().toString();
				if(locationList.contains(text))
				{
					selectedStartingStation = text;
				}
				else
				{					
					selectedStartingStation = locationList.get(0);
					arg0.setText(locationList.get(0));
				}			
				
				if(!selectedStartingStation.equals(selectedDestinationStation))
				{
					setAlarmButton.setEnabled(true);
				}
				else
				{
					setAlarmButton.setEnabled(false);
				}
				
				return false;
			}			

		});	
		startingLocationSearchBar.setHint("Type start stop");
		
		destinationLocationSearchBar.setAdapter(locationAdapter);
		destinationLocationSearchBar.setOnEditorActionListener(new OnEditorActionListener() 
		{
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) 
			{
				List<String> locationList = database.getLineStops(selectedLine);
				String text = arg0.getText().toString();
				if(locationList.contains(text))
				{
					selectedDestinationStation = text;					
				}
				else
				{
					selectedDestinationStation = locationList.get(0);
					arg0.setText(locationList.get(0));
				}			
				
				if(!selectedDestinationStation.equals(selectedStartingStation))
				{
					setAlarmButton.setEnabled(true);
				}
				else
				{
					setAlarmButton.setEnabled(false);
				}

				return false;
			}			

		});	
		
		destinationLocationSearchBar.setHint("Type destination stop");
		
	}	
	
	/**
	 * This function populates the lineListSpinner.  It gets the list of lines from the database,
	 * and adds them to the Spinner.
	 * @throws Exception 
	 */
	public void populateLineSpinner()
	{
		if(!database.geoAlarmDB.isOpen()) loadDatabase();	
		ArrayList<String> linesList = database.getBusLines();		
		lineSearchBar = (AutoCompleteTextView)findViewById(R.id.lineSearchBar);		

		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_dropdown_item_1line, linesList);
		lineSearchBar.setAdapter(adapter1);
		lineSearchBar.setOnEditorActionListener(new OnEditorActionListener() {

			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) 
			{
				ArrayList<String> linesList = database.getBusLines();
				String text = arg0.getText().toString();
				if(linesList.contains(text))
				{
					selectedLine = text;
					populateStartingAndDestination();
				}
				else
				{				
					selectedLine = linesList.get(0);
					arg0.setText(linesList.get(0));
					populateStartingAndDestination();
				}
				
				startingLocationSearchBar.setEnabled(true);
				destinationLocationSearchBar.setEnabled(true);
								
				return false;
			}			
			
		});	

		// Set background message for the search bar
		lineSearchBar.setHint("Type line");
	}	
	
	/**
	 * This method is used to launch the alarm options dialog.  The method is bound to the button using
	 * the onClick XML attribute.
	 */
	public void configureAlarmOptions(View view)
	{
		this.showDialog(ALARM_OPTIONS_ID);		
		this.showDialog(TIME_OPTIONS_ID);
		if(isOnline())
		{
			this.showDialog(ITINERARY_OPTIONS_ID);
		}
	}
	
	@Override
	public Dialog onCreateDialog(int dialogID)
	{
		Dialog dialog = null;
		
		switch(dialogID)
		{
		case ITINERARY_OPTIONS_ID:
			dialog = createItineraryOptionsDialog();
			break;
		case LEG_ID:
			dialog = createLegsDialog();
			break;
		case ALARM_OPTIONS_ID:
			dialog = createAlarmOptionsDialog();
			break;
		case TIME_OPTIONS_ID:
			dialog = createTimeOptionsDialog();
			break;
		case INPUT_TIME_ID:
			dialog = createTimeInputDialog();
			break;
		default:
			dialog = null;
		}
		
		
		return dialog;		
	}
	
	/**
	 * Checks whether we have a network connection
	 * @return true if connected, false otherwise
	 */
	public boolean isOnline() 
	{
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	private AlertDialog createItineraryOptionsDialog()
	{
		DecimalFormat df = new DecimalFormat("#.######");
		String startLat = "" + df.format(database.getLatitude(selectedStartingStation));
		String startLon = "" + df.format(database.getLongitude(selectedStartingStation));
		String endLat = "" + df.format(database.getLatitude(selectedDestinationStation));
		String endLon = "" + df.format(database.getLongitude(selectedDestinationStation));
			
		final CharSequence[] items = parser.getItineraryArray(startLat, startLon, endLat, endLon);
		//final CharSequence[] items = {"blah", "blah2"};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select itinerary");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int item) 
			{
				itineraryNum = item;
				showDialog(LEG_ID);
			}
		});		
		return builder.create();
	}
	
	private AlertDialog createLegsDialog()
	{
		DecimalFormat df = new DecimalFormat("#.######");
		String startLat = "" + df.format(database.getLatitude(selectedStartingStation));
		String startLon = "" + df.format(database.getLongitude(selectedStartingStation));
		String endLat = "" + df.format(database.getLatitude(selectedDestinationStation));
		String endLon = "" + df.format(database.getLongitude(selectedDestinationStation));
			
		final CharSequence[] items = parser.getLegArray(startLat, startLon, endLat, endLon, itineraryNum);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Selected itinerary");
			builder.setItems(items, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int item) 
			{
				showDialog(TIME_OPTIONS_ID);	
			}
		});		
		return builder.create();
	}
	
	/**
	 * This method creates a AlertDialog that is used to determine what kind of notification the user wants
	 * to receive
	 * @return An AlertDialog ready to be shown
	 */
	private AlertDialog createAlarmOptionsDialog()
	{
		final CharSequence[] items = {RING_NOTIFICATION, VIBRATE_NOTIFICATION, POP_UP_NOTIFICATION};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a notification");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int item) 
			{
				selectedNotification = items[item].toString();					
			}
		});		
		return builder.create();
	}
	
	/**
	 * This method creates an AlertDialog that is used to select what time the user wants to receive
	 * a notification
	 * @return An AlertDialog ready to be shown
	 */
	private AlertDialog createTimeOptionsDialog()
	{
		final CharSequence[] items = {AT_STOP_CHOICE, STATION_BEFORE_STOP_CHOICE, AT_TIME_CHOICE};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("When do you want to be notified?");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int item) 
			{
				if(items[item].equals(AT_TIME_CHOICE))
				{
					selectedNotificationTime = AT_TIME_CHOICE;
					showDialog(INPUT_TIME_ID);
				}
				else
				{
					selectedNotificationTime = items[item].toString();
				}
			}
		});		
		return builder.create();
	}
	
	/**
	 * This method creates a TimePickerDialog to enable the user to set the time for notification manually.
	 * @return A TimePickerDialog ready to be shown
	 */
	private Dialog createTimeInputDialog()
	{
		TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
			
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
			{
				hourSet = hourOfDay;
				minuteSet = minute;	
			}
		};
		
		Calendar calendar = GregorianCalendar.getInstance();
		TimePickerDialog dialog = new TimePickerDialog(this, listener, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false);
		return dialog;			
	}
	/**
	 * This method is used to launch the trip setting intent.  The method is bound to the button using the
	 * onClick XML attribute.
	 */
	public void setAlarm(View view)
	{			
		database.close();
		Intent intent1 = new Intent(view.getContext(), RouteMap.class);
		intent1.putExtra("edu.illinois.geoalarm.isPlannedTrip", true);
		intent1.putExtra("edu.illinois.geoalarm.line", selectedLine);
		intent1.putExtra("edu.illinois.geoalarm.startingStation", selectedStartingStation);
		intent1.putExtra("edu.illinois.geoalarm.destinationStation", selectedDestinationStation);
		intent1.putExtra("edu.illinois.geoalarm.selectedNotification", selectedNotification);
		intent1.putExtra("edu.illinois.geoalarm.selectedNotificationTime", selectedNotificationTime);
		intent1.putExtra("edu.illinois.geoalarm.selectedNotificationHour", hourSet);
		intent1.putExtra("edu.illinois.geoalarm.selectedNotificationMinute", minuteSet);
		startActivityForResult(intent1, 0);		
	}
	
	/**
	 * Returns a handle to the active GeoAlarmDB
	 * @return A handle to the active GeoAlarmDB
	 */
	public GeoAlarmDB getDatabase()
	{
		return database;
	}

    public void speakButtonClicked1(View v)
    {
        buttonVoice = 1;
        startVoiceRecognitionActivity();
    }    
	
	public void speakButtonClicked2(View v)
    {
		// TODO Auto-generated method stub
        buttonVoice = 2;
        startVoiceRecognitionActivity();
    }
    
    public void speakButtonClicked3(View v)
    {
        buttonVoice = 3;
        startVoiceRecognitionActivity();
    }
	
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "GeoAlarm");      
        database.close();
        startActivityForResult(intent, 1234);
    }
 
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {    	
    	loadDatabase();
        if (requestCode == 1234 && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);        
        
            if(matches == null) 
            {
            	database.close();
            	return;
            }

            if(matches.size() == 0) 
            {
            	database.close();
            	return;
            }

            if(buttonVoice == 1)
            {
            	ArrayList<String> linesList = database.getBusLines();

            	for(int i = linesList.size() - 1 ; i >= 0; i--)
            	{
            		for(int j = matches.size() - 1; j >= 0; j--)
            		{
            			if(linesList.get(i).toLowerCase().equals(matches.get(j).toLowerCase()))
            			{
            				selectedLine = linesList.get(i);
            				lineSearchBar.setText(selectedLine);
            				lineSearchBar.onEditorAction(EditorInfo.IME_ACTION_DONE);
            			}
            		}
            	}
            }

            if(buttonVoice == 2)
            {
            	ArrayList<String> linesList = database.getLineStops(selectedLine);

            	for(int i = linesList.size() - 1 ; i >= 0; i--)
            	{
            		for(int j = matches.size() - 1; j >= 0; j--)
            		{
            			if(matches.get(j).contains("and"))
            			{
            				matches.add(j, matches.get(j).replace("and", "&"));
            			}
            			if(linesList.get(i).toLowerCase().contains(matches.get(j).toLowerCase()))
            			{
            				selectedStartingStation = linesList.get(i);
            				startingLocationSearchBar.setText(selectedStartingStation);
            				startingLocationSearchBar.onEditorAction(EditorInfo.IME_ACTION_DONE);
            			}
            		}
            	}


            }

            if(buttonVoice == 3)
            {
            	ArrayList<String> linesList = database.getLineStops(selectedLine);

            	for(int i = linesList.size() - 1 ; i >= 0; i--)
            	{
            		for(int j = matches.size() - 1; j >= 0; j--)
            		{
            			if(matches.get(j).contains("and"))
            			{
            				matches.add(j, matches.get(j).replace("and", "&"));
            			}
            			if(linesList.get(i).toLowerCase().equals(matches.get(j).toLowerCase()))
            			{
            				selectedDestinationStation = linesList.get(i);
            				destinationLocationSearchBar.setText(selectedDestinationStation);
            				destinationLocationSearchBar.onEditorAction(EditorInfo.IME_ACTION_DONE);
            			}
            		}
            	}

            }
            database.close();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
	 * @return the lineSearchBar
	 */
	public AutoCompleteTextView getLineSearchBar() 
	{
		return lineSearchBar;
	}

	/**
	 * @return the startingLocationSearchBar
	 */
	public AutoCompleteTextView getStartingLocationSearchBar() 
	{
		return startingLocationSearchBar;
	}

	/**
	 * @return the destinationLocationSearchBar
	 */
	public AutoCompleteTextView getDestinationLocationSearchBar() 
	{
		return destinationLocationSearchBar;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) {
		case R.id.options:
			Intent intent = new Intent(TripPlannerBus.this, Options.class);
			startActivityForResult(intent, 0);
		}
		return true;
	}
}
