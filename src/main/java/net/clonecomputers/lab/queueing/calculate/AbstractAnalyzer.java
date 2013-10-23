package net.clonecomputers.lab.queueing.calculate;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class AbstractAnalyzer extends JPanel {
	
	private StatsMain main;
	
	public AbstractAnalyzer() {}
	
	void setMain(StatsMain main) {
		this.main = main;
	}
	
	public final SimulationData getData() {
		return main.getData();
	}
	
}
