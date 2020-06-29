package routing.draw;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Parameter extends JPanel{
	private JLabel labelName;
	private JTextField field;
	
	public Parameter(JLabel labelName, JTextField field) {
		this.labelName = labelName;
		this.field = field;

		setLayout(new GridLayout(1, 2));
		add(labelName);
		
		field.setHorizontalAlignment(JTextField.LEFT);
		add(field);
	}

	public JLabel getLabelName() {
		return labelName;
	}

	public void setLabelName(JLabel labelName) {
		this.labelName = labelName;
	}

	public String getValue() {
		return field.getText();
	}

	public void setValue(String value) {
		this.field.setText(value);
	}	
}
