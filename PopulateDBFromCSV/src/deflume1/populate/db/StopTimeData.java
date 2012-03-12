package deflume1.populate.db;

public class StopTimeData 
{	
	private int tripIDHash;
	private int stopIDHash;
	private String tripID;
	private String arrivalTime;
	private String departureTime;
	private String stopID;
	
	public StopTimeData(String tripID, String arrivalTime, String departureTime, String stopID)
	{
		tripIDHash = tripID.hashCode();
		stopIDHash = stopID.hashCode();
		setTripID(tripID);
		setArrivalTime(arrivalTime);
		setDepartureTime(departureTime);
		setStopID(stopID);
	}

	public int getTripIDHash()
	{
		return tripIDHash;
	}
	
	public int getStopIDHash()
	{
		return stopIDHash;
	}
	
	public String getTripID() 
	{
		return tripID;
	}

	public void setTripID(String tripID) 
	{
		this.tripID = tripID;
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

	public void setDepartureTime(String departureTime) 
	{
		this.departureTime = departureTime;
	}

	public String getStopID() 
	{
		return stopID;
	}

	public void setStopID(String stopID) 
	{
		this.stopID = stopID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((arrivalTime == null) ? 0 : arrivalTime.hashCode());
		result = prime * result
				+ ((departureTime == null) ? 0 : departureTime.hashCode());
		result = prime * result + ((stopID == null) ? 0 : stopID.hashCode());
		result = prime * result + stopIDHash;
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
		StopTimeData other = (StopTimeData) obj;
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
		if (stopID == null) {
			if (other.stopID != null)
				return false;
		} else if (!stopID.equals(other.stopID))
			return false;
		if (stopIDHash != other.stopIDHash)
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
