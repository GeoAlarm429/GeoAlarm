package edu.illinois.geoalarm;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * For the near bus stop overlay item
 * @author Seungmok Lee, Hyungjoo Kim
 */
public class NearStopOverlayItem extends OverlayItem {
	@SuppressWarnings("unused")
	private StopInfo busStop;
	
	/**
	 * Default constructor
	 * @param point, title, snippet
	 */
	public NearStopOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}
	
	/**
	 * Constructor with StopInfo object
	 * @param stop
	 */
	public NearStopOverlayItem(StopInfo stop) {
		super(new GeoPoint((int)(stop.getLatitude()*1E6), (int)(stop.getLongitude()*1E6)), stop.getFullName(), "Tap to see bus information");
		busStop = stop;
	}
}
