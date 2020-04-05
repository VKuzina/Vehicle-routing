package routing;

import java.io.IOException;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import routing.create.CVRPCreate;
import routing.create.ICreate;
import routing.cross.ArcCrossover;
import routing.cross.ICrossover;
import routing.cross.NodeCrossover;
import routing.cross.SequenceCrossover;
import routing.evaluate.Evaluator;
import routing.evaluate.IEvaluator;
import routing.mutate.CloseMutator;
import routing.mutate.DetourMutator;
import routing.mutate.IMutation;
import routing.mutate.MiniMutator;
import routing.mutate.RandomNodeMutator;
import routing.select.ISelection;
import routing.select.TournamentSelection;
import routing.select.IndexSelection;

public class UsmjeravanjeVozila {
	private static final int tournamentSize = 3;
	private static final double k3 = 0.5;
	private static final double k1 = 0.0;
	private static final double k2 = 3.0;
	
	public String name = null;
	public int NOTrucks = 0;
	public int dimension = 0;
	public int capacity = 0;
	public Location[] locations = null;
	public int[] demands = null;
	
	long startTime = 0;
	Path data = null;
	int populationSize = 0;
	int NOIterations = 0;
	int withoutImprovement = 0;
	boolean selection = false;
	int mutatedPopulationSize = 0;
	int NOMutatedIterations = 0;
	double mutationProbability = 0;
	
	double[][] distances = null;

	Random random = new Random();
	ISelection selector = new TournamentSelection(tournamentSize, random);
	IndexSelection elimSelector = new IndexSelection(random);
	IEvaluator evaluator = null;
	ICreate creator = null;
	List<ICrossover> crossers = new ArrayList();
	List<IMutation> mutators = new ArrayList();
	List<Unit> population = new ArrayList();
	double p = Double.MAX_VALUE;
	double corr = 0.0;
	int staleCounter = 0;
	double best = 0;

