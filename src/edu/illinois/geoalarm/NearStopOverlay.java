package edu.illinois.geoalarm;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * To show near bus stops on the map
 * @author Seungmok Lee, Hyungjoo Kim
 */
public class NearStopOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	private Context mapContext;

	public NearStopOverlay(Drawable defaultMarker, Context context) {
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
	
	/**
	 * Called when the bus icon is tapped
	 */
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = overlays.get(index);

		Toast.makeText(mapContext, "TESTING---TESTING---TESTING", Toast.LENGTH_LONG).show();
		return true;
	}
}
