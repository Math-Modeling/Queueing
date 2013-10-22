package net.clonecomputers.lab.queueing.calculate;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.reflections.Reflections;

public class StatsMain extends JFrame {
	
	private final JFileChooser fileChooser = new JFileChooser();
	
	private DataSnapshot[] data = new DataSnapshot[0];
	private Set<AbstractAnalyzer> analyzers;
	private JList analyzersList;
	private AbstractAnalyzer showing = null;

	public static void main(String[] args) {
		final StatsMain app = new StatsMain();
		app.loadAnalyzersFromDefaultPackage();
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				app.initGui();
			}
		});
	}
	
	private void loadAnalyzersFromDefaultPackage() {
		Reflections analyzerPackage = new Reflections("net.clonecomputers.lab.queueing.calculate.analyzers");
		Set<Class<? extends AbstractAnalyzer>> analyzersInPackage = analyzerPackage.getSubTypesOf(AbstractAnalyzer.class);
		analyzers = new HashSet<AbstractAnalyzer>();
		for(Class<? extends AbstractAnalyzer> a : analyzersInPackage) {
			try {
				Constructor<? extends AbstractAnalyzer> constructor = a.getConstructor();
				analyzers.add(constructor.newInstance());
			} catch(NoSuchMethodException e) {
				System.err.println(a.getSimpleName() + " needs a default constructor");
			} catch(Exception e) {
				System.err.println("Error initializing analyzer class " + a.getSimpleName());
				e.printStackTrace();
			}
		}
	}
	
	private void initGui() {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		JPanel sidePanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new BorderLayout());
		JButton openAnalyzer = new JButton("Open Analyzer");
		JButton openData = new JButton("Open Data");
		analyzersList = new JList(analyzers.toArray());
		analyzersList.setCellRenderer(new DefaultListCellRenderer() {
			
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				return super.getListCellRendererComponent(list, value.getClass().getSimpleName(), index, isSelected, cellHasFocus);
			}
			
		});
		analyzersList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				setShowingAnalyzer((AbstractAnalyzer) analyzersList.getSelectedValue());
			}
		});
		openAnalyzer.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				openAnalyzer();
			}
		});
		openData.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				openData();
			}
		});
		buttonPanel.add(openAnalyzer, BorderLayout.PAGE_START);
		buttonPanel.add(openData, BorderLayout.PAGE_END);
		sidePanel.add(buttonPanel, BorderLayout.PAGE_START);
		sidePanel.add(analyzersList, BorderLayout.CENTER);
		contentPane.add(sidePanel, BorderLayout.LINE_START);
		setResizable(false);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void loadCsvData(File csv) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(csv));
		CSVParser parser = new CSVParser(in, CSVFormat.EXCEL);
		data = null;
		int i = 0;
		for(CSVRecord r : parser) {
			if(data == null) {
				if(r.isSet("how long to run")) {
					int length = 0;
					try {
						length = Integer.parseInt(r.get("how long to run"));
					} catch(NumberFormatException e) {
						continue;
					}
					data = new DataSnapshot[length];
				}
				continue;
			}
			try {
				data[i++] = new DataSnapshot(Double.parseDouble(r.get("delta t")),
										 	Integer.parseInt(r.get("shopping")),
										 	Integer.parseInt(r.get("in line")),
										 	Integer.parseInt(r.get("at checkout")));
			} catch(NumberFormatException e) {
				System.err.println("NAN on line " + parser.getCurrentLineNumber() + " of csv file " + csv);
				e.printStackTrace();
			}
		}
		System.out.println("Done reading CSV");
	}
	
	DataSnapshot[] getData() {
		return data;
	}
	
	private void openAnalyzer() {
		//JFileChooser analyzerChooser = new JFileChooser();
		//analyzerChooser.setFileFilter(new FileNameExtensionFilter("Allow .class or .jar files", "class", "jar"));
		//analyzerChooser.showOpenDialog(this);
		JOptionPane.showMessageDialog(this, "Sorry loading analyzers from file is not yet implemented",
				"Not Implemented", JOptionPane.ERROR_MESSAGE);
	}
	
	private void openData() {
		fileChooser.setFileFilter(new FileNameExtensionFilter("*.csv", "csv"));
		if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				loadCsvData(fileChooser.getSelectedFile());
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "Failed to find CSV file!", "Error!", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch(IOException e) {
				JOptionPane.showMessageDialog(this, "Error parsing CSV file!", "Error!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void setShowingAnalyzer(AbstractAnalyzer a) {
		if(showing != null) {
			getContentPane().remove(showing);
		}
		if(a != null) {
			getContentPane().add(a, BorderLayout.CENTER);
		}
		pack();
	}

}