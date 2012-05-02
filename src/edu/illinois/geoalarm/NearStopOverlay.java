package edu.illinois.geoalarm;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

	public NearStopOverlay(Drawable defaultMarker, Context context) 
	{
		super(boundCenterBottom(defaultMarker));
		mContext = context;
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
		
/*		NearStopOverlayItem item = (NearStopOverlayItem) overlays.get(index);
		Toast.makeText(mapContext, "BUS STOP", Toast.LENGTH_SHORT).show();
		
	StopInfo busStop = item.getBusStop();
		if(busStop.getIsSelected()){
			busStop.setSelected(false);
			Toast.makeText(mapContext, "FASLE", Toast.LENGTH_SHORT).show();
		}
		else {
			busStop.setSelected(true);
			startAndDest.add(busStop);
			Toast.makeText(mapContext, "True + " + startAndDest.size(), Toast.LENGTH_SHORT).show();
		}*/
		
		selectedItem = mOverlays.get(index);
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(selectedItem.getBusStop().getFullName());
		final CharSequence[] items = {"OK"};
		dialog.setItems(items, new DialogInterface.OnClickListener() 
		{
		    public void onClick(DialogInterface dialog, int item) 
		    {
		    //	Intent browserIntent = new Intent(mContext, TournamentPageActivity.class);
	    	//	browserIntent.putExtra("brute.squad.app.selected_tournament_name", selectedItem.getName());
		    //	mContext.startActivity(browserIntent);
		    }
		});		    

		dialog.show();
		return true;		
	}

	public ArrayList<NearStopOverlayItem> getOverlays() 
	{
		return mOverlays;
	}
}
