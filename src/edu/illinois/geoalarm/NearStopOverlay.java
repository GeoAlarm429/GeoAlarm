package edu.illinois.geoalarm;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * To show near bus stops on the map
 * @author Seungmok Lee, Hyungjoo Kim
 */
public class NearStopOverlay extends ItemizedOverlay<OverlayItem> 
{
	private ArrayList<NearStopOverlayItem> mOverlays = new ArrayList<NearStopOverlayItem>();
	private Context mContext;
	private ArrayList<StopInfo> startAndDest = new ArrayList<StopInfo>(2);
	private NearStopOverlayItem selectedItem;
	private GeoAlarmDB database;

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
	
	public void addOverlay(NearStopOverlayItem overlay) 
	{ 
		mOverlays.add(overlay); 
	    populate(); 
	}
	
	/**
	 * Called when the bus icon is tapped
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

	public ArrayList<NearStopOverlayItem> getOverlays() 
	{
		return mOverlays;
	}
}
