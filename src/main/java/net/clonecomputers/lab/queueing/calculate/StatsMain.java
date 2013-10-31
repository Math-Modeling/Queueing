package net.clonecomputers.lab.queueing.calculate;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.clonecomputers.lab.queueing.*;
import net.clonecomputers.lab.queueing.calculate.DataSnapshot.QueueingEvent;
import net.clonecomputers.lab.queueing.generate.Queueing;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.reflections.Reflections;

@SuppressWarnings("serial")
public class StatsMain extends JFrame {
	
	private ExecutorService exec = Executors.newCachedThreadPool();
	
	private final JFileChooser fileChooser = new JFileChooser();
	
	private SimulationData data = new SimulationData(new DataSnapshot[0], 0, 0, 0);
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
				AbstractAnalyzer newAnalyzer = constructor.newInstance();
				newAnalyzer.setMain(this);
				analyzers.add(newAnalyzer);
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
		JButton generateData = new JButton("Generate Data");
		JButton openData = new JButton("Open Data");
		analyzersList = new JList(analyzers.toArray());
		analyzersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
				exec.execute(new Runnable(){
					@Override public void run() {
						openAnalyzer();
					}
				}
				);
			}
		});
		generateData.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				exec.execute(new Runnable(){
					@Override public void run() {
						try {
							generateData();
						} catch (IOException e) {
							throw new RuntimeException(e);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						} catch (InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					}
				}
				);
			}
		});
		openData.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				openData();
			}
		});
		buttonPanel.add(openAnalyzer, BorderLayout.PAGE_START);
		buttonPanel.add(generateData, BorderLayout.CENTER);
		buttonPanel.add(openData, BorderLayout.PAGE_END);
		sidePanel.add(buttonPanel, BorderLayout.PAGE_START);
		sidePanel.add(new JScrollPane(analyzersList), BorderLayout.CENTER);
		contentPane.add(sidePanel, BorderLayout.LINE_START);
		setResizable(false);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void generateData() throws IOException, InterruptedException, InvocationTargetException{
		File f = File.createTempFile("data", null);
		f.deleteOnExit();
		PrintStream pipeInput = new PrintStream(f); // FileWriter doesn't work, PrintStream does
		Reader pipeOutput = new FileReader(f);
		
		InputStream oldIn = System.in;
		PrintStream oldOut = System.out;
		PrintStream oldErr = System.err;
		
		final JConsole guiConsole = new JConsole();
		System.setIn(guiConsole.getIn());
		System.setOut(guiConsole.getOut());
		System.setErr(guiConsole.getErr());
		JFrame consoleWindow = new JFrame("Console");
		consoleWindow.pack();
		consoleWindow.setSize(800, 600);
		consoleWindow.add(guiConsole);
		consoleWindow.setResizable(false);
		consoleWindow.setVisible(true);
		consoleWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.repaint();
		
		Queueing q = new Queueing();
		q.setup(pipeInput);
		q.run();
		
		System.setIn(oldIn);
		System.setOut(oldOut);
		System.setErr(oldErr);
		
		loadCsvData(new BufferedReader(pipeOutput));
	}
	
	private void loadCsvData(File csv) throws IOException {
		loadCsvData(new BufferedReader(new FileReader(csv)));
	}
	
	private void loadCsvData(Reader csvInput) throws IOException {
		CSVParser parser = new CSVParser(csvInput, CSVFormat.EXCEL
				.withSkipHeaderRecord(true).withHeader().withIgnoreEmptyLines(true));
		DataSnapshot[] tempData = null;
		double lambda, mu;
		int numCashiers = 0;
		lambda = mu = Double.NaN;
		DataSnapshot lastData = null;
		int i = 0;
		try {
			for(CSVRecord r : parser) {
				if(tempData == null) {
					if(r.isSet("how long to run") && r.isSet("lambda") && r.isSet("mu") && r.isSet("number of cashiers")) {
						System.out.println("Found simulation wide data line");
						int length = 0;
						try {
							length = Integer.parseInt(r.get("how long to run"));
							lambda = Double.parseDouble(r.get("lambda"));
							mu = Double.parseDouble(r.get("mu"));
							numCashiers = Integer.parseInt(r.get("number of cashiers"));
						} catch(NumberFormatException e) {}
						if(length < 1) {
							throw new IOException("error parsing how long to run (must be a positve int)");
						} else if(Double.isNaN(lambda)) {
							throw new IOException("error parsing lambda (must be a double)");
						} else if(Double.isNaN(mu)) {
							throw new IOException("error parsing mu (must be a double)");
						} else if(numCashiers < 1) {
							throw new IOException("error parsing number of cashiers (must be a positve int)");
						}
						tempData = new DataSnapshot[length];
					}
					continue;
				}
				try {
					lastData = tempData[i++] = new DataSnapshot(Double.parseDouble(r.get("delta t")),
							Integer.parseInt(r.get("shopping")),
							Integer.parseInt(r.get("in line")),
							Integer.parseInt(r.get("at checkout")),
							lastData);
				} catch(NumberFormatException e) {
					System.err.println("NAN on line " + parser.getCurrentLineNumber() + " of csv file " + csvInput);
					e.printStackTrace();
				}
			}
			data = new SimulationData(tempData, lambda, mu, numCashiers);
			System.out.println("Done reading CSV");
		} finally {
			parser.close();
		}
	}
	
	SimulationData getData() {
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
		showing = a;
		pack();
	}

}
