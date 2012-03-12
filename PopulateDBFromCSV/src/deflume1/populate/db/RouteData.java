package deflume1.populate.db;

public class RouteData 
{	
	private int routeIDHash;
	private String routeID;
	private String routeName;
	private String routeColor;
	
	public RouteData(String ID, String name, String color)
	{
		routeIDHash = ID.hashCode();
		setID(ID);
		setName(name);
		setColor(color);
	}
	
	public int getRouteIDHash()
	{
		return routeIDHash;
	}

	public String getID() 
	{
		return routeID;
	}

	public void setID(String routeID) 
	{
		this.routeID = routeID;
	}

	public String getName() 
	{
		return routeName;
	}

	public void setName(String routeName)
	{
		this.routeName = routeName;
	}

	public String getColor() 
	{
		return routeColor;
	}

	public void setColor(String routeColor) 
	{
		this.routeColor = routeColor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((routeColor == null) ? 0 : routeColor.hashCode());
		result = prime * result + ((routeID == null) ? 0 : routeID.hashCode());
		result = prime * result + routeIDHash;
		result = prime * result
				+ ((routeName == null) ? 0 : routeName.hashCode());
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
		RouteData other = (RouteData) obj;
		if (routeColor == null) {
			if (other.routeColor != null)
				return false;
		} else if (!routeColor.equals(other.routeColor))
			return false;
		if (routeID == null) {
			if (other.routeID != null)
				return false;
		} else if (!routeID.equals(other.routeID))
			return false;
		if (routeIDHash != other.routeIDHash)
			return false;
		if (routeName == null) {
			if (other.routeName != null)
				return false;
		} else if (!routeName.equals(other.routeName))
			return false;
		return true;
	}
}
