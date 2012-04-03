import java.util.Vector;

public class Itinerary {
	private Vector<Leg> legs;
	private String startTime;
	private String endTime;
	
	public Itinerary(String startTime,String endTime){
		legs = new Vector<Leg>();
		this.startTime=startTime;
		this.endTime=endTime;
	}
	
	public void addLeg(Trip begin, Trip end){
		Leg leg = new Leg(begin, end);
		legs.add(leg);
	}
	
	public Vector<Leg> getLegs() {
		return legs;
	}

	public void setLegs(Vector<Leg> legs) {
		this.legs = legs;
	}

	public class Leg{
		private Trip begin;
		private Trip end;
		
		public Trip getBegin() {
			return begin;
		}

		public void setBegin(Trip begin) {
			this.begin = begin;
		}

		public Trip getEnd() {
			return end;
		}

		public void setEnd(Trip end) {
			this.end = end;
		}

		public Leg(Trip begin, Trip end){
			this.begin = begin;
			this.end = end;
		}
	}
	
}
