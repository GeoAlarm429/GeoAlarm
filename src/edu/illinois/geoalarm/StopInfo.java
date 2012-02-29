package edu.illinois.geoalarm;

/**
 * Bus stop information
 * @author Seungmok Lee, Hyungjoo Kim
 */
public class StopInfo {
	private String name;
	private double latitude;
	private double longitude;
	
	public StopInfo(String fullName, double latitude, double longitude) {
		super();
		this.name = fullName;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	// Getters
	public String getFullName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
}
