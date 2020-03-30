package routing.mutate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import routing.Unit;
import routing.create.ICreate;

public abstract class AbstractMutator implements IMutation{
	
	protected Random random;
	protected int dimension;
	protected double[][] distances;
	protected ICreate creator;
	protected int mutationPopulationSize;

	public AbstractMutator(Random random, int dimension, double[][] distances, ICreate creator, int mutationPopulationSize) {
		this.random = random;
		this.dimension = dimension;
		this.distances = distances;
		this.creator = creator;
		this.mutationPopulationSize = mutationPopulationSize;
	}
	
	public void remove(List<List<Integer>> routes, int toRemove) {
		for (List<Integer> route : routes) {
			if (route.contains(toRemove)) {
				route.remove(route.indexOf(toRemove));
			}
		}
	}
	
	@Override
	public List<Unit> mutatePopulation(Unit unit) {
		unit = unit.copy();
		List<Unit> mutationPopulation = new ArrayList();
		List<List<Integer>> routes = unit.getRoutes();
		List<Integer> throwouts = new ArrayList();
		
		throwout(routes, throwouts);
		for (int i = 0; i < mutationPopulationSize; i++) {
			Unit created = creator.create(routes, new ArrayList(throwouts));
			mutationPopulation.add(created);
		}

		return mutationPopulation;
	}

	public Unit mutate(Unit unit) {
		unit = unit.copy();
		List<Unit> mutationPopulation = new ArrayList();
		List<List<Integer>> routes = unit.getRoutes();
		List<Integer> throwouts = new ArrayList();
		
		throwout(routes, throwouts);
		Unit created = creator.create(routes, throwouts);

		return created;
	}
	
	public abstract void throwout(List<List<Integer>> routes, List<Integer> throwouts);
}
