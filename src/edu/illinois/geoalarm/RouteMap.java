package edu.illinois.geoalarm;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
import android.location.LocationManager;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
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
 */

public class RouteMap extends MapActivity 
{
	private static final int INITIAL_ZOOM = 15;
	protected static final int LAUNCH_ACTIVITY = 1;

	private MapView mainMap;
	private MapController mapControl;
	private CheckBox satellite;
	private Location currentLocation;
	private GeoPoint centerPoint;
	private List<Overlay> mapOverlays;
	private NearStopOverlay nearOverlay;
	private ArrayList<StopInfo> nearStops;
	private Location mapCenter;
	private GeoAlarmDB dbController;
	private GeoPoint src;
	private GeoPoint dest;
	private int startingLatitude;
	private int startingLongitude;
	private int destinationLatitude;
	private int destinationLongitude;
	private Intent alarmService;
	private LocationManager locationManager;
	private Uri notification;
	private AsyncPlayer player;
	private Vibrator vibrator;
	private TextView remainingTime;
	private TextView remainingDistance;
	private final Handler handler = new Handler();
	private TimerTask second;
	private Date currentDt; 
    private int currentHours; 
    private int currentMinutes;
	
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

	/** 
	 * Called when the activity is first created.
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.map);
        
        SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
        View v = findViewById(R.id.mainMap);
        View root = v.getRootView();
        root.setBackgroundColor(settings.getInt("color_value", Color.BLACK));
        ringLength = settings.getInt("ring_length", 3);
        vibrateLength = settings.getInt("vibrate_length", 3);
        
        ThreadPolicy tp = ThreadPolicy.LAX; 
        StrictMode.setThreadPolicy(tp);
        
        dbController = new GeoAlarmDB(this);
        try 
        {
        	dbController.openDataBase();
        } 
        catch (SQLException e) 
        {
        	e.printStackTrace();
        	throw e;
        }
        		
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
            drawPath(src, dest);
            calcRemainingTimeAndDistance();
            
            startAlarmService();
        }                       
        
        setSatelliteOnClickListener();              
    }

	@Override
	public void onPause()
	{
		super.onPause();
		updateUsageData();
	}
	
	/**
	 * Updates the stored usage data with the most up-to-date numbers
	 */
	public void updateUsageData()
	{
		if(dbController != null)
		{		
			long numBytesLastReceivedSession =  dbController.getBytes(GeoAlarmDB.DB_RX_SESSION);
			long numBytesLastTransmittedSession =  dbController.getBytes(GeoAlarmDB.DB_TX_SESSION);
			long numBytesReceived = dbController.getBytes(GeoAlarmDB.DB_RX);
			long numBytesTransmitted = dbController.getBytes(GeoAlarmDB.DB_TX);
			long numBytesReceivedDelta = TrafficStats.getUidRxBytes(Process.myUid()) - dbController.getBytes(GeoAlarmDB.DB_RX_TARE_SESSION) - numBytesLastReceivedSession;
			long numBytesTransmittedDelta = TrafficStats.getUidTxBytes(Process.myUid()) - dbController.getBytes(GeoAlarmDB.DB_TX_TARE_SESSION) - numBytesLastTransmittedSession;
		
			dbController.setBytes(GeoAlarmDB.DB_RX_SESSION, numBytesLastReceivedSession + numBytesReceivedDelta);
			dbController.setBytes(GeoAlarmDB.DB_TX_SESSION, numBytesLastTransmittedSession + numBytesTransmittedDelta);
			dbController.setBytes(GeoAlarmDB.DB_RX, numBytesReceived + numBytesReceivedDelta);
			dbController.setBytes(GeoAlarmDB.DB_TX, numBytesTransmitted + numBytesTransmittedDelta);			
		}
	}
	
	/**
	 * Called when the os destroys this process.
	 */
	@Override
    public void onDestroy()
	{
		super.onDestroy();
		updateUsageData();
		dbController.close();
	}
	
