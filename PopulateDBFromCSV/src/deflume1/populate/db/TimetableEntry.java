package deflume1.populate.db;

public class TimetableEntry 
{
	private int stopID;
	private int routeID;
	private String arrivalTime;
	private String departureTime;
	
	public TimetableEntry(int stopID, int routeID, String arrivalTime, String departureTime) 
	{
		this.stopID = stopID;
		this.routeID = routeID;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
	}

	public int getStopID() 
	{
		return stopID;
	}

	public void setStopID(int stopID) 
	{
		this.stopID = stopID;
	}

	public int getRouteID() 
	{
		return routeID;
	}

	public void setRouteID(int routeID) 
	{
		this.routeID = routeID;
	}

	public String getArrivalTime() 
	{
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) 
	{
		this.arrivalTime = arrivalTime;
	}

	public String getDepartureTime() 
	{
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((arrivalTime == null) ? 0 : arrivalTime.hashCode());
		result = prime * result
				+ ((departureTime == null) ? 0 : departureTime.hashCode());
		result = prime * result + routeID;
		result = prime * result + stopID;
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimetableEntry other = (TimetableEntry) obj;
		if (arrivalTime == null) {
			if (other.arrivalTime != null)
				return false;
		} else if (!arrivalTime.equals(other.arrivalTime))
			return false;
		if (departureTime == null) {
			if (other.departureTime != null)
				return false;
		} else if (!departureTime.equals(other.departureTime))
			return false;
		if (routeID != other.routeID)
			return false;
		if (stopID != other.stopID)
			return false;
		return true;
	}

}
