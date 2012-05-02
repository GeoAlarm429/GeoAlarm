package edu.illinois.geoalarm.parser;
import java.util.Vector;

public class Station {

	private String stopID;
	private String name;
	private String direction;
	private Vector<subStation> stations;

	public Station(String stopID, String name) {
		this.stopID = stopID;
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
	
		
	private class subStation {
		private String direction;
		private String stopID;
		private String longtitude;
		private String latitude;
		
		private subStation(String stopID,String longtitude, String latitude) {
			this.stopID = stopID;
			this.longtitude = longtitude;
			this.latitude = latitude;
		}

		public String getStopID() {
			return stopID;
		}
		public void setStopID(String stopID) {
			this.stopID = stopID;
		}
		public String getName() {
			return name;
		}

		public String getLongtitude() {
			return longtitude;
		}

		public void setLongtitude(String longtitude) {
			this.longtitude = longtitude;
		}

		public String getLatitude() {
			return latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
	}
}


