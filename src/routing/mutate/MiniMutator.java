package routing.mutate;

import java.util.List;
import java.util.Random;

import routing.create.ICreate;

public class MiniMutator extends AbstractMutator{

	public MiniMutator(Random random, int dimension, double[][] distances, ICreate creator, int mutationPopulationSize) {
		super(random, dimension, distances, creator, mutationPopulationSize);
	}

	@Override
	public void throwout(List<List<Integer>> routes, List<Integer> throwouts) {
		int toRemove = random.nextInt(dimension - 1) + 1;
		
		throwouts.add(toRemove);

		remove(routes, toRemove);
	}
}
