package edu.illinois.geoalarm.parser;
import java.util.Vector;

/**
 * A storage object for a Route
 * @author GeoAlarm
 *
 */
public class Route 
{
	private String name;
	private int number;
	private Vector<Station> stops;
	
	public Route(String routeID, String name, int number) 
	{		
		this.name = name;
		this.number = number;
		this.stops = new Vector<Station>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}

	public Vector<Station> getStops() {
		return this.stops;
	}

	public void setDirection(Vector<Station> stops) {
		this.stops = new Vector<Station>();
		int vectorSize = stops.size();
		for(int i = 0; i < vectorSize; i++) {
			Station currStop = stops.get(i);
			this.stops.add(currStop);
		}
	}
	
	
}
