package routing;

public class Location {
	private int x;
	private int y;
	private int start;
	private int stop;
	private int time;
	
	public Location(int x, int y, int start, int stop, int time) {
		super();
		this.x = x;
		this.y = y;
		this.start = start;
		this.stop = stop;
		this.time = time;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public int getStop() {
		return stop;
	}
	
	public void setStop(int stop) {
		this.stop = stop;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
