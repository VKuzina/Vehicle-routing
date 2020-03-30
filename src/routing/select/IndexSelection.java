package routing.select;

import java.util.List;
import java.util.Random;

import routing.Unit;

public class IndexSelection {
	private Random random;
	
	public IndexSelection(Random random) {
		this.random = random;
	}

	public Unit select(List<Unit> population, double exp) {
		return population.get((int) ((population.size() - 1) * Math.pow(random.nextDouble(), exp)));
	}
}
	