package routing.pareto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import routing.Unit;

public class ParetoSeparator {
	private int defaultSize;
	
	public ParetoSeparator(int size) {
		this.defaultSize = size;
	}
	
	public List<List<Unit>> separate(List<Unit> population) {
		int size = population.size();
		int[] dominated = new int[size];
		List<Unit>[] dominate = new ArrayList[size];
		List<List<Unit>> fronts = new ArrayList<List<Unit>>();
		List<Unit> currentFront = new ArrayList<Unit>();
	
		for (int i = 0; i < size; i++) {
			List<Unit> dominates = dominate[i] = new ArrayList<Unit>();
			Unit first = population.get(i);
			first.setCurrentIndex(i);
			
			for (int j = 0; j < size; j++) {
				if (i == j) continue;
				
				Unit second = population.get(j);
				if (first.dominates(second)) dominates.add(second);
				if (first.is_dominated(second)) dominated[i]++;
			}
			
			if (dominated[i] == 0) {
				currentFront.add(first);
			}
		}
		
		while (!currentFront.isEmpty()) {
			fronts.add(currentFront);
			List<Unit> nextFront = new ArrayList<Unit>();

			for (Unit current : currentFront) {
				List<Unit> dominates = dominate[current.getCurrentIndex()];

				for (Unit dominatedUnit : dominates) {
					if (--dominated[dominatedUnit.getCurrentIndex()] == 0) {
						nextFront.add(dominatedUnit);
						dominatedUnit.setRank(fronts.size());
					}
				}
			}
			
			currentFront = nextFront;
		}
		calculateGroupingDistance(fronts);
		
		return extinction(fronts);
	}
	
	private void calculateGroupingDistance(List<List<Unit>> fronts) {
		for (List<Unit> front : fronts) {
			front.sort((u1, u2) -> ((Double)u1.getDistance()).compareTo(u2.getDistance()));
			front.get(0).setGroupingDistance(Double.MAX_VALUE);
			front.get(front.size() - 1).setGroupingDistance(Double.MAX_VALUE);
			Double max = front.get(front.size() - 1).getDistance();
			Double min = front.get(0).getDistance();

			for (int i = 1; i < front.size() - 1; i++) {
				Unit previous = front.get(i - 1);
				Unit current = front.get(i);
				Unit next = front.get(i + 1);
				if (current.getGroupingDistance() == Double.MAX_VALUE) continue;
				
				current.setGroupingDistance(current.getGroupingDistance() +
										    (next.getDistance() - previous.getDistance()) /
										    (max - min));
			}
		}
		
		for (List<Unit> front : fronts) {
			front.sort((u1, u2) -> ((Double)u1.getBalance()).compareTo(u2.getBalance()));
			front.get(0).setGroupingDistance(Double.MAX_VALUE);
			front.get(front.size() - 1).setGroupingDistance(Double.MAX_VALUE);
			Double max = front.get(front.size() - 1).getBalance();
			Double min = front.get(0).getBalance();

			for (int i = 1; i < front.size() - 1; i++) {
				Unit previous = front.get(i - 1);
				Unit current = front.get(i);
				Unit next = front.get(i + 1);
				if (current.getGroupingDistance() == Double.MAX_VALUE) continue;
				
				current.setGroupingDistance(current.getGroupingDistance() +
										    (next.getBalance() - previous.getBalance()) /
										    (max - min));
			}
		}
	}
	
	private List<List<Unit>> extinction(List<List<Unit>> fronts) {
		List<List<Unit>> survivingPopulation = new ArrayList<List<Unit>>();
		int currentSize = 0;
		
		for (List<Unit> front : fronts) {
			if (currentSize + front.size() <= defaultSize) {
				survivingPopulation.add(front);
				currentSize += front.size();
			} else {
				List<Unit> partialFront = new ArrayList<Unit>();
				front.sort((u1, u2) -> ((Double)u2.getGroupingDistance()).compareTo(u1.getGroupingDistance()));
				for (Unit u : front) {
					if (currentSize >= defaultSize) break;
					
					partialFront.add(u);
					currentSize++;
				}
				
				survivingPopulation.add(partialFront);
			}
			
			if (currentSize == defaultSize) {
				break;
			}
		}
		
		return survivingPopulation;
	}
	
	public List<Unit> group(List<List<Unit>> fronts) {
		List<Unit> population = new ArrayList<Unit>();
		
		for (List<Unit> front : fronts) population.addAll(front);
		
		return population;
	}
}
