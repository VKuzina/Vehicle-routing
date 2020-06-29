package routing.draw;
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import routing.UsmjeravanjeVozila;

public class Algorithm extends JPanel {

	private List<Parameter> parametriAlgoritma;
	private JComboBox<String> comboBox;
	private List<Parameter> parametriGenotipa;
	private List<Parameter> parametriRegistrya;
	private String ime;
	private JPanel param;
	private String ruta;
	private JProgressBar progressBar;
	private int currentMaxEvals;
	private UsmjeravanjeVozila program;
	private String trenutniProblem;

	public Algorithm(List<Parameter> parametriAlgoritma, JComboBox<String> comboBox, String ime,
			JProgressBar progressBar, UsmjeravanjeVozila program) {
		this.parametriAlgoritma = parametriAlgoritma;
		this.comboBox = comboBox;
		this.ime = ime;
		this.progressBar = progressBar;
		this.program = program;

		setLayout(new BorderLayout());
	}

	public void setParametriGenotipa(List<Parameter> parametriGenotipa) {
		this.parametriGenotipa = parametriGenotipa;
	}

	public void setParametriRegistrya(List<Parameter> parametriRegistrya) {
		this.parametriRegistrya = parametriRegistrya;
		if (param != null) {
			promijeniParametre();
		}
	}
	
	public void setTrenutniProblem(String trenutniProblem) {
		this.trenutniProblem = trenutniProblem;
	}
	
	public void dodajParametre() {
		JPanel desno = new JPanel(new BorderLayout());

		desno.add(comboBox, BorderLayout.NORTH);

		param = new JPanel(new GridLayout(
				(parametriAlgoritma.size() + parametriGenotipa.size() + parametriRegistrya.size()) / 2, 2));
		desno.add(param, BorderLayout.CENTER);

		promijeniParametre();

		add(desno, BorderLayout.NORTH);

		JTextArea ulaz = new JTextArea();
		JTextArea izlaz = new JTextArea();
		JScrollPane ulazP = new JScrollPane(ulaz);
		JScrollPane izlazP = new JScrollPane(izlaz);

		ulaz.setBorder(BorderFactory.createLineBorder(Color.blue));
		izlaz.setBorder(BorderFactory.createLineBorder(Color.blue));

		ulaz.setText("Ulazna datoteka");
		ulaz.setEditable(false);
		izlaz.setText("Izlazna datoteka");
		izlaz.setEditable(false);

		JPanel prepanel = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new GridLayout(2, 1));

		JPanel startHelp = new JPanel(new FlowLayout());
		JButton start = new JButton("Izvedi");
		start.setPreferredSize(new Dimension(200, 30));
		startHelp.add(start);
		prepanel.add(panel, BorderLayout.CENTER);
		prepanel.add(startHelp, BorderLayout.SOUTH);
		panel.add(ulazP);
		panel.add(izlazP);

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				start.setEnabled(false);
				update(0);
				
				program.usmjeravanje("bla.txt");
			}
		});
		add(prepanel, BorderLayout.CENTER);
	}
	
	public Object update(int value) {
		progressBar.setValue(value);
		progressBar.setString(value + "%");

		return null;
	}
	private void promijeniParametre() {
		param.removeAll();
		for (Parameter p : parametriAlgoritma) {
			param.add(p);
		}
		for (Parameter p : parametriGenotipa) {
			param.add(p);
		}
		for (Parameter p : parametriRegistrya) {
			param.add(p);
		}
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
}
