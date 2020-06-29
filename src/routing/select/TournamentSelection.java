package routing.select;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
		
		int initial = random.nextInt(size);
		Unit best = population.get(initial);
		Set<Integer> set = new HashSet<Integer>();
		set.add(initial);
		
		for (int i = 1; i < tournamentSize; i++) {
			int number = random.nextInt(size);
			if (set.contains(number)) {
				i--;
				continue;
			}
			
			set.add(number);
			Unit current = population.get(number);
			
			if (current.getRank() < best.getRank() ||
				(current.getRank() == best.getRank() 
				 && current.getGroupingDistance() > best.getGroupingDistance())) {
				best = current;
			}
		}
		
		return best;
	}
}
