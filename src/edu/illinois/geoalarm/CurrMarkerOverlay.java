package edu.illinois.geoalarm;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * @author Seungmok Lee, Hyungjoo Kim
 */
@SuppressWarnings("rawtypes")
public class CurrMarkerOverlay extends ItemizedOverlay {
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
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
	
	@SuppressWarnings("unused")
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = overlays.get(index);

		Toast.makeText(mapContext, "Give me data", Toast.LENGTH_LONG).show();
		return true;
	}
}
