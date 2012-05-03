package edu.illinois.geoalarm;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * An implementation of OverlayItem, used to show nearby bus stops
 * @author GeoAlarm
 */
public class NearStopOverlayItem extends OverlayItem 
{
	private StopInfo busStop;
	
	/**
	 * Constructs a new NearStopOverlayItem from the specified parameters
	 * @param point The GeoPoint where the stop is located
	 * @param stopName The full name of the stop
	 * @param snippet Any String (required for compatibility)
	 */
	public NearStopOverlayItem(GeoPoint point, String stopName, String snippet) 
	{
		super(point, stopName, snippet);
		busStop = new StopInfo(stopName, point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6);
	}
	
	/**
	 * Constructs a new NearStopOverlayItem from a StopInfo object
	 * @param stop A StopInfo object containing information about a stop
	 */
	public NearStopOverlayItem(StopInfo stop) 
	{
		super(new GeoPoint((int)(stop.getLatitude()*1E6), (int)(stop.getLongitude()*1E6)), stop.getFullName(), "Tap to see bus information");
		busStop = stop;
	}
	
	/**
	 * Returns the StopInfo object wrapped by this object
	 * @return The StopInfo object
	 */
	public StopInfo getBusStop() 
	{
		return busStop;
	}
}
