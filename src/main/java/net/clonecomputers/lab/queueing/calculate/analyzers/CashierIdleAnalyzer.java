package net.clonecomputers.lab.queueing.calculate.analyzers;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.clonecomputers.lab.queueing.calculate.DataSnapshot;


@SuppressWarnings("serial")
public class CashierIdleAnalyzer extends AbstractAnalyzer {
	
	private JLabel timeIdleLabel;
	private JLabel combinedIdleLabel;
	private JLabel averageIdleLabel;
	
	public CashierIdleAnalyzer() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				initGui();
			}
		});
	}
	
	private void initGui() {
		this.setLayout(new BorderLayout());
		JLabel title = new JLabel("Cashier Idle Time");
		title.setFont(new Font("SansSerif", Font.PLAIN, 24));
		this.add(title, BorderLayout.PAGE_START);
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		timeIdleLabel = new JLabel("Time with idle cashiers (min): (UNCALCULATED)");
		combinedIdleLabel = new JLabel("Combined time idle for cashiers (person*min): (UNCALCULATED)");
		averageIdleLabel = new JLabel("Average idle time for a cashier (min): (UNCALCULATED)");
		JButton calculateBtn = new JButton("Calculate");
		calculateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				calculate(); //should this be done on graphics thread or new thread?
			}
		});
		main.add(timeIdleLabel);
		main.add(combinedIdleLabel);
		main.add(averageIdleLabel);
		main.add(calculateBtn);
		this.add(main, BorderLayout.CENTER);
	}
	
	private void calculate() {
		double timeIdle = 0;
		double combinedIdle = 0;
		int totalCashiers = getData().getNumberOfCashiers();
		for(DataSnapshot ds : getData()) {
			timeIdle += ds.getCashiersBusy() < totalCashiers ? ds.getTime() : 0;
			combinedIdle += (totalCashiers - ds.getCashiersBusy())*ds.getTime();
		}
		timeIdleLabel.setText("Time with idle cashiers (min): " + timeIdle);
		combinedIdleLabel.setText("Combined time idle for cashiers (person*min): " + combinedIdle);
		averageIdleLabel.setText("Average idle time for a cashier (min): " + combinedIdle/getData().getNumberOfCashiers());
	}
	
}
