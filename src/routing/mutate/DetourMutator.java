package routing.mutate;

import java.util.List;
import java.util.Random;

import routing.create.ICreate;

public class DetourMutator extends AbstractMutator{

	public DetourMutator(Random random, int dimension, double[][] distances, ICreate creator, int mutationPopulationSize) {
		super(random, dimension, distances, creator, mutationPopulationSize);
	}

	@Override
	public void throwout(List<List<Integer>> routes, List<Integer> throwouts) {
		int limit = (int) (random.nextDouble() * 0.5 * dimension);
		
		for (int i = 0; i < limit; i++) {
			int toRemove = -1;
			double maxDistance = 0;

			for (List<Integer> route : routes) {
				for (int j = 1; j < route.size() - 1; j++) {
					double previous = distances[j - 1][j];
					double next = distances[j][j + 1];
					double detour = distances[j - 1][j + 1];
					
					double dist = next + previous - detour;
					
					if (dist > maxDistance) {
						maxDistance = dist;
						toRemove = j;
					}
				}
			}

			throwouts.add(toRemove);
			remove(routes, toRemove);
		}
	}

}
