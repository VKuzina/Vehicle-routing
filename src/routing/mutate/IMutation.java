package routing.mutate;

import java.util.List;

import routing.Unit;

public interface IMutation {
	public Unit mutate(Unit unit);
	public List<Unit> mutatePopulation(Unit unit);
}
