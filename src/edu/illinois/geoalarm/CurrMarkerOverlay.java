package edu.illinois.geoalarm;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * To show marker on the map
 * @author Seungmok Lee, Hyungjoo Kim
 */
public class CurrMarkerOverlay extends ItemizedOverlay<OverlayItem> 
{
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();	
	
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

	public void addOverlay(OverlayItem overlay) 
	{ 
		overlays.add(overlay); 
	    populate(); 
	}
}
