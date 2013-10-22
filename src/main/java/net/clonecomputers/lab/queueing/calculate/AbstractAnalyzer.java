package net.clonecomputers.lab.queueing.calculate;

import javax.swing.JPanel;

public abstract class AbstractAnalyzer extends JPanel {
	
	private StatsMain main;
	
	public AbstractAnalyzer() {}
	
	void setMain(StatsMain main) {
		this.main = main;
	}
	
	public final DataIterator getData() {
		return new DataIterator(main.getData());
	}
	
}
