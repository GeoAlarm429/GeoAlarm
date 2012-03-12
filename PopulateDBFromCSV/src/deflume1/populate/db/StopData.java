package deflume1.populate.db;

public class StopData 
{
	private int stopIDHash;
	private String stopID;
	private String stopName;
	private double stopLatitude;
	private double stopLongitude;
	
	public StopData(String stopID, String stopName, String stopLatitude, String stopLongitude)
	{
		stopIDHash = stopID.hashCode();
		setID(stopID);
		setName(stopName);
		setLatitude(Double.parseDouble(stopLatitude));
		setLongitude(Double.parseDouble(stopLongitude));
	}

	public int getStopIDHash()
	{
		return stopIDHash;
	}
	
	public String getID() 
	{
		return stopID;
	}

	public void setID(String stopID) 
	{
		this.stopID = stopID;
	}

	public String getName() 
	{
		return stopName;
	}

	public void setName(String stopName) 
	{
		this.stopName = stopName;
	}

	public double getLatitude() 
	{
		return stopLatitude;
	}

	public void setLatitude(double stopLatitude) 
	{
		this.stopLatitude = stopLatitude;
	}

	public double getLongitude() 
	{
		return stopLongitude;
	}

	public void setLongitude(double stopLongitude) 
	{
		this.stopLongitude = stopLongitude;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stopID == null) ? 0 : stopID.hashCode());
		result = prime * result + stopIDHash;
		long temp;
		temp = Double.doubleToLongBits(stopLatitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(stopLongitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((stopName == null) ? 0 : stopName.hashCode());
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
		StopData other = (StopData) obj;
		if (stopID == null) {
			if (other.stopID != null)
				return false;
		} else if (!stopID.equals(other.stopID))
			return false;
		if (stopIDHash != other.stopIDHash)
			return false;
		if (Double.doubleToLongBits(stopLatitude) != Double
				.doubleToLongBits(other.stopLatitude))
			return false;
		if (Double.doubleToLongBits(stopLongitude) != Double
				.doubleToLongBits(other.stopLongitude))
			return false;
		if (stopName == null) {
			if (other.stopName != null)
				return false;
		} else if (!stopName.equals(other.stopName))
			return false;
		return true;
	}
	
}
