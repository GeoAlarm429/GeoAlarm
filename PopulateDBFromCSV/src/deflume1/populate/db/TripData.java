package deflume1.populate.db;

public class TripData 
{
	private int routeIDHash;
	private int tripIDHash;	
	private String routeID;
	private String tripID;
	private String tripHeadsign;
	private String serviceID;
	

	private int directionID;
	private String blockID;
	private String shapeID;
	
	public TripData(String routeID, String serviceID, String tripID, String tripHeadsign, int directionID, String blockID, String shapeID)
	{
		routeIDHash = routeID.hashCode();
		tripIDHash = tripID.hashCode();		
		setRouteID(routeID);		
		setTripID(tripID);
		setTripHeadsign(tripHeadsign);	
		setServiceID(serviceID);
		setDirectionID(directionID);
		setBlockID(blockID);
		setShapeID(shapeID);
	}
	
	public String getServiceID() 
	{
		return serviceID;
	}

	public void setServiceID(String serviceID) 
	{
		this.serviceID = serviceID;
	}

	public int getDirectionID() 
	{
		return directionID;
	}

	public void setDirectionID(int directionID) 
	{
		this.directionID = directionID;
	}

	public String getBlockID() 
	{
		return blockID;
	}

	public void setBlockID(String blockID) 
	{
		this.blockID = blockID;
	}

	public String getShapeID() 
	{
		return shapeID;
	}

	public void setShapeID(String shapeID) 
	{
		this.shapeID = shapeID;
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
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blockID == null) ? 0 : blockID.hashCode());
		result = prime * result + directionID;
		result = prime * result + ((routeID == null) ? 0 : routeID.hashCode());
		result = prime * result + routeIDHash;
		result = prime * result
				+ ((serviceID == null) ? 0 : serviceID.hashCode());
		result = prime * result + ((shapeID == null) ? 0 : shapeID.hashCode());
		result = prime * result
				+ ((tripHeadsign == null) ? 0 : tripHeadsign.hashCode());
		result = prime * result + ((tripID == null) ? 0 : tripID.hashCode());
		result = prime * result + tripIDHash;
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
		TripData other = (TripData) obj;
		if (blockID == null) {
			if (other.blockID != null)
				return false;
		} else if (!blockID.equals(other.blockID))
			return false;
		if (directionID != other.directionID)
			return false;
		if (routeID == null) {
			if (other.routeID != null)
				return false;
		} else if (!routeID.equals(other.routeID))
			return false;
		if (routeIDHash != other.routeIDHash)
			return false;
		if (serviceID == null) {
			if (other.serviceID != null)
				return false;
		} else if (!serviceID.equals(other.serviceID))
			return false;
		if (shapeID == null) {
			if (other.shapeID != null)
				return false;
		} else if (!shapeID.equals(other.shapeID))
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
