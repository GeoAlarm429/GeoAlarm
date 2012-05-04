package edu.illinois.geoalarm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import android.os.Process;

/**
 * A MapActivity class that will be responsible for displaying the transit map.
 * This class implements the Android MapActivity class, which allows us to display a map.
 * The class is also responsible for launching the AlarmService when a trip is planned, and for
 * sounding the alarm when the user arrives at their destination.
 * @author GeoAlarm
 */
public class RouteMap extends MapActivity 
{
	private static final int INITIAL_ZOOM = 17;
	protected static final int LAUNCH_ACTIVITY = 1;

	private MapView mainMap;
	private MapController mapControl;
	private CheckBox satellite;
	private Location currentLocation;
	private GeoPoint currentLocationPoint;
	private List<Overlay> mapOverlays;
	private NearStopOverlay nearOverlay;
	private ArrayList<StopInfo> nearStops;
	private Location mapCenter;
	private GeoAlarmDB database;
	private GeoPoint src;
	private GeoPoint dest;
	private int startingLatitude;
	private int startingLongitude;
	private int destinationLatitude;
	private int destinationLongitude;
	private LocationManager locationManager;	
	private AsyncPlayer player;
	private Vibrator vibrator;
	private TextView remainingTime;
	private TextView remainingDistance;
	NearStopOverlayItem startingLocationItem;
	NearStopOverlayItem destinationLocationItem;
	private Handler handler;
	
	
	/* Route data from TripPlannerBus or Map selection */
	private String selectedLine;
	private String selectedStartingStation;
	private String selectedDestinationStation;
	private String selectedNotification;
	private String selectedNotificationTime ;
	private int hourSet;
	private int minuteSet;
	private boolean isAM;
	
