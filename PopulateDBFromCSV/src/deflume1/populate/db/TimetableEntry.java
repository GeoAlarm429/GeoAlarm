package deflume1.populate.db;

public class TimetableEntry 
{
	private String stopID;
	private String routeID;
	private String arrivalTime;
	
	public TimetableEntry(String stopID, String routeID, String arrivalTime) 
	{
		this.stopID = stopID;
		this.routeID = routeID;
		this.arrivalTime = arrivalTime;
	}
	
	public int getStopIDHash()
	{
		return stopID.hashCode();
	}
	
	public int getRouteIDHash()
	{
		return routeID.hashCode();
	}

	public String getStopID() 
	{
		return stopID;
	}

	public void setStopID(String stopID) 
	{
		this.stopID = stopID;
	}

	public String getRouteID() 
	{
		return routeID;
	}

	public void setRouteID(String routeID) 
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((arrivalTime == null) ? 0 : arrivalTime.hashCode());
		result = prime * result + ((routeID == null) ? 0 : routeID.hashCode());
		result = prime * result + ((stopID == null) ? 0 : stopID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
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
		if (routeID == null) {
			if (other.routeID != null)
				return false;
		} else if (!routeID.equals(other.routeID))
			return false;
		if (stopID == null) {
			if (other.stopID != null)
				return false;
		} else if (!stopID.equals(other.stopID))
			return false;
		return true;
	}

}
