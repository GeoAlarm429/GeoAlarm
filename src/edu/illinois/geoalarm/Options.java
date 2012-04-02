package edu.illinois.geoalarm;

import java.text.DecimalFormat;

import android.app.Activity;
import android.database.SQLException;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Process;
import android.widget.TextView;

/**
 * This Activity is used to set various user options
 * for the GeoAlarm app
 * @author deflume1
 *
 */

public class Options extends Activity 
{		
	GeoAlarmDB database;
	TextView sessionUsageTxView;
	TextView sessionUsageRxView;
	TextView totalUsageTxView;	 
	TextView totalUsageRxView;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.options);
        
        sessionUsageTxView = (TextView)findViewById(R.id.sessionDataTxShow);
        sessionUsageRxView = (TextView)findViewById(R.id.sessionDataRxShow);
        totalUsageTxView = (TextView)findViewById(R.id.totalDataTxShow);
        totalUsageRxView = (TextView)findViewById(R.id.totalDataRxShow);        
    }
    
    @Override 
    public void onStart()
    {
    	showUsageData();
    	super.onStart();
    }
    
    @Override
    public void onResume()
    {
    	showUsageData();
    	super.onResume();
    }
    
    /**
     * This function updates the usage data labels when its launched.  It uses the data stored in the UsageTable of GeoAlarmDB
     */
   public void showUsageData()
   {
	   	database = new GeoAlarmDB(this);
       	try 
       	{
    	   	database.openDataBase();
       	} 
       	catch (SQLException e) 
       	{
    	   	e.printStackTrace();
    	   	throw e;
       	}
       
        long numBytesLastReceivedSession =  database.getBytes(GeoAlarmDB.DB_RX_SESSION);
		long numBytesLastTransmittedSession =  database.getBytes(GeoAlarmDB.DB_TX_SESSION);
		long numBytesReceived = database.getBytes(GeoAlarmDB.DB_RX);
		long numBytesTransmitted = database.getBytes(GeoAlarmDB.DB_TX);		
		database.close();
		
		double numMegaBytesReceivedSession = ((double) numBytesLastReceivedSession) / 1E6;		
		double numMegaBytesTransmittedSession = ((double) numBytesLastTransmittedSession) / 1E6;
		double numMegaBytesReceived = ((double) numBytesReceived) / 1E6;
		double numMegaBytesTransmitted = ((double) numBytesTransmitted) / 1E6;
		
		DecimalFormat df = new DecimalFormat("#.###");
		String displaySessionRx = " " + df.format(numMegaBytesReceivedSession) + " MB";
		String displaySessionTx = " " + df.format(numMegaBytesTransmittedSession) + " MB";
		String displayTotalRx = " " + df.format(numMegaBytesReceived) + " MB";
		String displayTotalTx = " " + df.format(numMegaBytesTransmitted) + " MB";
		
		sessionUsageRxView.setText(displaySessionRx);
		sessionUsageTxView.setText(displaySessionTx);		
		totalUsageRxView.setText(displayTotalRx);	   
		totalUsageTxView.setText(displayTotalTx);
   }

}
