package model;

public class City {
	
	private Spot[] spot = new Spot[9];
	
	public City() {
		// TODO Auto-generated constructor stub
		for(int i=0; i<9; i++) {
			spot[i] = new Spot();
		}
	}

	public Spot[] getSpot() {
		return spot;
	}

	public void setSpot(Spot[] spot) {
		this.spot = spot;
	}
}
