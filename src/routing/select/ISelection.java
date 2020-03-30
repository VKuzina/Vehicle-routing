package routing.select;

import java.util.List;

import routing.Unit;

public interface ISelection {
	public Unit select(List<Unit> population);
}
