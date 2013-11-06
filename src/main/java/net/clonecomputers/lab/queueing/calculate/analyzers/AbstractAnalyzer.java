package net.clonecomputers.lab.queueing.calculate.analyzers;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import net.clonecomputers.lab.queueing.calculate.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

@SuppressWarnings("serial")
public abstract class AbstractAnalyzer extends JPanel {
	
	private StatsMain main;
	private List<InputField<?>> inputFields = new ArrayList<InputField<?>>();
	private final JFileChooser fileChooser = new JFileChooser();
	
	public AbstractAnalyzer() {}
	
	public void setMain(StatsMain main) {
		this.main = main;
	}
	
	public final SimulationData getData() {
		return main.getData();
	}
	
	public void add(InputField<?> field) {
		super.add(field);
		inputFields.add(field);
	}
	
	public List<Object> getInputted() {
		ArrayList<Object> inputted = new ArrayList<Object>();
		for (InputField<?> i : inputFields) {
			inputted.add(i.getInputted());
		}
		return inputted;
	}
	
	public CSVPrinter getCsvPrinter() throws IOException {
		int value = fileChooser.showSaveDialog(this.getParent());
		if(value == JFileChooser.APPROVE_OPTION) {
			return new CSVPrinter(new BufferedWriter(new FileWriter(fileChooser.getSelectedFile())), CSVFormat.EXCEL);
		} else {
			return null;
		}
	}
	
	@Override
	public void remove(int index) {
		this.remove(this.getComponent(index));
	}
	
	@Override
	public void remove(Component comp) {
		super.remove(comp);
		if(inputFields.contains(comp)) {
			inputFields.remove(comp);
		}
	}
	
	@Override
	public void removeAll() {
		super.removeAll();
		inputFields.clear();
	}
	
}
