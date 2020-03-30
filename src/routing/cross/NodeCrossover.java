package routing.cross;

import java.util.ArrayList;
import java.util.List;

import routing.Unit;
import routing.create.ICreate;

public class NodeCrossover implements ICrossover{
	private ICreate creator;
	
	public NodeCrossover(ICreate creator) {
		this.creator = creator;
	}
	
	@Override
	public Unit cross(Unit firstParent, Unit secondParent) {
		List<List<Integer>> firstRoutes = firstParent.copy().getRoutes();
		List<List<Integer>> secondRoutes = secondParent.getRoutes();
		
		List<List<Integer>> tempRoutes = new ArrayList();
		
		for (int i = 0; i < firstRoutes.size(); i++) {
			List<Integer> tempRoute = new ArrayList();
			tempRoute.add(0);
			tempRoutes.add(tempRoute);
		}
		
		for (List<Integer> route : secondRoutes) {
			for (Integer i : route) {
				if (i == 0) {
					continue;
				}
				
				tempRoutes.get(getIndex(i, firstRoutes)).add(i);
			}
		}
		
		int index = -1;
		int maxNodes = 0;
		
		for (int i = 0; i < tempRoutes.size(); i++) {
			List<Integer> route = tempRoutes.get(i);
			
			if (route.size() > maxNodes) {
				maxNodes = route.size();
				index = i;
			}
		}
		
		List<List<Integer>> childRoutes = new ArrayList();
		List<Integer> best = tempRoutes.get(index);
		best.add(0);
		childRoutes.add(best);
		List<Integer> removed = new ArrayList();
		
		for (int i = 0; i < tempRoutes.size(); i++) {
			if (i == index) {
				continue;
			}
			
			List<Integer> route = tempRoutes.get(i);
			for (Integer j : route) {
				removed.add(j);
			}
		}
		
		return creator.create(childRoutes, removed);
	}

	private int getIndex(Integer i, List<List<Integer>> firstRoutes) {
		for (int j = 0; j < firstRoutes.size(); j++) {
			List<Integer> route = firstRoutes.get(j);
			
			for (int k = 1; k < route.size() - 1; k++) {
				if (route.get(k) == i) {
					return j;
				}
			}
		}
		
		return -1;
	}

}
