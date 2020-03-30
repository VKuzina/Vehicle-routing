package routing;

import java.util.ArrayList;

import java.util.List;
import java.util.StringJoiner;

public class Unit implements Comparable<Unit>{
	private List<Vehicle> trucks;
	private double fitness;
	private double[][] distances;
	private Location[] locations;
	
	public Unit(List<Vehicle> trucks, double[][] distances, Location[] locations) {
		this.trucks = trucks;
		this.fitness = Double.MAX_VALUE;
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
		
		sb.append(fitness + "\n");
		return sb.toString();
	}
	
	public List<Vehicle> getTrucks() {
		return trucks;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public double getFitness() {
		return fitness;
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
		if (trucks.size() < other.trucks.size()) {
			return -1;
		} else if (trucks.size() > other.trucks.size()){
			return 1;
		} else if (fitness < other.fitness) {
			return -1;
		} else if (fitness > other.fitness) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Unit)) {
			return false;
		}
		
		Unit second = (Unit) other;
		
		if (fitness == second.fitness) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return ((Double) fitness).hashCode();
	}
	
	public Unit copy() {
		List<Vehicle> copyTrucks = new ArrayList();
		for (Vehicle t : trucks) {
			copyTrucks.add(t.copy());
		}
		
		return new Unit(copyTrucks, distances, locations);
	}
}
