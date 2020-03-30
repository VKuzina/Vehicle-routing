package routing.cross;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import routing.Unit;
import routing.create.ICreate;

public class SequenceCrossover implements ICrossover{
	private ICreate creator;
	
	public SequenceCrossover(ICreate creator) {
		this.creator = creator;
	}
	
	@Override
	public Unit cross(Unit firstParent, Unit secondParent) {
		List<List<Integer>> firstRoutes = firstParent.getRoutes();
		List<List<Integer>> secondRoutes = secondParent.getRoutes();
		List<List<Integer>> childRoutes = new ArrayList();
		List<Integer> unvisited = new ArrayList();
		
		for (List<Integer> firstRoute: firstRoutes) {
			List<Integer> mostCommon = mostCommonNodes(firstRoute, secondRoutes);
			List<Integer> commonNodes = new ArrayList();
			
			for (Integer i : firstRoute) {
				if (i == 0) {
					continue;
				}
				
				if (mostCommon.contains(i)) {
					commonNodes.add(i);
				} else {
					unvisited.add(i);
				}
			}
			
			if (commonNodes.size() > 1) {
				childRoutes.add(commonNodes);
			} else if(commonNodes.size() == 1) {
				unvisited.add(commonNodes.get(0));
			}
		}
		
		return creator.create(childRoutes, unvisited);
	}
	
	
	private List<Integer> mostCommonNodes(List<Integer> route, List<List<Integer>> comparingRoutes) {
		int index = -1;
		int NOCommon = 0;
		
		for (int i = 0; i < comparingRoutes.size(); i++) {
			List<Integer> comparingRoute = comparingRoutes.get(i);
			
			int common = 0;
			if ((common = NOCommonNodes(route, comparingRoute)) >= NOCommon) {
				NOCommon = common;
				index = i;
			}
		}
		
		return comparingRoutes.get(index);
	}
	
	private int NOCommonNodes(List<Integer> firstRoute, List<Integer> secondRoute) {
		int NOCommon = 0;
		
		for (Integer i : firstRoute) {
			if (i == 0) {
				continue;
			}
			
			if (secondRoute.contains(i)) {
				NOCommon++;
			}
		}
		
		return NOCommon;
	}
}
