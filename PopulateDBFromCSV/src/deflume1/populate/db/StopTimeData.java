package deflume1.populate.db;

public class StopTimeData 
{
	private int tripIDHash;
	private int stopIDHash;
	private String tripID;
	private String arrivalTime;
	private String departureTime;
	private String stopID;
	private int stopSequence;
	
	public StopTimeData(String tripID, String arrivalTime, String departureTime, String stopID, int stopSequence)
	{
		setTripID(tripID);
		tripIDHash = tripID.hashCode();
		stopIDHash = stopID.hashCode();
		setArrivalTime(arrivalTime);
		setDepartureTime(departureTime);
		setStopID(stopID);
		setStopSequence(stopSequence);
	}
	
	public int getStopIDHash() 
	{
		return stopIDHash;
	}
	
	public int getTripIDHash() 
	{
		return tripIDHash;
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
	public int getStopSequence() 
	{
		return stopSequence;
	}
	public void setStopSequence(int stopSequence) 
	{
		this.stopSequence = stopSequence;
	}
	
}
