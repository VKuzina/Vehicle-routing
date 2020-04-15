package routing.evaluate;

import java.util.List;

import routing.Vehicle;
import routing.Unit;

public class Evaluator implements IEvaluator{
	private double[][] distances;
	private int count = 0;
	
	public Evaluator(double[][] distances) {
		this.distances = distances;
	}
	
	@Override
	public void evaluate(Unit unit) {
		count++;
		List<Vehicle> trucks = unit.getTrucks();
		
		double totalDistance = 0;
		double maxVehicleDistance = 0;
		double minVehicleDistance = Double.MAX_VALUE;
		
		for (Vehicle t: trucks) {
			List<Integer> route = t.getRoute();
			double routeDistance = 0;
			
			for (int i = 0; i < route.size() - 1; i++) {
				routeDistance += distances[route.get(i)][route.get(i + 1)];
			}
			
			maxVehicleDistance = Math.max(maxVehicleDistance, routeDistance);
			minVehicleDistance = Math.min(minVehicleDistance, routeDistance);

			totalDistance += routeDistance;
		}
		
		double balance = (maxVehicleDistance - minVehicleDistance) / (totalDistance / distances.length);
		
		unit.setDistance(totalDistance);
		unit.setBalance(balance);
	}
	
	@Override
	public int getCount() {
		return count;
	}

}
