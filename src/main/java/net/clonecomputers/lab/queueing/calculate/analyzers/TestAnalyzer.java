package net.clonecomputers.lab.queueing.calculate.analyzers;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.clonecomputers.lab.queueing.calculate.AbstractAnalyzer;

public class TestAnalyzer extends AbstractAnalyzer {

	public TestAnalyzer() {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				initGui();
			}
		});
	}
	
	private void initGui() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.add(new JLabel("TEST!!!!"));
	}
	
}
