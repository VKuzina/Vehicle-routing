package routing;

import routing.Location;

public class Util {
	
	public static double[][] calculateEuclidian2D(Location[] locations) {
		double[][] distances = new double[locations.length][locations.length];
		
		for (int i = 0; i < locations.length; i++) {
			for (int j = 0; j < locations.length; j++) {
				int x1 = locations[i].getX();
				int y1 = locations[i].getY();
				int x2 = locations[j].getX();
				int y2 = locations[j].getY();
				
				double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
				distances[i][j] = distance;
				distances[j][i] = distance;
			}
		}
		
		return distances;
	}
}
