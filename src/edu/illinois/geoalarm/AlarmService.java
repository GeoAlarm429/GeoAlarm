package edu.illinois.geoalarm;

import java.util.Calendar;

import android.app.PendingIntent;
import android.app.Service;
import android.app.AlarmManager;

import android.content.Context;
import android.content.Intent;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * This Service is launched when a trip is planned.  It monitors the current time/location, and then
 * it signals the RouteMap service to sound the alarm.
 * @author GeoAlarm
 *
 */

public class AlarmService extends Service
{        
	/* Constants */
	private final long minTime = 3000; // milliseconds
	private final long minDistance = 10; // meters
	 private final IBinder serviceBinder = new AlarmServiceBinder();
	
	/* Instance Variables */
    private LocationManager locationManager;   
    private AlarmManager alarmManager;
    private LocationListener locationListener; 
    private Location startingLocation;
    private Location destinationLocation;
    private Location currentLocation;
    private boolean gpsEnabled;
    private boolean networkEnabled;   
    
    private int startingLatitude;
	private int startingLongitude;
	private int destinationLatitude;
	private int destinationLongitude;
	private String selectedNotificationTime ;
	private int hourSet;
	private int minuteSet;
	
	
	private static Service mInstance;
        
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) 
    {  
    	startingLatitude = intent.getIntExtra("edu.illinois.geoalarm.startingStationLatitude", 0);
    	startingLongitude = intent.getIntExtra("edu.illinois.geoalarm.startingStationLongitude", 0);
    	destinationLatitude = intent.getIntExtra("edu.illinois.geoalarm.destinationStationLatitude", 0);
    	destinationLongitude = intent.getIntExtra("edu.illinois.geoalarm.destinationStationLongitude", 0);
    	selectedNotificationTime = intent.getStringExtra("edu.illinois.geoalarm.selectedNotificationTime") ;
    	hourSet = intent.getIntExtra("edu.illinois.geoalarm.selectedNotificationHour", 0);
    	minuteSet = intent.getIntExtra("edu.illinois.geoalarm.selectedNotificationMinute", 0); 	
    	
    	startingLocation = new Location(LocationManager.GPS_PROVIDER);
    	startingLocation.setLatitude(((double)startingLatitude) / 1E6);
    	startingLocation.setLongitude(((double)startingLongitude) / 1E6);
    	destinationLocation = new Location(LocationManager.GPS_PROVIDER);
    	destinationLocation.setLatitude(((double)destinationLatitude) / 1E6);
    	destinationLocation.setLongitude(((double)destinationLongitude) / 1E6);
    	
    	Log.d("AlarmService", "Service Started");
    	
    	if(!selectedNotificationTime.equals("At Time"))
    	{
    		setLocationListener();
    		checkForProviders();
    		registerListeners();  
    	}
    	else
    	{
    		setAlarmListener();
    	}
    	
        return START_STICKY;
    }
    
    @Override
    public void onCreate()
    {
    	super.onCreate();
    	Log.d("AlarmService", "Alarm Service Started");    	
    	mInstance = this;	
    	
    }
    
    /**
     * This function sets up a gps/network location event listener.  When location is updated, it checks to see
     * if we have reached the destination
     */
    private void setLocationListener()
    {
    	locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	locationListener = new LocationListener(){ 
            
            public void onLocationChanged(Location location) 
            {            	    	
            	currentLocation = location;
            	checkIfAtDestination();
            }        
            public void onProviderDisabled(String provider) 
            {
            	
            }
            public void onProviderEnabled(String provider) 
            {
            	
            }        
            public void onStatusChanged(String provider, int status, Bundle extras) 
            {
            	
            }
    		
        };        
        
        locationManager.removeUpdates(locationListener);       
    }
    
    /**
     * This method sets a system alarm that launches the RouteMap intent when we have arrived
     */
    private void setAlarmListener()
    {
    	alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    	Intent intent = new Intent(this.getApplicationContext(), RouteMap.class);
    	intent.putExtra("edu.illinois.geoalarm.timedAlarmSignal", true);
    	intent.putExtra("edu.illinois.geoalarm.isPlannedTrip", false);
    	PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 111, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	Calendar c = Calendar.getInstance();
    	c.set(Calendar.HOUR_OF_DAY, hourSet);
    	c.set(Calendar.MINUTE, minuteSet);  
    	
    	alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - 30000, pendingIntent);
    	stopSelf();
    }
    
    /**
     * This method checks to see if the current location is 10 meters or less from the destination
     * location
     */
    public void checkIfAtDestination()
    {
    	float distanceTo = currentLocation.distanceTo(destinationLocation);
    	
    	if(selectedNotificationTime.equals(TripPlanner.AT_STOP_CHOICE) && distanceTo < 50.0)
    	{
    		Intent wakeUpRouteMap = new Intent(getBaseContext(), RouteMap.class);
    		wakeUpRouteMap.putExtra("edu.illinois.geoalarm.timedAlarmSignal", true);
    		wakeUpRouteMap.putExtra("edu.illinois.geoalarm.isPlannedTrip", false);
    		wakeUpRouteMap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		startActivity(wakeUpRouteMap);
    		stopSelf();
    	}
    	else if(selectedNotificationTime.equals(TripPlanner.STATION_BEFORE_STOP_CHOICE) && distanceTo < 150.0)
    	{
    		Intent wakeUpRouteMap = new Intent(getBaseContext(), RouteMap.class);
    		wakeUpRouteMap.putExtra("edu.illinois.geoalarm.timedAlarmSignal", true);
    		wakeUpRouteMap.putExtra("edu.illinois.geoalarm.isPlannedTrip", false);
    		wakeUpRouteMap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		startActivity(wakeUpRouteMap);
    		stopSelf();    		
    	}    	
    }
    
    /**
     * This method returns the static instance of this service
     * @return
     */
    public static Service getInstance()
    {
    	return mInstance;
    }
    
    /**
     * This method checks to see what location providers are currently enabled
     */
    protected void checkForProviders()
    {    	
    	gpsEnabled = locationManager.getProviders(true).contains(LocationManager.GPS_PROVIDER);              
        networkEnabled = locationManager.getProviders(true).contains(LocationManager.NETWORK_PROVIDER);        
    }
    
    /**
     * This method registers
     */
    protected void registerListeners()
    {
        if(gpsEnabled==true)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
        }
        if(networkEnabled==true)
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
        }        
       
    }   

    /**
     * This method is called when the service is bound to an Activity.
     * In this case, we return the serviceBinder to the Activity so
     * that it can interact with the service.
     */
	@Override
	public IBinder onBind(Intent intent) 
	{		
		return serviceBinder;
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(locationManager != null && locationListener != null)
		{
			locationManager.removeUpdates(locationListener);	
		}		
	}
	
	/**
	 * This inner class extends the Binder class, and allows us to return a Service
	 * binder to an Activity.
	 * @author deflume1
	 *
	 */
	public class AlarmServiceBinder extends Binder
	{
		AlarmService getService()
		{
			return AlarmService.this;
		}
	}
} 
