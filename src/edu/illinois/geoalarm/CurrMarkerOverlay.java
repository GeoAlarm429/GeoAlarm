package edu.illinois.geoalarm;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * This class override ItemizedOverlay<<OverlayItem>>, to provide an overlay suitable for displaying the
 * current location of the user.  This ItemizedOverlay can be used in an instance of the RouteMap class
 * @author GeoAlarm
 */
public class CurrMarkerOverlay extends ItemizedOverlay<OverlayItem> 
{
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();	
	
	/**
	 * Constructs a new CurrMarkerOverlay
	 * @param defaultMarker The Drawable used for marker display
	 * @param context The context the overlay will be used in
	 */
	public CurrMarkerOverlay(Drawable defaultMarker, Context context) 
	{
		super(boundCenterBottom(defaultMarker));		
	}

	@Override
	protected OverlayItem createItem(int index) 
	{
		return overlays.get(index);
	}

	@Override
	public int size() 
	{
		return overlays.size();
	}

	/**
	 * Adds a new overlay item to this overlay
	 * @param overlayItem The OverlayItem to be added
	 */
	public void addOverlay(OverlayItem overlayItem) 
	{ 
		overlays.add(overlayItem); 
	    populate(); 
	}
}
