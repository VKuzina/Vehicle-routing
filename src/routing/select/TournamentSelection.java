package routing.select;

import java.util.List;
import java.util.Random;

import routing.Unit;

public class TournamentSelection implements ISelection{
	private int tournamentSize;
	private Random random;
	
	public TournamentSelection(int tournamentSize, Random random) {
		super();
		this.tournamentSize = tournamentSize;
		this.random = random;
	}

	@Override
	public Unit select(List<Unit> population) {
		int size = population.size();
		
		Unit best = population.get(random.nextInt(size));
		
		for (int i = 1; i < tournamentSize; i++) {
			Unit current = population.get(random.nextInt(size));
			
			if (current.getFitness() < best.getFitness()) {
				best = current;
			}
		}
		
		return best;
	}
}
