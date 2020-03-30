package routing;

import java.util.ArrayList;

import java.util.List;


public class Vehicle {
	private int capacity;
	private int takenCapacity;
	private List<Integer> route;
	private int leastIndex;
	private double[][] distances;
	private int[] demands;
	private Location[] locations;
	
	public Vehicle(int capacity, double[][] distances, int[] demands, Location[] locations) {
		this.capacity = capacity;
		this.takenCapacity = 0;
		this.route = new ArrayList();
		route.add(0);
		route.add(0);
		this.distances = distances;
		this.leastIndex = 0;
		this.demands = demands;
		this.locations = locations;
	}
	
	public Vehicle(int capacity, double[][] distances, List<Integer> route, int[] demands, Location[] locations) {
		this.capacity = capacity;
		this.takenCapacity = 0;
		this.route = new ArrayList();
		this.route.add(0);
		for (Integer i : route) {
			if (i == 0) {
				continue;
			}
			this.route.add(i);
			this.takenCapacity += demands[i];
		}
		this.route.add(0);
		this.distances = distances;
		this.leastIndex = 0;
		this.demands = demands;
		this.locations = locations;
	}
	
	public boolean canVisit(int packageCapacity) {
		if (takenCapacity + packageCapacity > capacity) {
			return false;
		}
		
		return true;
	}
	
	public double getLowestDistanceForInsertion(int indexOfLocation) {
		double leastDistance = Double.MAX_VALUE;
		
		for (int i = 0; i < route.size() - 1; i++) {
			int firstIndex = route.get(i);
			int secondIndex = route.get(i + 1);
			
			double distance = distances[firstIndex][indexOfLocation] + distances[secondIndex][indexOfLocation] - distances[firstIndex][secondIndex];
			if (leastDistance > distance && checkRoute(firstIndex, indexOfLocation)) {
				leastDistance = distance;
				leastIndex = i;
			}
		}
		
		return leastDistance;
	}
	
	public double getLowestStartForInsertion(int indexOfLocation) {
		double leastDistance = Double.MAX_VALUE;
		
		for (int i = 0; i < route.size() - 1; i++) {
			int firstIndex = route.get(i);
			int secondIndex = route.get(i + 1);
			
			double distance = distances[firstIndex][indexOfLocation] + distances[secondIndex][indexOfLocation] - distances[firstIndex][secondIndex];
			if (leastDistance > distance && checkRoute(firstIndex, indexOfLocation)) {
				leastDistance = distance;
				leastIndex = i;
			}
		}
		
		return leastDistance;
	}
	
	private boolean checkRoute(int change, int indexOfLocation) {
		int count = 0;
		for (int i = 0; i < route.size() - 1; i++) {
			int current = route.get(i);
			int next = route.get(i + 1);
			
			if (current == change) {
				count = calcCount(count, current, indexOfLocation);
				if (count == -1) return false;
				
				count = calcCount(count, indexOfLocation, next);
				if (count == -1) return false;
			} else {
				count = calcCount(count, current, next);
				if (count == -1) return false;
			}
		}
		
		return true;
	}
	
	private int calcCount(int count, int current, int next) {
		count += Math.ceil(distances[current][next]);
		Location loc = locations[next];
		
		if (loc.getStop() < loc.getTime() + count) {
			return -1;
		}
		
		return Math.max(count, loc.getStart()) + loc.getTime();
	}
	
	public void insertIntoBestArc(int indexOfLocation, int packageCapacity) {
		this.takenCapacity += packageCapacity;
		route.add(leastIndex + 1, indexOfLocation);
	}
	
	public List<Integer> getRoute() {
		return route;
	}
	
	public Vehicle copy() {
		return new Vehicle(capacity, distances, route, demands, locations);
	}
}
