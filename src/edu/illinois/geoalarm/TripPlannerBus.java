package edu.illinois.geoalarm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
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
	private Button alarmOptionsButton;
	private Button setAlarmButton;
	private GeoAlarmDB database;
	private String selectedLine;
	private String selectedStartingStation;
	private String selectionDestinationStation;
	private String selectedNotification;
	private String selectedNotificationTime;
	private boolean setTimeManual;
	private int hourSet;
	private int minuteSet;
	private boolean isAM;
	
	private final int ALARM_OPTIONS_ID = 0;
	private final int TIME_OPTIONS_ID = 1;
	private final int INPUT_TIME_ID = 2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_cta_bus);         
		initializeHandles();
		
		startingLocationSpinner.setEnabled(false);
		destinationLocationSpinner.setEnabled(false);
		setAlarmButton.setEnabled(false);
		setTimeManual = false;
        
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
		alarmOptionsButton = (Button) findViewById(R.id.alarmOptionsButton);
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
    	    		
    	    		if(destinationLocationSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION)
    	    		{
    	    			setAlarmButton.setEnabled(true);
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
    	    		selectionDestinationStation = destinationLocationSpinner.getSelectedItem().toString(); 	
    	    		
    	    		if(startingLocationSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION)
    	    		{
    	    			setAlarmButton.setEnabled(true);
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
		final CharSequence[] items = {"Ring", "Vibrate", "PopUp Message"};
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
		final CharSequence[] items = {"At Stop", "Station Before Stop", "At Time"};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("When do you want to be notified?");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int item) 
			{
				if(items[item].equals("At Time"))
				{
					selectedNotificationTime = "Manual";
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
		
	}
	
	
	
	
	
}
