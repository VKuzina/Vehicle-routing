package routing.mutate;

import java.util.List;
import java.util.Random;

import routing.create.ICreate;

public class RandomNodeMutator extends AbstractMutator {

	public RandomNodeMutator(Random random, int dimension, double[][] distances, ICreate creator,
			int mutationPopulationSize) {
		super(random, dimension, distances, creator, mutationPopulationSize);
	}

	@Override
	public void throwout(List<List<Integer>> routes, List<Integer> throwouts) {
		int limit = (int) (random.nextDouble() * 0.5 * dimension);
		
		for (int i = 0; i < limit; i++) {
			int toRemove = random.nextInt(dimension - 1) + 1;
			while (throwouts.contains(toRemove)) {
				toRemove = random.nextInt(dimension - 1) + 1;
			}
			throwouts.add(toRemove);

			remove(routes, toRemove);
		}
	}
}
