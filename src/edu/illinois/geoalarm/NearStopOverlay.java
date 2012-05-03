package edu.illinois.geoalarm;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * An ItemizedOverlay<<OverlayItem>> for showing nearby stops on the map
 * @author GeoAlarm
 */
public class NearStopOverlay extends ItemizedOverlay<OverlayItem> 
{
	private ArrayList<NearStopOverlayItem> mOverlays = new ArrayList<NearStopOverlayItem>();
	private Context mContext;
	private NearStopOverlayItem selectedItem;
	private GeoAlarmDB database;

	/**
	 * Constructs a new NearStopOverlay with specified parameters
	 * @param defaultMarker The Drawable used for marker display
	 * @param context The context the overlay will be used in
	 * @param db A handle to the GeoAlarmDB database
	 */
	public NearStopOverlay(Drawable defaultMarker, Context context, GeoAlarmDB db) 
	{
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		database = db;
	}

	@Override
	protected OverlayItem createItem(int index) 
	{
		return mOverlays.get(index);
	}

	@Override
	public int size() 
	{
		return mOverlays.size();
	}	
	
	/**
	 * Adds a new overlay to this object
	 * @param overlay The NearStopOverlayItem to add
	 */
	public void addOverlay(NearStopOverlayItem overlay) 
	{ 
		mOverlays.add(overlay); 
	    populate(); 
	}
	
	/**
	 * Called when the bus icon is tapped.  Shows the list of bus lines that service the
	 * selected stop in a custom dialog.
	 */
	@Override
	protected boolean onTap(int index) 
	{				
		selectedItem = mOverlays.get(index);
					
		Dialog dialog = new Dialog(mContext);

		dialog.setContentView(R.layout.route_map_dialog);
		dialog.setTitle(selectedItem.getBusStop().getFullName());
		
		TextView routesLabel = (TextView)dialog.findViewById(R.id.routeMapDialogRoutesLabel);
		LinearLayout routeListLinearLayout = (LinearLayout)dialog.findViewById(R.id.routeListLinearLayout);
		
		/* Populate display with lines servicing this stop */
		ArrayList<String> routeList = database.getLinesForStopName(selectedItem.getBusStop().getFullName());
		
		routeListLinearLayout.removeAllViews();
		routeListLinearLayout.addView(routesLabel);
		for(String routeName : routeList)
		{
			TextView newView = new TextView(dialog.getContext());
			newView.setText(routeName);
			newView.setTextSize(20);
			routeListLinearLayout.addView(newView);
		}			
		
		Window window = dialog.getWindow();
		window.setLayout((int)(window.getWindowManager().getDefaultDisplay().getWidth()),
				(int)(window.getWindowManager().getDefaultDisplay().getHeight() * .50));
					
		dialog.show();		
		return true;		
	}

	/**
	 * Returns the list of overlay objects this NearStopOverlay wraps
	 * @return The ArrayList<<NearStopOverlayItem>> this object wraps
	 */
	public ArrayList<NearStopOverlayItem> getOverlays() 
	{
		return mOverlays;
	}
}
