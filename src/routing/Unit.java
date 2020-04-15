package routing;

import java.util.ArrayList;

import java.util.List;
import java.util.StringJoiner;

public class Unit implements Comparable<Unit>{
	private List<Vehicle> trucks;
	private double distance;
	private double balance;
	private double groupingDistance;
	private int rank;
	private double[][] distances;
	private Location[] locations;
	private int currentIndex = -1;
	
	public Unit(List<Vehicle> trucks, double[][] distances, Location[] locations) {
		this.trucks = trucks;
		this.distance = Double.MAX_VALUE;
		this.distances = distances;
		this.locations = locations;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(trucks.size() + "\n");
		for (int i = 0; i < trucks.size(); i++) {
			sb.append("" + (i + 1) + ": ");
			
			StringJoiner sj = new StringJoiner("->");
			Vehicle current = trucks.get(i);
			List<Integer> route = current.getRoute();
			 
			int count = 0;
			sj.add("0(0)");
			for (int j = 0; j < route.size() - 1; j++) {
				int curr = route.get(j);
				int next = route.get(j + 1);
				Location location = locations[next];
				
				count += Math.ceil(distances[curr][next]);
				count = Math.max(location.getStart(), count);
				sj.add(next + "(" + count + ")" );
				
				count += location.getTime();
			}
			
			sb.append(sj.toString() + "\n");
		}
		
		sb.append(distance + "\n");
		return "distance: " + distance + " balance: " + balance;
	}
	
	public List<Vehicle> getTrucks() {
		return trucks;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	public double getGroupingDistance() {
		return groupingDistance;
	}
	
	public void setGroupingDistance(double groupingDistance) {
		this.groupingDistance = groupingDistance;
	}
	
	public int getRank() {
		return rank;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public List<List<Integer>> getRoutes() {
		List<List<Integer>> routes = new ArrayList();
		for (Vehicle t: trucks) {
			routes.add(t.getRoute());
		}
		
		return routes;
	}

	@Override
	public int compareTo(Unit other) {
		if (distance == other.distance && balance == other.balance) return 0;
		else if (distance >= other.distance && balance <= other.balance) return 1;
		else if (distance <= other.distance && balance >= other.balance) return -1;
		else return 0;
	}
	
	public boolean dominates(Unit other) {
		return compareTo(other) < 0;
	}
	
	public boolean is_dominated(Unit other) {
		return compareTo(other) > 0;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Unit)) {
			return false;
		}
		
		Unit second = (Unit) other;
		
		if (distance == second.distance && balance == second.balance) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return ((Double) distance).hashCode();
	}
	
	public Unit copy() {
		List<Vehicle> copyTrucks = new ArrayList();
		for (Vehicle t : trucks) {
			copyTrucks.add(t.copy());
		}
		
		return new Unit(copyTrucks, distances, locations);
	}
}
