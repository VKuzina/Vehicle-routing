package routing.cross;

import java.util.ArrayList;
import java.util.List;

import routing.Unit;
import routing.create.ICreate;

public class ArcCrossover implements ICrossover{
	private ICreate creator;
	
	public ArcCrossover(ICreate creator) {
		this.creator = creator;
	}
	
	@Override
	public Unit cross(Unit firstParent, Unit secondParent) {
		List<List<Integer>> firstRoutes = firstParent.copy().getRoutes();
		List<List<Integer>> secondRoutes = secondParent.getRoutes();
		
		List<Integer> removed = new ArrayList();
		for (List<Integer> route : firstRoutes) {
			for (int i = 1; i < route.size() - 1; i++) {
				if (!isArcContained(route.get(i), route.get(i + 1), secondRoutes)) {
					removed.add(route.get(i));
					route.remove(i);
					i--;
				}
			}
		}
		
		return creator.create(firstRoutes, removed);
	}

	private boolean isArcContained(Integer start, Integer finish, List<List<Integer>> routes) {
		for (List<Integer> route : routes) {
			for (int i = 1; i < route.size() - 1; i++) {
				if (route.get(i) == finish) {
					return false;
				}
				
				if (route.get(i) == start) {
					if (route.get(i + 1) == finish) {
						return true;
					}
					
					return false;
				}
			}
		}
		
		return false;
	}

}
