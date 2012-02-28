package edu.illinois.geoalarm;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.slee.Helper.MTDDbController;

/**
 * A MapActivity class that will be responsible for displaying the transit map.
 * 
 * @author deflume1
 * 
 */

public class RouteMap extends MapActivity {
	private static final int INITIAL_ZOOM = 15;
	protected static final int LAUNCH_ACTIVITY = 1;

	private MapView mainMap;
	private MapController mapControl;
	private Button backBtn;
	private Location currentLocation;
	private GeoPoint centerPoint;
	private List<Overlay> currentMarkerOverlays;
	private List<Overlay> nearStopsOverlays;
	private ArrayList<StopInfo> nearStops;
	private Location mapCenter;
	private GeoAlarmDB dbController;

	/** Called when the activity is first created. 
	 * @author Hyung Joo Kim and Seung Mok Lee
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.map);
        
        dbController = new GeoAlarmDB(this);
        try {
        	dbController.openDataBase();
        } catch (SQLException e) {
        			throw e;
        }
        		
        mainMap = (MapView)findViewById(R.id.mainMap);
        backBtn = (Button)findViewById(R.id.backBtn);
        
        // Get current location and show it on the map
        showCurrentLocation();
        
        // Setup Google Map
        mapControl = mainMap.getController();
        mainMap.setBuiltInZoomControls(true);

        mapControl.animateTo(centerPoint);
        mapControl.setZoom(INITIAL_ZOOM);
        
        showNearBusStopsOnMap(currentLocation);
        
        // Event Listeners
        backBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(RouteMap.this, GeoAlarm.class);
				startActivityForResult(intent, LAUNCH_ACTIVITY);
			}
		});
    }

	/**
	 * Method to show current location on the map
	 * @author Hyung Joo Kim and Seung Mok Lee
	 */
	private void showCurrentLocation() {
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
	       
		// Get the lastest location
		currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));

		// Update location with new location information
		double latitude = currentLocation.getLatitude();   
		double longitude = currentLocation.getLongitude();
	  
		// Point for the current location
		centerPoint = new GeoPoint((int)(latitude*1E6), (int)(longitude*1E6));
		
		// Show a marker on the map
		currentMarkerOverlays = mainMap.getOverlays(); 
	    Drawable drawable = this.getResources().getDrawable(R.drawable.current);        
	    
	    CurrMarkerOverlay itemizedOverlay = new CurrMarkerOverlay(drawable, this);
        OverlayItem overlayitem = new OverlayItem(centerPoint, "", "");
        
        itemizedOverlay.addOverlay(overlayitem);  
        currentMarkerOverlays.add(itemizedOverlay);
	}
	
    /**
     * Helper function to show the bus stops near the current location on the map
     * @param currentLocation 
     * @author Hyung Joo Kim and Seung Mok Lee
     */
	private void showNearBusStopsOnMap(Location currentLocation) {
		nearStopsOverlays = mainMap.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.near);
		
		nearStops = dbController.getAroundMe(currentLocation);
		NearStopOverlay itemizedOverlay = new NearStopOverlay(drawable, this);
		
		if(!nearStops.isEmpty()){
			for(StopInfo stopToShow : nearStops){
				
				// A point to show on the map
				NearStopOverlayItem item = new NearStopOverlayItem(stopToShow);
				itemizedOverlay.addOverlay(item);
			}
			
			nearStopsOverlays.add(itemizedOverlay);
		}
		else {
			Toast.makeText(RouteMap.this, "No near bus stop", Toast.LENGTH_LONG).show();
			onResume();
		}
	}
	
    /**
     * Called when the user move the center of the map
     * @author Hyung Joo Kim and Seung Mok Lee
     */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		boolean result = super.dispatchTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP){
			GeoPoint center = mainMap.getMapCenter();
			mapCenter = new Location("");
			mapCenter.setLatitude((double)center.getLatitudeE6()/(double)1E6);
			mapCenter.setLongitude((double)center.getLongitudeE6()/(double)1E6);
			
			// Clear previous view
			nearStopsOverlays.clear();
			
			showCurrentLocation();
			showNearBusStopsOnMap(mapCenter);
		}

		return result;
	}

	/**
	 * This method returns whether routes are currently being displayed on the
	 * map. Right now, they're not.
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}