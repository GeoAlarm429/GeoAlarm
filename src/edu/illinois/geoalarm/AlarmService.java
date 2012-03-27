package edu.illinois.geoalarm;

import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;



public class AlarmService extends Service
{        
	/* Constants */
	private final long minTime = 3000; // milliseconds
	private final long minDistance = 10; // meters
	
	/* Instance Variables */
    private LocationManager locationManager;   
    private LocationListener locationListener; 
    private boolean gpsEnabled;
    private boolean networkEnabled;
    private final IBinder serviceBinder = new AlarmServiceBinder();
    
    private String selectedLine;
	private String selectedStartingStation;
	private String selectedDestinationStation;
	private String selectedNotification;
	private String selectedNotificationTime ;
	private int hourSet;
	private int minuteSet;
	private boolean isAM;
	
	private Intent mIntent;
	private static Service mInstance;
        
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) 
    {  
    	selectedLine = intent.getStringExtra("edu.illinois.geoalarm.line");
    	selectedStartingStation = intent.getStringExtra("edu.illinois.geoalarm.startingStation");
    	selectedDestinationStation = intent.getStringExtra("edu.illinois.geoalarm.destinationStation");
    	selectedNotification = intent.getStringExtra("edu.illinois.geoalarm.selectedNotification");
    	selectedNotificationTime = intent.getStringExtra("edu.illinois.geoalarm.selectedNotificationTime") ;
    	hourSet = intent.getIntExtra("edu.illinois.geoalarm.selectedNotificationHour", 0);
    	minuteSet = intent.getIntExtra("edu.illinois.geoalarm.selectedNotificationMinute", 0);
    	isAM = intent.getBooleanExtra("edu.illinois.geoalarm.selectedNotificationIsAM", false);       
        return START_STICKY;
    }
    
    @Override
    public void onCreate()
    {
    	super.onCreate();
    	locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	
    	locationListener = new LocationListener(){ 
            
            public void onLocationChanged(Location location) 
            {
                
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
        checkForProviders();
        registerListeners();
        mInstance = this;
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
	
	/**
	 * This inner class extends the Binder class, and allows us to return a Service
	 * binder to an Activity.
	 * @author Chris
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
