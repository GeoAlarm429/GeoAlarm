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
	private ArrayList<StopInfo> startAndDest = new ArrayList<StopInfo>(2);

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
		NearStopOverlayItem item = (NearStopOverlayItem) overlays.get(index);
		Toast.makeText(mapContext, "BUS STOP", Toast.LENGTH_SHORT).show();
		
/*		StopInfo busStop = item.getBusStop();
		if(busStop.getIsSelected()){
			busStop.setSelected(false);
			Toast.makeText(mapContext, "FASLE", Toast.LENGTH_SHORT).show();
		}
		else {
			busStop.setSelected(true);
			startAndDest.add(busStop);
			Toast.makeText(mapContext, "True + " + startAndDest.size(), Toast.LENGTH_SHORT).show();
		}*/

		return true;
	}

	public ArrayList<OverlayItem> getOverlays() {
		return overlays;
	}
}
