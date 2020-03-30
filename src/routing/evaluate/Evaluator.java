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
		
		double fitness = 0;
		
		for (Vehicle t: trucks) {
			List<Integer> route = t.getRoute();
			
			for (int i = 0; i < route.size() - 1; i++) {
				fitness += distances[route.get(i)][route.get(i + 1)];
			}
		}
		
		unit.setFitness(fitness);
	}
	
	@Override
	public int getCount() {
		return count;
	}

}
