package routing;

import java.awt.BorderLayout;
import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import routing.create.CVRPCreate;
import routing.create.ICreate;
import routing.cross.ArcCrossover;
import routing.cross.ICrossover;
import routing.cross.NodeCrossover;
import routing.cross.SequenceCrossover;
import routing.draw.Algorithm;
import routing.draw.Parameter;
import routing.evaluate.Evaluator;
import routing.evaluate.IEvaluator;
import routing.mutate.CloseMutator;
import routing.mutate.DetourMutator;
import routing.mutate.IMutation;
import routing.mutate.MiniMutator;
import routing.mutate.RandomNodeMutator;
import routing.pareto.ParetoSeparator;
import routing.select.ISelection;
import routing.select.TournamentSelection;
import routing.select.IndexSelection;

public class UsmjeravanjeVozila extends JFrame {
	private static final double k3 = 0.5;
	private static final double k1 = 0.0;
	private static final double k2 = 3.0;

	private static final int startingWidth = 1000;
	private static final int startingHeight = 500;
	private static final int startingPositionX = 100;
	private static final int startingPositionY = 100;

	private JTabbedPane tabbedPane;
	private JProgressBar progressBar;

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
	int tournamentSize = 0;
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
	ParetoSeparator separator = null;
	List<ICrossover> crossers = new ArrayList();
	List<IMutation> mutators = new ArrayList();
	List<Unit> population = new ArrayList();
	double p = Double.MAX_VALUE;
	double corr = 0.0;
	int staleCounter = 0;
	List<List<Unit>> fronts = null;

	JTextArea output = null;
	JButton start = null;

	public void usmjeravanje(String arg, int broj, int drugibroj) {
		populationSize = 60;
		NOIterations = 2500;
		tournamentSize = 3;
		mutatedPopulationSize = 5;
		NOMutatedIterations = 5;
		mutationProbability = 0.0;
		data = Paths.get(arg);
		// takeOutData(arg);
		readInstance();
		setUpOperators(drugibroj);
		setUpInitialPopulation();

		evolve();

		double time = ((double) Math.round((double) (System.nanoTime() - startTime) / Math.pow(10, 7))) / 100;

		Path result = Paths.get("./Results/hmo/mut/o" + broj);
		StringJoiner sj = new StringJoiner("\n");
		sj.add("\n");
		for (Unit u : fronts.get(0)) {
			sj.add(u.justDataOutput());
		}
		
		String output = sj.toString();

		try {
			result.toFile().createNewFile();
			Files.write(result, output.getBytes(), StandardOpenOption.APPEND);
			System.out.println("zapisano");
		} catch (IOException e) {
			System.err.println("Neuspjelo pisanje u datoteku.");
		}
	}

