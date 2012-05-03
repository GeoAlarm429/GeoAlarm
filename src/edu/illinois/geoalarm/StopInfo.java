package edu.illinois.geoalarm;

/**
 * A class which encapsulates all of the information about a particular stop
 * @author GeoAlarm
 */
public class StopInfo 
{
	private String name;
	private double latitude;
	private double longitude;
	
	/**
	 * Constructs a new StopInfo object with the specified parameters
	 * @param fullName The full name of the stop
	 * @param latitude The latitude where the stop is located
	 * @param longitude The longitude where the stop is located
	 */
	public StopInfo(String fullName, double latitude, double longitude) 
	{
		super();
		this.name = fullName;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Returns the full name of the stop
	 * @return The full name of the stop
	 */
	public String getFullName() 
	{
		return name;
	}

	/**
	 * Returns the latitude of the stop
	 * @return The latitude of the stop
	 */
	public double getLatitude() 
	{
		return latitude;
	}

	/**
	 * Returns the longitude of the stop
	 * @return The longitude of the stop
	 */
	public double getLongitude() 
	{
		return longitude;
	}
	
}
