package edu.illinois.geoalarm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * The TripPlannerBus activity handles planning a bus trip.
 * @author deflume1
 *
 */

public class TripPlannerBus extends Activity 
{
	private Spinner lineSpinner;
	private Spinner startingLocationSpinner;
	private Spinner destinationLocationSpinner;
	private Button setAlarmButton;
	private GeoAlarmDB database;
	private String selectedLine;
	private String selectedStartingStation;
	private String selectedDestinationStation;
	private String selectedNotification = POP_UP_NOTIFICATION;
	private String selectedNotificationTime = AT_STOP_CHOICE;
	private int hourSet = -1;
	private int minuteSet = -1;
	
	public static final String AT_STOP_CHOICE = "At Stop";
	public static final String STATION_BEFORE_STOP_CHOICE = "Station Before Stop";
	public static final String AT_TIME_CHOICE = "At Time";
	
	public static final String RING_NOTIFICATION = "Ring";
	public static final String VIBRATE_NOTIFICATION = "Vibrate";
	public static final String POP_UP_NOTIFICATION = "PopUp Message";
	
	private static final int ALARM_OPTIONS_ID = 0;
	private static  final int TIME_OPTIONS_ID = 1;
	private static final int INPUT_TIME_ID = 2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_cta_bus);         
		initializeHandles();
		
		startingLocationSpinner.setEnabled(false);
		destinationLocationSpinner.setEnabled(false);
		setAlarmButton.setEnabled(false);
        
        loadDatabase();        
		populateLineSpinner();		 
    }	
	
	/**
     * This method is called when the activity is going to become visible to the user.
     * We setup the selection event listener for the Spinner here.
     */
    @Override
    public void onStart()
    {   	
    	/* Call superclass constructor.  Required */
    	super.onStart();	
    	setStationSpinnerEventListeners();
    	setLineSpinnerEventListeners();
    }
    
    /**
     * This method uses findViewById to initialized the object handles from the View elements
     */
    public void initializeHandles()
    {
    	startingLocationSpinner = (Spinner) findViewById(R.id.startingLocationSpinner);  
		destinationLocationSpinner = (Spinner) findViewById(R.id.destinationSpinner);
		lineSpinner = (Spinner) findViewById(R.id.lineSpinner);		
		setAlarmButton = (Button) findViewById(R.id.setAlarmButton);
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
	 * This method populates the startingLocationSpinner and destinationSpinner with data from the database
	 */
	public void populateStartingAndDestination()
	{		
		List<String> locationList = database.getLineStops(selectedLine);														   
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_spinner_item, locationList);		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		startingLocationSpinner.setAdapter(adapter);
		destinationLocationSpinner.setAdapter(adapter);
		
		startingLocationSpinner.setEnabled(true);
		destinationLocationSpinner.setEnabled(true);
	}	
	
	/**
	 * This function populates the lineListSpinner.  It gets the list of lines from the database,
	 * and adds them to the Spinner.
	 * @throws Exception 
	 */
	public void populateLineSpinner()
	{
		lineSpinner = (Spinner)findViewById(R.id.lineSpinner);		
		ArrayList<String> linesList = database.getBusLines();
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_spinner_item, linesList);		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		lineSpinner.setAdapter(adapter);	
	}
	
	/**
	 * This method sets the click event listener for the lineSpinner selection action.
	 * When a line is selected, we retrieve the selected line, then we populate the starting
	 * and destination Spinners with the appropriate stops.
	 */
	public void setLineSpinnerEventListeners()
	{
		/* Set a new event listener for the Spinner item selection */
    	lineSpinner.setOnItemSelectedListener(new OnItemSelectedListener() 
    	{    
    		/* Implement the onItemSelected method to handle item selections */
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
    	    {
    	    	int selectionPosition = lineSpinner.getSelectedItemPosition();
    	    	if(selectionPosition != Spinner.INVALID_POSITION)
    	    	{
    	    		selectedLine = lineSpinner.getSelectedItem().toString();
    	    		populateStartingAndDestination();
    	    	}
    	    }

    	    /* We do nothing here.  May want to change behavior so the last selected item behavior is used */
    	    public void onNothingSelected(AdapterView<?> parentView) 
    	    {
    	        // do nothing
    	    }

    	});
	}
	
	/**
	 * This method set the click event listeners for the startingLocationSpinner and destinationSpinner
	 * spinners.  When a station/location is selected in one of the spinners, we check to see if the
	 * other spinner has been selected too.  If so, we enable the setAlarm button.
	 */
	public void setStationSpinnerEventListeners()
	{
		startingLocationSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			/* Implement the onItemSelected method to handle item selections */
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
    	    {    	    	
    	    	if(startingLocationSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION)
    	    	{
    	    		selectedStartingStation = startingLocationSpinner.getSelectedItem().toString(); 	
    	    		
    	    		if(destinationLocationSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION &&
    	    				destinationLocationSpinner.getSelectedItemPosition() != startingLocationSpinner.getSelectedItemPosition())
    	    		{
    	    			setAlarmButton.setEnabled(true);
    	    		}
    	    		else
    	    		{
    	    			setAlarmButton.setEnabled(false);
    	    		}
    	    	}
    	    	
    	    }

    	    /* We do nothing here.  May want to change behavior so the last selected item behavior is used */
    	    public void onNothingSelected(AdapterView<?> parentView) 
    	    {
    	        // do nothing
    	    }

		});
		
		destinationLocationSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			/* Implement the onItemSelected method to handle item selections */
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
    	    {    	    	
    	    	if(destinationLocationSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION)
    	    	{
    	    		selectedDestinationStation = destinationLocationSpinner.getSelectedItem().toString(); 	
    	    		
    	    		if(startingLocationSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION &&
    	    				destinationLocationSpinner.getSelectedItemPosition() != startingLocationSpinner.getSelectedItemPosition())
    	    		{
    	    			setAlarmButton.setEnabled(true);
    	    		}
    	    		else
    	    		{
    	    			setAlarmButton.setEnabled(false);
    	    		}
		
    	    	}    	    	
    	    }

    	    /* We do nothing here.  May want to change behavior so the last selected item behavior is used */
    	    public void onNothingSelected(AdapterView<?> parentView) 
    	    {
    	        // do nothing
    	    }

		});
	}
	
	/**
	 * This method is used to launch the alarm options dialog.  The method is bound to the button using
	 * the onClick XML attribute.
	 */
	public void configureAlarmOptions(View view)
	{
		this.showDialog(ALARM_OPTIONS_ID);		
		this.showDialog(TIME_OPTIONS_ID);
	}
	
	@Override
	public Dialog onCreateDialog(int dialogID)
	{
		Dialog dialog = null;
		
		switch(dialogID)
		{
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
		Intent intent = new Intent(view.getContext(), RouteMap.class);
		intent.putExtra("edu.illinois.geoalarm.isPlannedTrip", true);
		intent.putExtra("edu.illinois.geoalarm.line", selectedLine);
		intent.putExtra("edu.illinois.geoalarm.startingStation", selectedStartingStation);
		intent.putExtra("edu.illinois.geoalarm.destinationStation", selectedDestinationStation);
		intent.putExtra("edu.illinois.geoalarm.selectedNotification", selectedNotification);
		intent.putExtra("edu.illinois.geoalarm.selectedNotificationTime", selectedNotificationTime);
		intent.putExtra("edu.illinois.geoalarm.selectedNotificationHour", hourSet);
		intent.putExtra("edu.illinois.geoalarm.selectedNotificationMinute", minuteSet);
		startActivityForResult(intent, 0);		
	}
	
	/**
	 * Returns a handle to the active GeoAlarmDB
	 * @return A handle to the active GeoAlarmDB
	 */
	public GeoAlarmDB getDatabase()
	{
		return database;
	}
	
	
	
}