	@Override
	public void onNewIntent(Intent newIntent)
	{
		Log.d("RouteMap", "Alarm Received");
		this.setIntent(newIntent);
		boolean alarmTime = getIntent().getBooleanExtra("edu.illinois.geoalarm.timedAlarmSignal", false);
	    if(alarmTime)
	    {
				Toast.makeText(this, "YOU HAVE ARRIVED", Toast.LENGTH_LONG).show();
	    		if(selectedNotification.equals(RING_NOTIFICATION))
	    		{
	    			notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
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
	    		else if(selectedNotification.equals(VIBRATE_NOTIFICATION))
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
	    } 
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
        alarmService = serviceIntent;
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
	 * This method sets up the event listener for the Satellite button
	 */
	private void setSatelliteOnClickListener()
	{
			satellite.setOnClickListener(new OnClickListener() {
			
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
	 * This method update the coordinates of the start and destination
	 */
	private void updateCoordinates()
	{
		startingLatitude = (int) (dbController.getLatitude(selectedStartingStation) * 1E6) ;
		startingLongitude = (int) (dbController.getLongitude(selectedStartingStation)* 1E6);
		destinationLatitude = (int) (dbController.getLatitude(selectedDestinationStation)* 1E6);
		destinationLongitude = (int) (dbController.getLongitude(selectedDestinationStation)* 1E6);
		src = new GeoPoint(startingLatitude, startingLongitude);
		dest = new GeoPoint(destinationLatitude, destinationLongitude);
	}

	/**
	 * Setup Google Map's options
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

        if(centerPoint != null)
        {
        	mapControl.animateTo(centerPoint);
        }
        mapControl.setZoom(INITIAL_ZOOM);
	}

	/**
	 * Method to show current location on the map
	 */
	private void showCurrentLocation() {
		setCurrentPoint();		
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
		
		currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
	
		if(currentLocation != null)
		{
			double latitude = currentLocation.getLatitude();   
			double longitude = currentLocation.getLongitude();	  
			centerPoint = new GeoPoint((int)(latitude*1E6), (int)(longitude*1E6));
		}
	}

	/**
	 * Show current location on the map with a marker
	 */
	private void showMarkerOnMap() 
	{
		if(centerPoint != null)
		{
			mapOverlays = mainMap.getOverlays(); 
			Drawable drawable = this.getResources().getDrawable(R.drawable.current);        
	    
			CurrMarkerOverlay itemizedOverlay = new CurrMarkerOverlay(drawable, this);
			OverlayItem overlayitem = new OverlayItem(centerPoint, "", "");
        
			itemizedOverlay.addOverlay(overlayitem);  
			mapOverlays.add(itemizedOverlay);
		}
	}

    /**
     * Helper function to show the bus stops near the current location on the map
     * @param currentLocation
     */
	private void showNearBusStopsOnMap(Location currentLocation) {
		mapOverlays = mainMap.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.near);
		
		nearStops = dbController.getAroundMe(currentLocation);
		nearOverlay = new NearStopOverlay(drawable, this);
		
		if(!nearStops.isEmpty()){
			for(StopInfo stopToShow : nearStops){
				
				NearStopOverlayItem item = new NearStopOverlayItem(stopToShow);
				nearOverlay.addOverlay(item);
			}
			
			mapOverlays.add(nearOverlay);
		}
		else {
			Toast.makeText(RouteMap.this, "No near bus stop", Toast.LENGTH_SHORT).show();
			onResume();
		}
	}
	
    /**
     * Called when the user move the center of the map
     */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) 
	{
		boolean result = super.dispatchTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP){
			GeoPoint center = mainMap.getMapCenter();
			mapCenter = new Location("");
			mapCenter.setLatitude((double)center.getLatitudeE6()/(double)1E6);
			mapCenter.setLongitude((double)center.getLongitudeE6()/(double)1E6);

			nearOverlay.getOverlays().clear();
			showNearBusStopsOnMap(mapCenter);
		}

		return result;
	}

	/**
	 * Draw a path on the map
	 * @param src, dest
	 */
	private void drawPath(GeoPoint src, GeoPoint dest) 
	{ 
		if(src == null || dest == null)
		{
			Log.d("RouteMap", "Source or Destination not set");
			return;
		}
		StringBuilder urlString = getURL(src, dest); 
		
		Document doc = null; 
		HttpURLConnection urlConnection= null; 
		URL url = null; 
		try 
		{ 
			url = new URL(urlString.toString()); 
			urlConnection=(HttpURLConnection)url.openConnection(); 
			urlConnection.setRequestMethod("GET"); 
			urlConnection.setDoOutput(true); 
			urlConnection.setDoInput(true); 
			urlConnection.connect(); 

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
				
				mainMap.getOverlays().add(new DirectionPathOverlay(dest,dest));
				
				mainMap.invalidate();
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Helper to build URL 
	 * @param src, dest
	 * @return URL string
	 */
	private StringBuilder getURL(GeoPoint src, GeoPoint dest) {
		StringBuilder urlString = new StringBuilder(); 
		urlString.append("http://maps.google.com/maps?f=d&hl=en"); 
		urlString.append("&saddr="); 
		urlString.append( Double.toString((double)src.getLatitudeE6()/1.0E6 )); 
		urlString.append(","); 
		urlString.append( Double.toString((double)src.getLongitudeE6()/1.0E6 )); 
		urlString.append("&daddr=");
		urlString.append( Double.toString((double)dest.getLatitudeE6()/1.0E6 )); 
		urlString.append(","); 
		urlString.append( Double.toString((double)dest.getLongitudeE6()/1.0E6 )); 
		urlString.append("&ie=UTF8&0&om=0&output=kml");
		
		return urlString;
	}	
	
	/**
	 * This method starts the Timetable activity
	 * @param view
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
	
	private void calcRemainingTimeAndDistance() {
		remainingTime = (TextView)findViewById(R.id.remainingTime);
		remainingDistance = (TextView)findViewById(R.id.remainingDistance);
		
		setRemainingTime(remainingTime);
		setRemainingDistance(remainingDistance);
	}

	private void setRemainingTime(TextView remainingTime) {


		second = new TimerTask() {
			public void run() {

				Log.i("Test", "Timer start");

				Update();

				currentDt = new Date(); 
				currentHours = currentDt.getHours(); 
				currentMinutes = currentDt.getMinutes();
			}
		};

		Timer timer = new Timer();
		timer.schedule(second, 0, 1000);
	}

	protected void Update() {
		Runnable updater = new Runnable() {
			public void run() {
			       remainingTime.setText((hourSet-currentHours) + " hours " + (minuteSet-currentMinutes) + " minutes");
			}
		};

		handler.post(updater);
	}

	private void setRemainingDistance(TextView remainingDistance) {
		
		remainingDistance.setText("DISTANCE DISTANCE DISTACNE");
	}
		
	/**
	 * This method returns whether routes are currently being displayed on the
	 * map. Right now, they're not.
	 */
	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}
}