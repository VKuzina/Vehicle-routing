package routing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Zbroji {
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(args[0]));
		int sum = 0;
		for (String line : lines) {
			sum += Integer.parseInt(line);
//			if (Integer.parseInt(line) == 1034) {
//				System.out.println("NAJBOLJE!!!!!");
//			}
		}
		
		System.out.println((sum / 30) - 1167);
	}
}
