package gravtitypackage;

public class Player {
	private int px;
	private int py;
	private int tpRights;
	private int points;
	
	public Player(int px, int py, int tpRights, int points)
	{
		this.px = px;
		this.py = py;
		this.tpRights = tpRights;
		this.points = points;		
	}

	public int getPx() {
		return px;
	}

	public void setPx(int px) {
		this.px = px;
	}

	public int getPy() {
		return py;
	}

	public void setPy(int py) {
		this.py = py;
	}

	public int getTpRights() {
		return tpRights;
	}

	public void setTpRights(int tpRights) {
		this.tpRights = tpRights;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	
	

}
