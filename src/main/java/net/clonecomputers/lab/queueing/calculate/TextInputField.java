package net.clonecomputers.lab.queueing.calculate;

import java.awt.FlowLayout;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class TextInputField extends InputField<String> {
	
	private JLabel label;
	private JTextField field;

	public TextInputField(String labelText) {
		this(labelText, new FlowLayout());
	}
	
	public TextInputField(String labelText, LayoutManager layout) {
		this.setLayout(layout);
		label = new JLabel(labelText);
		this.add(label);
		field = new JTextField();
		this.add(field);
	}
	
	public String getLabelText() {
		return label.getText();
	}
	
	public void setLabelText(String labelText) {
		label.setText(labelText);
	}
	
	public String getInputted() {
		return field.getText();
	}
	
	public void setInputted(String t) {
		field.setText(t);
	}
	
}