	public void usmjeravanje(String[] args) {
		
		takeOutData(args);
		readInstance();
		setUpOperators();
		setUpInitialPopulation();
		
		evolve();

		double time = ((double) Math
				.round((double) (System.nanoTime() - startTime)
						/ Math.pow(10, 7))) / 100;
		String elim = "elimination";
		if (selection) {
			elim = "selection";
		}

		Path result = Paths.get("./Results/hmo/" + args[0]); 
		String output = population.get(0) + "\n";

		try {
			result.toFile().createNewFile();
			Files.write(result, output.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.err.println("Neuspjelo pisanje u datoteku.");
		}
//		
		System.out.println(population.get(0));
		System.out.println(time);
	}

	public void read(List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.startsWith("NAME")) {
				name = line.split(":")[1].trim();
			} else if (line.startsWith("COMMENT")) {
				NOTrucks = Integer.parseInt(line.split(",")[1].split(":")[1]
						.trim());
			} else if (line.startsWith("DIMENSION")) {
				dimension = Integer.parseInt(line.split(":")[1].trim());
				locations = new Location[dimension];
				demands = new int[dimension];
			} else if (line.startsWith("CAPACITY")) {
				capacity = Integer.parseInt(line.split(":")[1].trim());
			} else if (line.startsWith("NODE_COORD_SECTION")) {
				for (int j = 0; j < dimension; j++) {
					i++;
					line = lines.get(i);

					String[] parts = line.trim().split("\\s+");
					locations[j] = new Location(Integer.parseInt(parts[1]),
							Integer.parseInt(parts[2]), 0, 0, 0);
				}
			} else if (line.startsWith("DEMAND_SECTION")) {
				for (int j = 0; j < dimension; j++) {
					i++;
					line = lines.get(i);

					demands[j] = Integer.parseInt(line.trim().split("\\s+")[1]);
				}
			}
		}
	}
	
	public void readInstance() {
		List<String> lines = null;

		try {
			lines = Files.readAllLines(data);
		} catch (IOException e) {
			System.err.println("�itanje iz dane datoteke nije uspjelo.");
			e.printStackTrace();
			System.exit(0);
		}
		
		dimension = Integer.parseInt(lines.get(lines.size() - 1).split("\\s+")[1]) + 1;
		String[] numCap = lines.get(2).split("\\s+");
		
		NOTrucks = Integer.parseInt(numCap[1]);
		capacity = Integer.parseInt(numCap[2]);
		locations = new Location[dimension];
		demands = new int[dimension];
		
		for (int i = 7; i < lines.size(); i++) {
			String[] line = lines.get(i).split("\\s+");
			
			int index = Integer.parseInt(line[1]);
			locations[index] = new Location(Integer.parseInt(line[2]),
											Integer.parseInt(line[3]),
											Integer.parseInt(line[5]),
											Integer.parseInt(line[6]),
											Integer.parseInt(line[7]));
			demands[index] = Integer.parseInt(line[4]);
		}
	}
	
	public void takeOutData(String[] args) {
		if (args.length != 7 && args.length != 6) {
			System.err
					.println("Potrebno je unijeti �est ili sedam argumenata, od kojih ako se �eli eliminacijska selekcija se predaju samo 6, a ako se �eli obi�na selekcija je potreban jo� 1 argument. Argumenti su redom putanja do datoteke s problemom, veli�ina populacije, broj iteracija, broj iteracija bez poboljsanja za prekid, veli�ina mutirane populacije, broj iteracija za mutiranu populaciju i vjerojatnost mutacije.");
			System.exit(0);
		}

		startTime = System.nanoTime();
		data = Paths.get(args[0]);
		populationSize = Integer.parseInt(args[1]);
		NOIterations = Integer.parseInt(args[2]);
		withoutImprovement = Integer.parseInt(args[3]);
		selection = false;
		mutatedPopulationSize = Integer.parseInt(args[4]);
		NOMutatedIterations = Integer.parseInt(args[5]);
		mutationProbability = 0;
		
		if (args.length == 7) {
			mutationProbability = Double.parseDouble(args[6]);
			selection = true;
		}
	}
	
	public void setUpOperators() {
		distances = Util.calculateEuclidian2D(locations);
		evaluator = new Evaluator(distances);
		creator = new CVRPCreate(NOTrucks, capacity, dimension,
				distances, demands, locations, random);
		
		crossers.add(new SequenceCrossover(creator));
		crossers.add(new ArcCrossover(creator));
		//crossers.add(new NodeCrossover(creator));
		mutators.add(new DetourMutator(random, dimension, distances, creator,
		mutatedPopulationSize));
		mutators.add(new RandomNodeMutator(random, dimension, distances,
				creator, mutatedPopulationSize));
		//mutators.add(new CloseMutator(random, dimension, distances, creator,
		//		mutatedPopulationSize));
		// mutators.add(new MiniMutator(random, dimension, distances, creator,
		// mutatedPopulationSize));
	}
	
	private void setUpInitialPopulation() {
		for (int i = 0; i < populationSize; i++) {
			Unit created = creator.create();
			population.add(created);
			evaluator.evaluate(created);
		}

		Collections.sort(population);
		best = population.get(0).getFitness();
	}
	
	private void evolve() {
		for (int i = 0; i < NOIterations; i++) {
			if (is_stale()) break;
			
			if (selection) {
				selection_algorithm();
			} else {
				elimination_algorithm();
			}
			
			Collections.sort(population); 

			System.out.println(population.get(0).getFitness() + " " + population.get(0).getTrucks().size());
			System.out.println(evaluator.getCount());
		}
	}
	

	private boolean is_stale() {
		staleCounter++;
		if (population.get(0).getFitness() < best) {
			best = population.get(0).getFitness();
			staleCounter = 0;
		}

		if (staleCounter == withoutImprovement) {
			System.out.println("Stale");
			return true;
			
//			List<Unit> tempPopulation = new ArrayList();
//			tempPopulation.add(population.get(0));
//			tempPopulation.add(population.get(1));
//			tempPopulation.add(population.get(2));
//			for (int j = 0; j < populationSize - 3; j++) {
//				Unit created = creator.create();
//				tempPopulation.add(created);
//				evaluator.evaluate(created);
//			}
//			
//			population = tempPopulation;
		}
		
		return false;
	}

	private void selection_algorithm() {
		List<Unit> newPopulation = new ArrayList();

		newPopulation.add(population.get(0));
		newPopulation.add(population.get(1));

		for (int j = 2; j < populationSize / 2; j++) {
			Unit firstParent = selector.select(population);
			Unit secondParent = selector.select(population);

			while (secondParent == firstParent) {
				secondParent = selector.select(population);
			}

			Unit firstChild = crossers.get(
					random.nextInt(crossers.size())).cross(firstParent,
					secondParent);
			Unit secondChild = crossers.get(
					random.nextInt(crossers.size())).cross(
					secondParent, firstParent);

			evaluator.evaluate(firstChild);
			evaluator.evaluate(secondChild);

			newPopulation.add(firstChild);
			newPopulation.add(secondChild);

			if (random.nextDouble() < mutationProbability) {
				List<Unit> mutatedPopulation = mutators.get(
						random.nextInt(mutators.size()))
						.mutatePopulation(firstChild);

				for (Unit u : mutatedPopulation) {
					evaluator.evaluate(u);
				}

				Collections.sort(mutatedPopulation);
				for (int k = 0; k < NOMutatedIterations; k++) {
					List<Unit> newMutatedPopulation = new ArrayList();

					newMutatedPopulation.add(mutatedPopulation.get(0));
					for (int l = 1; l < mutatedPopulationSize / 2; l++) {
						Unit firstMutatedParent = selector
								.select(mutatedPopulation);
						Unit secondMutatedParent = selector
								.select(mutatedPopulation);

						while (secondMutatedParent == firstMutatedParent) {
							secondMutatedParent = selector
									.select(mutatedPopulation);
						}

						Unit firstMutatedChild = crossers.get(
								random.nextInt(crossers.size()))
								.cross(firstMutatedParent,
										secondMutatedParent);
						Unit secondMutatedChild = crossers.get(
								random.nextInt(crossers.size()))
								.cross(secondMutatedParent,
										firstMutatedParent);

						evaluator.evaluate(firstMutatedChild);
						evaluator.evaluate(secondMutatedChild);

						newMutatedPopulation.add(firstMutatedChild);
						newMutatedPopulation.add(secondMutatedChild);

						if (random.nextDouble() < mutationProbability) {
							newMutatedPopulation.add(mutators.get(
									random.nextInt(mutators.size()))
									.mutate(firstMutatedChild));
							l++;
						}
					}

					Collections.sort(newMutatedPopulation);

					mutatedPopulation = newMutatedPopulation;
				}

				newPopulation.add(mutatedPopulation.get(0));
				j++;
			}
		  }
		}
	
	private void elimination_algorithm() {
		double fitSum = 0.0;
		for (Unit u : population) {
			fitSum += u.getFitness();
		}

		double fmax = Math.pow(population.get(0).getFitness(), -1);
		double favg = Math.pow(fitSum / population.size(), -1);

		p = (fmax - favg) / fmax;

		if (p > corr) {
			corr = p;
		}

		mutationProbability = k3 * (corr - p) / corr;

		double exp = k1 + k2 * Math.pow(p / corr, 2);

		for (int j = 0; j < populationSize / 2; j++) {
			Unit first = elimSelector.select(population, exp);
			Unit second = elimSelector.select(population, exp);

			if (second.compareTo(first) < 0) {
				Unit temp = first;
				first = second;
				second = temp;
			}

			Unit third = elimSelector.select(population, exp);

			if (third.compareTo(second) < 0) {
				Unit temp = third;
				third = second;
				second = temp;

				if (second.compareTo(first) < 0) {
					Unit temp2 = first;
					first = second;
					second = temp2;
				}
			}

			population.remove(population.indexOf(third));

			Unit child = crossers.get(random.nextInt(crossers.size()))
					.cross(first, second);

			evaluator.evaluate(child);

			if (random.nextDouble() < mutationProbability) {
				List<Unit> mutatedPopulation = mutators.get(
						random.nextInt(mutators.size()))
						.mutatePopulation(child);

				for (Unit u : mutatedPopulation) {
					evaluator.evaluate(u);
				}

				Collections.sort(mutatedPopulation);
				for (int k = 0; k < NOMutatedIterations; k++) {
					for (int l = 0; l < mutatedPopulationSize / 2; l++) {
						Unit firstMutated = elimSelector.select(
								mutatedPopulation, exp);
						Unit secondMutated = elimSelector.select(
								mutatedPopulation, exp);

						if (secondMutated.compareTo(firstMutated) < 0) {
							Unit temp = firstMutated;
							firstMutated = secondMutated;
							secondMutated = temp;
						}

						Unit thirdMutated = elimSelector.select(
								mutatedPopulation, exp);

						if (thirdMutated.compareTo(secondMutated) < 0) {
							Unit temp = thirdMutated;
							thirdMutated = secondMutated;
							secondMutated = temp;

							if (secondMutated.compareTo(firstMutated) < 0) {
								Unit temp2 = firstMutated;
								firstMutated = secondMutated;
								secondMutated = temp2;
							}
						}

						Unit mutatedChild = crossers.get(
								random.nextInt(crossers.size())).cross(
								firstMutated, secondMutated);

						evaluator.evaluate(mutatedChild);

						mutatedPopulation.remove(mutatedPopulation
								.indexOf(thirdMutated));
						if (random.nextDouble() < mutationProbability) {
							mutatedChild = mutators.get(
									random.nextInt(mutators.size()))
									.mutate(mutatedChild);
							evaluator.evaluate(mutatedChild);
						}

						mutatedPopulation.add(mutatedChild);
					}

					Collections.sort(mutatedPopulation);
				}
				child = mutatedPopulation.get(0);
			}

			population.add(child);
		}
	}

	public static void main(String[] args) {
			for (int i = 0; i < 5; i++) {
				UsmjeravanjeVozila u = new UsmjeravanjeVozila();
				u.usmjeravanje(args);
			}
	}
}
