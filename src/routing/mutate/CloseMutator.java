package routing.mutate;


import java.util.List;
import java.util.Random;
import routing.create.ICreate;

public class CloseMutator extends AbstractMutator{

	public CloseMutator(Random random, int dimension, double[][] distances, ICreate creator, int mutationPopulationSize) {
		super(random, dimension, distances, creator, mutationPopulationSize);
	}

	public void throwout(List<List<Integer>> routes, List<Integer> throwouts) {
		int limit = (int) (random.nextDouble() * 0.5 * dimension);

		int firstRemoved = random.nextInt(dimension - 1) + 1;
		throwouts.add(firstRemoved);

		remove(routes, firstRemoved);

		for (int i = 1; i < limit; i++) {
			int toRemove = -1;
			double minDist = Double.MAX_VALUE;

			for (int j = 1; j < dimension; j++) {
				if (!throwouts.contains(j)) {
					if (minDist > distances[firstRemoved][j]) {
						toRemove = j;
						minDist = distances[firstRemoved][j];
					}
				}
			}

			throwouts.add(toRemove);
			remove(routes, toRemove);
		}
	}
}
