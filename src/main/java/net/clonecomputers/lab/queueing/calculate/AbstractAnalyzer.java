package net.clonecomputers.lab.queueing.calculate;

import java.awt.Component;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class AbstractAnalyzer extends JPanel {
	
	private StatsMain main;
	private List<InputField<?>> inputFields = new ArrayList<InputField<?>>();
	
	public AbstractAnalyzer() {}
	
	void setMain(StatsMain main) {
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
