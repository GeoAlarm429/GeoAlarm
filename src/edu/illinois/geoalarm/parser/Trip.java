package edu.illinois.geoalarm.parser;

/**
 * A storage object for a Trip
 * @author GeoAlarm
 *
 */

public class Trip {
	private String longitude;
	private String latitude;
	private String time;
	private String busName;
	
	public Trip( String latitude, String longitude, String time, String busName){
		this.busName = busName;
		this.longitude = longitude;
		this.latitude = latitude;
		this.time = time;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getBusName() {
		return busName;
	}

	public void setBusName(String busName) {
		this.busName = busName;
	}
}
