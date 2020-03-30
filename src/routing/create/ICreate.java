package routing.create;

import java.util.List;
import java.util.Set;

import routing.Unit;

public interface ICreate {
	
	public Unit create();
	
	public Unit create(List<List<Integer>> routes, List<Integer> unvisited);
}
