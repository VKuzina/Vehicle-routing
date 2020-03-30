package routing.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import routing.Vehicle;
import routing.Location;
import routing.Unit;

public class CVRPCreate implements ICreate{
	private int NOTrucks;
	private int truckCapacity;
	private int dimension;
	private double[][] distances;
	private int[] demands;
	private Random random;
	private Location[] locations;
	
	
	public CVRPCreate(int nOTrucks, int truckCapacity, int dimension, double[][] distances, int[] demands, Location[] locations2, Random random) {
		super();
		NOTrucks = nOTrucks;
		this.truckCapacity = truckCapacity;
		this.dimension = dimension;
		this.distances = distances;
		this.demands = demands;
		this.random = random;
		this.locations = locations2;
	}
	
	@Override
	public Unit create() {
		List<Integer> notVisited = new ArrayList();
		for (int i = 0; i < dimension; i++) {
			notVisited.add(i);
		}
		
		List<Vehicle> trucks = new ArrayList();
		for (int i = 0; i < dimension - 1; i++) {
			int randomIndex = random.nextInt(notVisited.size() - 1) + 1;
			int index = notVisited.get(randomIndex);
			
			notVisited.remove(randomIndex);
			
			try {
				insert(trucks, index);
			} catch (IllegalArgumentException e) {
				return create();
			}
		}
		
		return new Unit(trucks, distances, locations);
	}
	
	public Unit create(List<List<Integer>> routes, List<Integer> unvisited) {
		List<Vehicle> trucks = new ArrayList();
		for (List<Integer> route : routes) {
			Vehicle t = new Vehicle(truckCapacity, distances, route, demands, locations);
			/*for (Integer i : route) {
				if (i == 0) {
					continue;
				}
				if (!t.canVisit(demands[i])) {
					throw new IllegalArgumentException("Nisam još implementirao");
				}
				
				t.getLowestDistanceForInsertion(i);
				t.insertIntoBestArc(i, demands[i]);
			}*/
			trucks.add(t);
		}
		
		while (unvisited.size() > 0) {
			int randomIndex = random.nextInt(unvisited.size());
			int index = unvisited.get(randomIndex);
			
			unvisited.remove(randomIndex);
			
			try {
				insert(trucks, index);
			} catch (IllegalArgumentException e) {
				return create();
			}
		}
		
		return new Unit(trucks, distances, locations);
	}
	
	private void insert(List<Vehicle> trucks, int index) {
		double leastDistance = Double.MAX_VALUE;
		Vehicle best = null;
		for (Vehicle truck : trucks) {
			if (truck.canVisit(demands[index])) {
				double lowestDistance = truck.getLowestDistanceForInsertion(index);
				if (leastDistance > lowestDistance) {
					leastDistance = lowestDistance;
					best = truck;
				}
			}
		}
		
		if (leastDistance == Double.MAX_VALUE) {
			if (trucks.size() == NOTrucks) {
				//System.out.println("STVARANJE NIJE USPJELO!!!");
				throw new IllegalArgumentException();
			}
			
			best = new Vehicle(truckCapacity, distances, demands, locations);
			trucks.add(best);
			if (!best.canVisit(demands[index])) {
				throw new IllegalArgumentException("Nisam još implementirao");
			}
		}
		
		best.insertIntoBestArc(index, demands[index]);
	}

}
