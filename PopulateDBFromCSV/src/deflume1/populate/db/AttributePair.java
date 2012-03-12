package deflume1.populate.db;

public class AttributePair<T,K>
{
	private T firstElement;
	private K secondElement;
	
	public AttributePair(T first, K second)
	{
		setFirstElement(first);
		setSecondElement(second);
	}

	public T getFirstElement() 
	{
		return firstElement;
	}

	public void setFirstElement(T firstElement) 
	{
		this.firstElement = firstElement;
	}

	public K getSecondElement() 
	{
		return secondElement;
	}

	public void setSecondElement(K secondElement) 
	{
		this.secondElement = secondElement;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstElement == null) ? 0 : firstElement.hashCode());
		result = prime * result
				+ ((secondElement == null) ? 0 : secondElement.hashCode());
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
		AttributePair other = (AttributePair) obj;
		if (firstElement == null) {
			if (other.firstElement != null)
				return false;
		} else if (!firstElement.equals(other.firstElement))
			return false;
		if (secondElement == null) {
			if (other.secondElement != null)
				return false;
		} else if (!secondElement.equals(other.secondElement))
			return false;
		return true;
	}
}