package deflume1.populate.db;

public class TripData 
{
	private int routeIDHash;
	private int tripIDHash;	
	private String routeID;
	private String tripID;
	private String tripHeadsign;
	
	public TripData(String routeID, String tripID, String tripHeadsign)
	{
		routeIDHash = routeID.hashCode();
		tripIDHash = tripID.hashCode();		
		setRouteID(routeID);		
		setTripID(tripID);
		setTripHeadsign(tripHeadsign);		
	}

	public int getRouteIDHash()
	{
		return routeIDHash;
	}
	
	public int getTripIDHash()
	{
		return tripIDHash;
	}
	
	public String getRouteID() 
	{
		return routeID;
	}

	public void setRouteID(String routeID) 
	{
		this.routeID = routeID;
	}

	public String getTripID() 
	{
		return tripID;
	}

	public void setTripID(String tripID) 
	{
		this.tripID = tripID;
	}

	public String getTripHeadsign() 
	{
		return tripHeadsign;
	}

	public void setTripHeadsign(String tripHeadsign) 
	{
		this.tripHeadsign = tripHeadsign;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((routeID == null) ? 0 : routeID.hashCode());
		result = prime * result + routeIDHash;
		result = prime * result
				+ ((tripHeadsign == null) ? 0 : tripHeadsign.hashCode());
		result = prime * result + ((tripID == null) ? 0 : tripID.hashCode());
		result = prime * result + tripIDHash;
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
		TripData other = (TripData) obj;
		if (routeID == null) {
			if (other.routeID != null)
				return false;
		} else if (!routeID.equals(other.routeID))
			return false;
		if (routeIDHash != other.routeIDHash)
			return false;
		if (tripHeadsign == null) {
			if (other.tripHeadsign != null)
				return false;
		} else if (!tripHeadsign.equals(other.tripHeadsign))
			return false;
		if (tripID == null) {
			if (other.tripID != null)
				return false;
		} else if (!tripID.equals(other.tripID))
			return false;
		if (tripIDHash != other.tripIDHash)
			return false;
		return true;
	}

}