	public static final String RING_NOTIFICATION = "Ring";
	public static final String VIBRATE_NOTIFICATION = "Vibrate";
	public static final String POP_UP_NOTIFICATION = "PopUp Message";
	private int ringLength;
	private int vibrateLength;
	private LocationListener locationListener;
	private boolean gpsEnabled;
	private boolean networkEnabled;
	private long minTime = 3000;
	private float minDistance = 10;
	CurrMarkerOverlay itemizedOverlay;
	NearStopOverlay startDestOverlay;

	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.map);
        
        SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
        View v = findViewById(R.id.mainMap);
        View root = v.getRootView();
        root.setBackgroundResource(settings.getInt("color_value", Color.BLACK));
        ringLength = settings.getInt("ring_length", 3);
        vibrateLength = settings.getInt("vibrate_length", 3);
        
        ThreadPolicy tp = ThreadPolicy.LAX; 
        StrictMode.setThreadPolicy(tp);
        
        loadDatabase();
        		
        mainMap = (MapView)findViewById(R.id.mainMap);
        satellite = (CheckBox)findViewById(R.id.satellite);
        
        showCurrentLocation();        
        setupGoogleMap();        
        showNearBusStopsOnMap(currentLocation);  
        
        /* Grab data from TripPlannerBus activity, if launched from that activity */
        boolean fromTripPlanner = getIntent().getBooleanExtra("edu.illinois.geoalarm.isPlannedTrip", false);
        if(fromTripPlanner)
        {
        	initializeTripVariables();
        	updateCoordinates();        
        	if(isOnline())
        	{
        		drawPath(src, dest);
        		calcRemainingTimeAndDistance(src, dest);
        	}
            
            startAlarmService();
        }                       
        
        setSatelliteOnClickListener();       
        handler = new Handler();
    }

	@Override
	public void onPause()
	{
		super.onPause();
		updateUsageData();
		database.close();		
	}
	
	@Override
    public void onResume()
	{
		super.onResume();
		loadDatabase();
	}	
	
	/**
	 * Updates the stored usage data with the most up-to-date numbers
	 */
	public void updateUsageData()
	{
		if(database != null)
		{		
			long numBytesLastReceivedSession =  database.getBytes(GeoAlarmDB.DB_RX_SESSION);
			long numBytesLastTransmittedSession =  database.getBytes(GeoAlarmDB.DB_TX_SESSION);
			long numBytesReceived = database.getBytes(GeoAlarmDB.DB_RX);
			long numBytesTransmitted = database.getBytes(GeoAlarmDB.DB_TX);
			long numBytesReceivedDelta = TrafficStats.getUidRxBytes(Process.myUid()) - database.getBytes(GeoAlarmDB.DB_RX_TARE_SESSION) - numBytesLastReceivedSession;
			long numBytesTransmittedDelta = TrafficStats.getUidTxBytes(Process.myUid()) - database.getBytes(GeoAlarmDB.DB_TX_TARE_SESSION) - numBytesLastTransmittedSession;
		
			database.setBytes(GeoAlarmDB.DB_RX_SESSION, numBytesLastReceivedSession + numBytesReceivedDelta);
			database.setBytes(GeoAlarmDB.DB_TX_SESSION, numBytesLastTransmittedSession + numBytesTransmittedDelta);
			database.setBytes(GeoAlarmDB.DB_RX, numBytesReceived + numBytesReceivedDelta);
			database.setBytes(GeoAlarmDB.DB_TX, numBytesTransmitted + numBytesTransmittedDelta);			
		}
	}
	
	@Override
	public void onNewIntent(Intent newIntent)
	{
		Log.d("RouteMap", "Alarm Received");
		this.setIntent(newIntent);
		boolean alarmTime = getIntent().getBooleanExtra("edu.illinois.geoalarm.timedAlarmSignal", false);
		
	    if(alarmTime)
	    {
			launchAlarm();	
	    } 
	}
	
	/**
	 * Helper method for sounding the appropriate alarm
	 */
	private void launchAlarm()
	{
		Toast.makeText(this, "YOU HAVE ARRIVED", Toast.LENGTH_LONG).show();
		if(selectedNotification.equals(RING_NOTIFICATION))
		{
			soundRingtone();
		}
		else if(selectedNotification.equals(VIBRATE_NOTIFICATION))
		{
			vibratePhone();
		}	    
	}
	
	/**
	 * Plays the default ringtone for the number of seconds specified by the user in Options 
	 */
	private void soundRingtone()
	{
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		player = new AsyncPlayer("RingAlarm");
		player.play(getApplicationContext(), notification, false, AudioManager.STREAM_RING);
		TimerTask task = new TimerTask() 
		{
		    @Override
		    public void run() 
		    {
		    	player.stop();
		    }
		};
		Timer timer = new Timer();
		timer.schedule(task, ringLength * 1000);
	}
	
	/**
	 * Vibrates the phone for the number of seconds specified by the user in Options
	 */
	private void vibratePhone()
	{
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(500);
		TimerTask task = new TimerTask() 
		{
		    @Override
		    public void run() 
		    {
		    	vibrator.cancel();
		    }
		};
		Timer timer = new Timer();
		timer.schedule(task, vibrateLength * 1000);	    		
	}
	
	/**
	 * This method starts the AlarmService service that monitors the trip
	 */
	private void startAlarmService()
	{
		/* Start alarm service */   
        Intent serviceIntent = new Intent(RouteMap.this, AlarmService.class);
        serviceIntent.putExtra("edu.illinois.geoalarm.isPlannedTrip", true);
        serviceIntent.putExtra("edu.illinois.geoalarm.line", selectedLine);
        serviceIntent.putExtra("edu.illinois.geoalarm.startingStationLatitude", startingLatitude);
        serviceIntent.putExtra("edu.illinois.geoalarm.startingStationLongitude", startingLongitude);
        serviceIntent.putExtra("edu.illinois.geoalarm.destinationStationLatitude", destinationLatitude);
        serviceIntent.putExtra("edu.illinois.geoalarm.destinationStationLongitude", destinationLongitude);
        serviceIntent.putExtra("edu.illinois.geoalarm.selectedNotification", selectedNotification);
        serviceIntent.putExtra("edu.illinois.geoalarm.selectedNotificationTime", selectedNotificationTime);
        serviceIntent.putExtra("edu.illinois.geoalarm.selectedNotificationHour", hourSet);
        serviceIntent.putExtra("edu.illinois.geoalarm.selectedNotificationMinute", minuteSet);
        serviceIntent.putExtra("edu.illinois.geoalarm.selectedNotificationIsAM", isAM);      
        startService(serviceIntent);        
	}
	
	/**
	 * This method initializes the trip instance variables from the intent that started this activity
	 */
	private void initializeTripVariables()
	{
		selectedLine = getIntent().getStringExtra("edu.illinois.geoalarm.line");
    	selectedStartingStation = getIntent().getStringExtra("edu.illinois.geoalarm.startingStation");
    	selectedDestinationStation = getIntent().getStringExtra("edu.illinois.geoalarm.destinationStation");
    	selectedNotification = getIntent().getStringExtra("edu.illinois.geoalarm.selectedNotification");
    	selectedNotificationTime = getIntent().getStringExtra("edu.illinois.geoalarm.selectedNotificationTime");
    	hourSet = getIntent().getIntExtra("edu.illinois.geoalarm.selectedNotificationHour", 0);
    	minuteSet = getIntent().getIntExtra("edu.illinois.geoalarm.selectedNotificationMinute", 0);
    	isAM = getIntent().getBooleanExtra("edu.illinois.geoalarm.selectedNotificationIsAM", false);        	
	}
	
	/**
	 * This method sets up the event listener for the Satellite button.  This toggles
	 * between a street view and satellite image
	 */
	private void setSatelliteOnClickListener()
	{
			satellite.setOnClickListener(new OnClickListener() 
			{
			
			public void onClick(View v) {
				if(satellite.isChecked()){
					mainMap.setSatellite(true);
				}
				else
					mainMap.setSatellite(false);
			}
		});                   
	}
	
	/**
	 * This method update the coordinates of the start and destination stops to refer to those
	 * selected in the trip planner. 
	 */
	private void updateCoordinates()
	{
		startingLatitude = (int) (database.getLatitude(selectedStartingStation) * 1E6) ;
		startingLongitude = (int) (database.getLongitude(selectedStartingStation)* 1E6);
		destinationLatitude = (int) (database.getLatitude(selectedDestinationStation)* 1E6);
		destinationLongitude = (int) (database.getLongitude(selectedDestinationStation)* 1E6);
		src = new GeoPoint(startingLatitude, startingLongitude);
		dest = new GeoPoint(destinationLatitude, destinationLongitude);	
		showStartAndDestOnMap();
					
	}
	
	/**
	 * This method removes old start and destination data from the map, creates a new overlay to display this data,
	 * and adds this overlay to the main map
	 */
	private void showStartAndDestOnMap()
	{
		mapOverlays = mainMap.getOverlays();
		if(startDestOverlay != null)
		{
			mapOverlays.remove(startDestOverlay);
		}
						
		Drawable drawable = this.getResources().getDrawable(R.drawable.blue_arrow);     
		startDestOverlay = new NearStopOverlay(drawable, this, database);
		startingLocationItem = new NearStopOverlayItem(new StopInfo(selectedStartingStation, src.getLatitudeE6() / 1E6, src.getLongitudeE6() / 1E6));
		destinationLocationItem = new NearStopOverlayItem(new StopInfo(selectedDestinationStation, dest.getLatitudeE6() / 1E6, dest.getLongitudeE6() / 1E6));
		startDestOverlay.addOverlay(startingLocationItem);
		startDestOverlay.addOverlay(destinationLocationItem);		     
		mapOverlays.add(startDestOverlay);			
	}

	/**
	 * Setup the options for the main map, like the default zoom and first center point
	 */
	private void setupGoogleMap() 
	{
		if(mainMap == null)
		{
			Log.d("RouteMap", "Center point not set");
			return;
		}
		mapControl = mainMap.getController();
        mainMap.setBuiltInZoomControls(true);

        if(currentLocationPoint != null)
        {
        	mapControl.animateTo(currentLocationPoint);
        }
        mapControl.setZoom(INITIAL_ZOOM);
	}

	/**
	 * Method to show current location on the map
	 */
	private void showCurrentLocation() 
	{
		setCurrentPoint();		
		checkForProviders();
		setLocationListener();
		registerListeners();
		showMarkerOnMap();
	}
	
	/**
	 * Get current location GPS values from built-in location manager
	 */
	private void setCurrentPoint() 
	{
		if(locationManager == null)
		{
			locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		}

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);	
		
		String provider = locationManager.getBestProvider(criteria, true);
		if(provider != null)
		{		
			currentLocation = locationManager.getLastKnownLocation(provider);		
		}
		else
		{
			currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
	
		if(currentLocation != null)
		{
			double latitude = currentLocation.getLatitude();   
			double longitude = currentLocation.getLongitude();	  
			currentLocationPoint = new GeoPoint((int)(latitude*1E6), (int)(longitude*1E6));
		}		
	}

	/**
	 * Show current location on the map with a marker
	 */
	private void showMarkerOnMap() 
	{
		if(currentLocationPoint != null)
		{			
			mapOverlays = mainMap.getOverlays();
			if(itemizedOverlay != null)
			{
				mapOverlays.remove(itemizedOverlay);
			}
			Drawable drawable = this.getResources().getDrawable(R.drawable.current);     
			itemizedOverlay = new CurrMarkerOverlay(drawable, this);
			OverlayItem overlayitem = new OverlayItem(currentLocationPoint, "", "");
        
			itemizedOverlay.addOverlay(overlayitem);  
			mapOverlays.add(itemizedOverlay);			
		}
	}

    /**
     * Helper function to show the bus stops near the current location on the map.
     * This function will only draw overlays that don't hide the start and destination overlays
     * @param loc The location we want to display nearby stops for
     */
	private void showNearBusStopsOnMap(Location loc) 
	{
		mapOverlays = mainMap.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.near);
		
		nearStops = database.getAroundMe(loc);
		nearOverlay = new NearStopOverlay(drawable, this, database);
		
		if(!nearStops.isEmpty())
		{
			for(StopInfo stopToShow : nearStops)
			{				
				NearStopOverlayItem item = new NearStopOverlayItem(stopToShow);
				double latitude = item.getBusStop().getLatitude();
				double longitude = item.getBusStop().getLongitude();
				
				if(startingLocationItem != null && destinationLocationItem != null)
				{
					if(latitude != startingLocationItem.getBusStop().getLatitude() && longitude != startingLocationItem.getBusStop().getLongitude() &&
						latitude != destinationLocationItem.getBusStop().getLatitude() && longitude != destinationLocationItem.getBusStop().getLongitude())							
					{
							nearOverlay.addOverlay(item);
					}
				}		
				else
				{
					nearOverlay.addOverlay(item);
				}
			}
			
			mapOverlays.add(nearOverlay);
		}
		else 
		{
			Toast.makeText(RouteMap.this, "No nearby bus stop", Toast.LENGTH_SHORT).show();
		}		
	}
	   
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) 
	{
		boolean result = false;
		try	
		{
			result = super.dispatchTouchEvent(event);
			if (event.getAction() == MotionEvent.ACTION_UP)
			{
				GeoPoint center = mainMap.getMapCenter();
				mapCenter = new Location("");
				mapCenter.setLatitude((double)center.getLatitudeE6()/(double)1E6);
				mapCenter.setLongitude((double)center.getLongitudeE6()/(double)1E6);

				nearOverlay.getOverlays().clear();
				showNearBusStopsOnMap(mapCenter);
			}
		}
		catch(IndexOutOfBoundsException ex)
		{
			ex.printStackTrace();
		}		

		return result;
	}

	/**
	 * Draws the start to destination path on the map.  It queries Google for drawing information, then
	 * draws the path
	 * @param startPoint - The starting point of the route
	 * @param endPoint - The ending point of the route
	 */
	private void drawPath(GeoPoint startPoint, GeoPoint endPoint) 
	{ 
		if(startPoint == null || endPoint == null)
		{
			Log.d("RouteMap", "Source or Destination not set");
			return;
		}
		
		StringBuilder urlString = getURL(startPoint, endPoint, true);

		Document doc;
		HttpURLConnection urlConnection;
		
		try 
		{
			urlConnection = setupConnection(urlString);

			int responseCode = urlConnection.getResponseCode(); 
			if (responseCode == HttpURLConnection.HTTP_OK) 
			{ 
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
				DocumentBuilder db = dbf.newDocumentBuilder(); 
				doc = db.parse(urlConnection.getInputStream()); 

				if(doc.getElementsByTagName("GeometryCollection").getLength()>0) 
				{ 
					String path = doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild().getFirstChild().getNodeValue() ; 
					Log.d("xxx","path="+ path); 
					String [] pairs = path.split(" "); 
					String [] lngLat = pairs[0].split(",");

					GeoPoint startGP = new GeoPoint((int)(Double.parseDouble(lngLat[1])*1E6),(int)(Double.parseDouble(lngLat[0])*1E6));
					mainMap.getOverlays().add(new DirectionPathOverlay(startGP,startGP)); 

					GeoPoint gp1; 
					GeoPoint gp2 = startGP; 
					for(int i = 1; i < pairs.length; i++)
					{ 
						lngLat = pairs[i].split(","); 
						gp1 = gp2; 

						gp2 = new GeoPoint((int)(Double.parseDouble(lngLat[1])*1E6),(int)(Double.parseDouble(lngLat[0])*1E6)); 
						mainMap.getOverlays().add(new DirectionPathOverlay(gp1,gp2)); 
						Log.d("xxx","pair:" + pairs[i]); 
					}

					mainMap.getOverlays().add(new DirectionPathOverlay(endPoint,endPoint));

					mainMap.invalidate();
				}
			}
			else
				Toast.makeText(this, "Fail to draw path. Check internet connection.", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is a simple helper function to setup http connection
	 * @param urlString The URL we want to setup the connection to
	 * @return urlConnection The setup connection
	 * @throws MalformedURLException, IOException, ProtocolException
	 */
	private HttpURLConnection setupConnection(StringBuilder urlString) throws MalformedURLException, IOException, ProtocolException 
	{
		HttpURLConnection urlConnection = null; 
		URL url = null; 

		url = new URL(urlString.toString()); 
		urlConnection=(HttpURLConnection)url.openConnection(); 
		urlConnection.setRequestMethod("GET"); 
		urlConnection.setDoOutput(true); 
		urlConnection.setDoInput(true); 
		urlConnection.connect();
		
		return urlConnection;
	}

	/**
	 * Helper to build the URL string to send to Google for the query
	 * @param startPoint - The starting point of the path
	 * @param endPoint - The ending point of the path
	 * @param isKML - A flag indicating KML
	 * @return The URL string
	 */
	private StringBuilder getURL(GeoPoint startPoint, GeoPoint endPoint, boolean isKML) 
	{
		StringBuilder urlString = new StringBuilder(); 

		if(isKML)
		{
			urlString.append("http://maps.google.com/maps?f=d&hl=en"); 
			urlString.append("&saddr="); 
			urlString.append( Double.toString((double)startPoint.getLatitudeE6()/1.0E6)); 
			urlString.append(","); 
			urlString.append( Double.toString((double)startPoint.getLongitudeE6()/1.0E6)); 
			urlString.append("&daddr=");
			urlString.append( Double.toString((double)endPoint.getLatitudeE6()/1.0E6)); 
			urlString.append(","); 
			urlString.append( Double.toString((double)endPoint.getLongitudeE6()/1.0E6));
			urlString.append("&ie=UTF8&0&om=0&output=kml");
		}
		else 
		{
			urlString.append("http://maps.google.com/maps/api/directions/json?");
			urlString.append("origin="); 
			urlString.append( Double.toString((double)startPoint.getLatitudeE6()/1.0E6)); 
			urlString.append(","); 
			urlString.append( Double.toString((double)startPoint.getLongitudeE6()/1.0E6));
			urlString.append("&destination=");
			urlString.append( Double.toString((double)endPoint.getLatitudeE6()/1.0E6)); 
			urlString.append(","); 
			urlString.append( Double.toString((double)endPoint.getLongitudeE6()/1.0E6));
			urlString.append("&sensor=false");
		}
		
		return urlString;
	}	
	
	/**
	 * This method starts the Timetable activity
	 * @param view The clicked button
	 */
	public void showTimetable(View view)
	{
		Intent intent1 = new Intent(view.getContext(), Timetable.class);
		startActivityForResult(intent1, 0);		
	}
	
	/**
	 * This function returns the list of stops around the current location
	 * @return The list of stops
	 */
	public ArrayList<StopInfo> getNearStops()
	{
		return nearStops;
	}
	
	/**
	 * This function animates the map to the given latitude and longitude,
	 * then draws the bus stops near that location
	 * @param latitude
	 * @param longitude
	 */
	public void setMapCenter(int latitude, int longitude)
	{
		GeoPoint scrollPoint = new GeoPoint(latitude, longitude);
		mapControl.setCenter(scrollPoint);		
		Location location = new Location(LocationManager.GPS_PROVIDER);
		location.setLatitude(((double)latitude) / 1E6);
		location.setLongitude(((double)longitude) / 1E6);
		showNearBusStopsOnMap(location);	
		mainMap.postInvalidate();
	}
	
	/**
	 * This function sets remaining time and distance. It asks Google for this information, then displays it
	 * to the user
	 * @param startPoint - The starting point of the trip
	 * @param endPoint - The ending point of the tirip
	 */
	private void calcRemainingTimeAndDistance(GeoPoint startPoint, GeoPoint endPoint) 
	{
		remainingTime = (TextView)findViewById(R.id.remainingTime);
		remainingDistance = (TextView)findViewById(R.id.remainingDistance);
		
		StringBuilder urlString = getURL(startPoint, endPoint, false);
		
		HttpURLConnection urlConnection;
		try 
		{
			urlConnection = setupConnection(urlString);
			
			StringBuffer response = new StringBuffer();
			int responseCode = urlConnection.getResponseCode(); 
			if (responseCode == HttpURLConnection.HTTP_OK) 
			{				
				BufferedReader input = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()),8192); 
				String strLine = null; 
				
				while ((strLine = input.readLine()) != null)
				{ 
					response.append(strLine); 
				} 
				input.close();
				
				String jsonOutput = response.toString();
	 
				JSONObject jsonObject = new JSONObject(jsonOutput); 
				JSONArray routesArray = jsonObject.getJSONArray("routes");  
				JSONObject route = routesArray.getJSONObject(0); 
				JSONArray legs = route.getJSONArray("legs"); 
				JSONObject leg = legs.getJSONObject(0); 
	
				JSONObject durationObject = leg.getJSONObject("duration"); 
				JSONObject distanceObject = leg.getJSONObject("distance"); 
				String duration = durationObject.getString("text");
				String distance = distanceObject.getString("text");
				
				remainingTime.setText(" " + duration);
				remainingDistance.setText(" " + distance);
			}
			else
				Toast.makeText(this, "Fail to load information. Check internet connection.", Toast.LENGTH_LONG).show();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}
	
	 /**
     * This function sets up a gps/network location event listener.  When location is updated, it calls the showMarkerOnMap
     * method to update the displayed user location on the map
     */
    private void setLocationListener()
    {
    	locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	locationListener = new LocationListener(){ 
            
            public void onLocationChanged(Location location) 
            {            	    	
            	currentLocation = location;  
            	double latitude = currentLocation.getLatitude();   
    			double longitude = currentLocation.getLongitude();	  
    			currentLocationPoint = new GeoPoint((int)(latitude*1E6), (int)(longitude*1E6));
    			showMarkerOnMap();   
    			mainMap.invalidate();    			
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
     * This method checks to see what location providers are currently enabled
     */
    protected void checkForProviders()
    {    	
    	gpsEnabled = locationManager.getProviders(true).contains(LocationManager.GPS_PROVIDER);              
        networkEnabled = locationManager.getProviders(true).contains(LocationManager.NETWORK_PROVIDER);        
    }
    
    /**
     * This method registers listeners for enabled location providers
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
	 * Checks whether we have a network connection
	 * @return true if connected, false otherwise
	 */
	public boolean isOnline() 
	{
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) 
	    {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * Helper function to load the database
	 */
	public void loadDatabase()
	{
		// Instantiate the database
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
	
	public void clickOnOverlay()
	{
		handler.post(new Runnable() {
		    public void run()
		    {
		    	GeoPoint p = nearOverlay.getItem(0).getPoint();
		    	nearOverlay.onTap(p, mainMap);
		    }
		});
	}
	
}