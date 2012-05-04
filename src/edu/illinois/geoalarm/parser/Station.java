package edu.illinois.geoalarm.parser;
import java.util.Vector;

/**
 * A storage object for a station
 * @author GeoAlarm
 *
 */

public class Station {

	private String name;
	private Vector<subStation> stations;

	public Station(String stopID, String name) 
	{		
		this.name = name;
		this.stations = new Vector<subStation>();
	}
	
	public void addSubStation(String stopID, String longtitude, String latitude) {
		subStation newSubStation = new subStation(stopID, longtitude, latitude);
		stations.add(newSubStation);
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Double getlongtitude(String stopID){
		for(int i=0; i < stations.size(); i++){
			if (stations.get(i).stopID.equals(stopID))
				return Double.parseDouble(stations.get(i).longtitude);
		}
		return -1.0;
	}
	
	public Double getlatitude(String stopID){
		for(int i=0; i < stations.size(); i++){
			if (stations.get(i).stopID.equals(stopID))
				return Double.parseDouble(stations.get(i).latitude);
		}
		return -1.0;
	}
	
		
	private class subStation 
	{
		private String stopID;
		private String longtitude;
		private String latitude;
		
		private subStation(String stopID,String longtitude, String latitude) 
		{
			this.stopID = stopID;
			this.longtitude = longtitude;
			this.latitude = latitude;
		}
	}
}


