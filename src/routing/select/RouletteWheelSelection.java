package routing.select;

import java.util.List;
import java.util.Random;

import routing.Unit;

public class RouletteWheelSelection implements ISelection{
	private Random random;
	
	public RouletteWheelSelection(Random random) {
		this.random = random;
	}
	
	@Override
	public Unit select(List<Unit> population) {
		int sum = 0;
		double fitsum = 0;
		for (Unit u: population) {
			sum += u.getFitness();
		}
		
		for (Unit u: population) {
			fitsum += 1 - (u.getFitness() / ((double)sum)) ;
		}
		
		double generated = random.nextDouble();
		double accumulation = 0;
		
		for (Unit u: population) {
			double probability = (1 - (u.getFitness()/(double) sum)) / fitsum;
			if (accumulation + probability > generated) {
				return u;
			}
			
			accumulation += probability;
		}
		
		return population.get(population.size() - 1);
	}

}
