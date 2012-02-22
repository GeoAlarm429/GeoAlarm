package edu.illinois.geoalarm;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("unchecked")
/**
 * @author Seungmok Lee, Hyungjoo Kim
 */
public class CurrMarkerOverlay extends ItemizedOverlay {
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	@SuppressWarnings("unused")
	private Context mapContext;
	
	public CurrMarkerOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mapContext = context;
	}

	@Override
	protected OverlayItem createItem(int index) {
		return overlays.get(index);
	}

	@Override
	public int size() {
		return overlays.size();
	}

	public void addOverlay(OverlayItem overlay) { 
		overlays.add(overlay); 
	    populate(); 
	}	
}