	public void read(List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.startsWith("NAME")) {
				name = line.split(":")[1].trim();
			} else if (line.startsWith("COMMENT")) {
				NOTrucks = Integer.parseInt(line.split(",")[1].split(":")[1].trim());
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
					locations[j] = new Location(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), 0, 0, 0);
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
			locations[index] = new Location(Integer.parseInt(line[2]), Integer.parseInt(line[3]),
					Integer.parseInt(line[5]), Integer.parseInt(line[6]), Integer.parseInt(line[7]));
			demands[index] = Integer.parseInt(line[4]);
		}
	}

	public void takeOutData(String[] args) {
		if (args.length != 7 && args.length != 6) {
			System.err.println(
					"Potrebno je unijeti �est ili sedam argumenata, od kojih ako se �eli eliminacijska selekcija se predaju samo 6, a ako se �eli obi�na selekcija je potreban jo� 1 argument. Argumenti su redom putanja do datoteke s problemom, veli�ina populacije, broj iteracija, broj iteracija bez poboljsanja za prekid, veli�ina mutirane populacije, broj iteracija za mutiranu populaciju i vjerojatnost mutacije.");
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

	public void setUpOperators(int j) {
		distances = Util.calculateEuclidian2D(locations);
		evaluator = new Evaluator(distances);
		separator = new ParetoSeparator(populationSize);
		creator = new CVRPCreate(NOTrucks, capacity, dimension, distances, demands, locations, random);
		
		
		crossers.add(new SequenceCrossover(creator));
		crossers.add(new ArcCrossover(creator));
		// crossers.add(new NodeCrossover(creator));
		
		mutators.add(new DetourMutator(random, dimension, distances, creator, mutatedPopulationSize));
		mutators.add(new RandomNodeMutator(random, dimension, distances, creator, mutatedPopulationSize));
		// mutators.add(new CloseMutator(random, dimension, distances, creator, mutatedPopulationSize));
		// mutators.add(new MiniMutator(random, dimension, distances, creator, mutatedPopulationSize));
	}

	private void setUpInitialPopulation() {
		for (int i = 0; i < populationSize; i++) {
			Unit created = creator.create();
			population.add(created);
			evaluator.evaluate(created);
		}

		fronts = separator.separate(population);
	}

	private void evolve() {
		for (int i = 0; i < NOIterations; i++) {
//			if (is_stale())
//				break;

			List<Unit> newPopulation = selection_algorithm();

			population.addAll(newPopulation);
			
			population = new ArrayList<Unit>(new HashSet<Unit>(population));

			fronts = separator.separate(population);

			population = separator.group(fronts);
			
			//System.out.println(prettyOutput(fronts));
			if (i % 10 == 0) {
				System.out.println(i);
				output.setText(prettyOutput(fronts));
			}
			
			int percent = (int) ((i / (double) (NOIterations - 1)) * 100);
			progressBar.setValue(percent);
			progressBar.setString(percent + "%");
		}
		
		start.setEnabled(true);
	}

	private String prettyOutput(List<List<Unit>> fronts) {
		StringJoiner sj = new StringJoiner("\n");
		sj.add("front");
		List<Unit> sorted = fronts.get(0);
		Collections.sort(sorted, (u1, u2) -> ((Double)u1.getDistance()).compareTo(u2.getDistance()));
		for (Unit u : sorted) {
			sj.add(u.toString());
		}

		return sj.toString();
	}

	private boolean is_stale() {
		staleCounter++;
//		if (population.get(0).getFitness() < best) {
//			best = population.get(0).getFitness();
//			staleCounter = 0;
//		}

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

	private List<Unit> selection_algorithm() {
		List<Unit> newPopulation = new ArrayList();

		for (int j = 0; j < populationSize / 2; j++) {
			Unit firstParent = selector.select(population);
			Unit secondParent = selector.select(population);

			while (secondParent == firstParent) {
				secondParent = selector.select(population);
			}

			Unit firstChild = crossers.get(random.nextInt(crossers.size())).cross(firstParent, secondParent);
			Unit secondChild = crossers.get(random.nextInt(crossers.size())).cross(secondParent, firstParent);

			evaluator.evaluate(firstChild);
			evaluator.evaluate(secondChild);
			
			newPopulation.add(firstChild);
			newPopulation.add(secondChild);

			if (random.nextDouble() < mutationProbability) {
				List<Unit> mutatedPopulation = mutators.get(random.nextInt(mutators.size()))
						.mutatePopulation(firstChild);

				for (Unit u : mutatedPopulation) {
					evaluator.evaluate(u);
				}

				List<List<Unit>> mutatedFronts = separator.separate(mutatedPopulation);
				for (int k = 0; k < NOMutatedIterations; k++) {
					List<Unit> newMutatedPopulation = new ArrayList();

					for (int l = 0; l < mutatedPopulationSize / 2; l++) {
						Unit firstMutatedParent = selector.select(mutatedPopulation);
						Unit secondMutatedParent = selector.select(mutatedPopulation);

						while (secondMutatedParent == firstMutatedParent) {
							secondMutatedParent = selector.select(mutatedPopulation);
						}

						Unit firstMutatedChild = crossers.get(random.nextInt(crossers.size())).cross(firstMutatedParent,
								secondMutatedParent);
						Unit secondMutatedChild = crossers.get(random.nextInt(crossers.size()))
								.cross(secondMutatedParent, firstMutatedParent);

						newMutatedPopulation.add(firstMutatedChild);
						newMutatedPopulation.add(secondMutatedChild);

						if (random.nextDouble() < mutationProbability) {
							Unit doubleMutetedChild = mutators.get(random.nextInt(mutators.size()))
									.mutate(firstMutatedChild);
							newMutatedPopulation.add(doubleMutetedChild);
							l++;
							evaluator.evaluate(doubleMutetedChild);
						}

						evaluator.evaluate(firstMutatedChild);
						evaluator.evaluate(secondMutatedChild);
					}

					mutatedPopulation.addAll(newMutatedPopulation);
					mutatedFronts = separator.separate(mutatedPopulation);
					mutatedPopulation = separator.group(mutatedFronts);
				}
				
				if (mutatedPopulationSize != 0) {
				newPopulation.add(mutatedFronts.get(0).get(random.nextInt(mutatedFronts.get(0).size())));
				j++;
				}
			}
		}

		return newPopulation;
	}

//	private void elimination_algorithm() {
//		double fitSum = 0.0;
//		for (Unit u : population) {
//			fitSum += u.getFitness();
//		}
//
//		double fmax = Math.pow(population.get(0).getFitness(), -1);
//		double favg = Math.pow(fitSum / population.size(), -1);
//
//		p = (fmax - favg) / fmax;
//
//		if (p > corr) {
//			corr = p;
//		}
//
//		mutationProbability = k3 * (corr - p) / corr;
//
//		double exp = k1 + k2 * Math.pow(p / corr, 2);
//
//		for (int j = 0; j < populationSize / 2; j++) {
//			Unit first = elimSelector.select(population, exp);
//			Unit second = elimSelector.select(population, exp);
//
//			if (second.compareTo(first) < 0) {
//				Unit temp = first;
//				first = second;
//				second = temp;
//			}
//
//			Unit third = elimSelector.select(population, exp);
//
//			if (third.compareTo(second) < 0) {
//				Unit temp = third;
//				third = second;
//				second = temp;
//
//				if (second.compareTo(first) < 0) {
//					Unit temp2 = first;
//					first = second;
//					second = temp2;
//				}
//			}
//
//			population.remove(population.indexOf(third));
//
//			Unit child = crossers.get(random.nextInt(crossers.size())).cross(first, second);
//
//			evaluator.evaluate(child);
//
//			if (random.nextDouble() < mutationProbability) {
//				List<Unit> mutatedPopulation = mutators.get(random.nextInt(mutators.size())).mutatePopulation(child);
//
//				for (Unit u : mutatedPopulation) {
//					evaluator.evaluate(u);
//				}
//
//				Collections.sort(mutatedPopulation);
//				for (int k = 0; k < NOMutatedIterations; k++) {
//					for (int l = 0; l < mutatedPopulationSize / 2; l++) {
//						Unit firstMutated = elimSelector.select(mutatedPopulation, exp);
//						Unit secondMutated = elimSelector.select(mutatedPopulation, exp);
//
//						if (secondMutated.compareTo(firstMutated) < 0) {
//							Unit temp = firstMutated;
//							firstMutated = secondMutated;
//							secondMutated = temp;
//						}
//
//						Unit thirdMutated = elimSelector.select(mutatedPopulation, exp);
//
//						if (thirdMutated.compareTo(secondMutated) < 0) {
//							Unit temp = thirdMutated;
//							thirdMutated = secondMutated;
//							secondMutated = temp;
//
//							if (secondMutated.compareTo(firstMutated) < 0) {
//								Unit temp2 = firstMutated;
//								firstMutated = secondMutated;
//								secondMutated = temp2;
//							}
//						}
//
//						Unit mutatedChild = crossers.get(random.nextInt(crossers.size())).cross(firstMutated,
//								secondMutated);
//
//						evaluator.evaluate(mutatedChild);
//
//						mutatedPopulation.remove(mutatedPopulation.indexOf(thirdMutated));
//						if (random.nextDouble() < mutationProbability) {
//							mutatedChild = mutators.get(random.nextInt(mutators.size())).mutate(mutatedChild);
//							evaluator.evaluate(mutatedChild);
//						}
//
//						mutatedPopulation.add(mutatedChild);
//					}
//
//					Collections.sort(mutatedPopulation);
//				}
//				child = mutatedPopulation.get(0);
//			}
//
//			population.add(child);
//		}
//	}

	public static void main(String[] args) {
		for (int i = 0; i < 15; i++) {
			for (int j = 6; j < 7; j++) {
				UsmjeravanjeVozila u = new UsmjeravanjeVozila();
				u.usmjeravanje("./i1.txt", i, j);
			}
		}
		
		//SwingUtilities.invokeLater(() -> new UsmjeravanjeVozila().setVisible(true));
	}

	public UsmjeravanjeVozila() {
		setSize(startingWidth, startingHeight);
		setLocation(startingPositionX, startingPositionY);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Usmjeravanje vozila");

		initGUI();
	}

	private void initGUI() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		progressBar = new JProgressBar();

		tabbedPane = new JTabbedPane();
		cp.add(tabbedPane, BorderLayout.CENTER);

		List<Parameter> parameters = new ArrayList<>();
		Parameter p1 = new Parameter(new JLabel("Tournament Size"), new JTextField("3"));
		parameters.add(p1);
		
		createNewTab(parameters, "Multiobjective VRP", tabbedPane, "i1.txt");

		JPanel doljnji = new JPanel(new FlowLayout());
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(400, 30));
		doljnji.add(progressBar);
		cp.add(doljnji, BorderLayout.SOUTH);
	}

	private void createNewTab(List<Parameter> parameters, String string, JTabbedPane tabbedPane, String path) {
		Parameter p1 = new Parameter(new JLabel("Population size"), new JTextField("100"));
		Parameter p2 = new Parameter(new JLabel("Number of iterations"), new JTextField("10000"));
		Parameter p3 = new Parameter(new JLabel("Probability of mutation"), new JTextField("0.3"));
		Parameter p4 = new Parameter(new JLabel("Tournament size"), new JTextField("3"));
		Parameter p5 = new Parameter(new JLabel("Mutated population size"), new JTextField("5"));
		Parameter p6 = new Parameter(new JLabel("Mutated iteration count"), new JTextField("5"));
		Parameter p7 = new Parameter(new JLabel("Problem file path"), new JTextField("./i1.txt"));

		JPanel alg = new JPanel(new BorderLayout());

		JPanel desno = new JPanel(new BorderLayout());

		JPanel param = new JPanel(new GridLayout(4, 2));
		desno.add(param, BorderLayout.CENTER);
		param.add(p1);
		param.add(p2);
		param.add(p3);
		param.add(p4);
		param.add(p5);
		param.add(p6);
		param.add(p7);

		alg.add(desno, BorderLayout.NORTH);

		output = new JTextArea();
		JScrollPane outputPannel = new JScrollPane(output);
		output.setBorder(BorderFactory.createLineBorder(Color.blue));
		output.setText("Output");
		output.setEditable(true);

		JPanel prepanel = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new BorderLayout());

		JPanel startHelp = new JPanel(new FlowLayout());
		start = new JButton("Izvedi");
		start.setPreferredSize(new Dimension(200, 30));
		startHelp.add(start);
		prepanel.add(panel, BorderLayout.CENTER);
		prepanel.add(startHelp, BorderLayout.SOUTH);
		panel.add(outputPannel, BorderLayout.CENTER);
		progressBar.setValue(0);
		progressBar.setString(0 + "%");

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				start.setEnabled(false);

				Thread t = new Thread(() -> {
					populationSize = Integer.parseInt(p1.getValue());
					NOIterations = Integer.parseInt(p2.getValue());
					mutationProbability = Double.parseDouble(p3.getValue());
					tournamentSize = Integer.parseInt(p4.getValue());
					mutatedPopulationSize = Integer.parseInt(p5.getValue());
					NOMutatedIterations = Integer.parseInt(p6.getValue());
					data = Paths.get(p7.getValue());

					progressBar.setValue(0);
					progressBar.setString(0 + "%");

					//usmjeravanje(path);
					
					plot();
				});

				t.start();
			}
		});
		alg.add(prepanel, BorderLayout.CENTER);
		tabbedPane.add(string, alg);
	}

	protected void plot() {
		JavaPlot plot = new JavaPlot();
		
		for(List<Unit> front :fronts) {
			int j = 0;
			double[][] data = new double[front.size()][2];
	
			for (int i = 0; i < front.size(); i++) {
				Unit u = front.get(i);
				data[i][0] = u.getDistance();
				data[i][1] = u.getBalance();
			}
	
			DataSetPlot dataPlot = new DataSetPlot(data);
	
		    plot.addPlot(dataPlot);
		    PlotStyle stl = dataPlot.getPlotStyle();
		    stl.setStyle(Style.LINESPOINTS);
		    if (j%2 ==0) {
	        stl.setLineType(NamedPlotColor.BLACK);
		    } else {
		    	stl.setLineType(NamedPlotColor.GREEN);
		    }
	        stl.setPointType(7);
	        stl.setPointSize(2);
	        stl.setLineType(2);
	        stl.setLineWidth(2);
	        j++;
	        
	        if (j == 1) {
	        	break;
	        }
		}
	    plot.plot();
	}
	
	
}
