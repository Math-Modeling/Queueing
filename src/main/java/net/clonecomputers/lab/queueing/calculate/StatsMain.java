package net.clonecomputers.lab.queueing.calculate;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import net.clonecomputers.lab.queueing.calculate.analyzers.*;
import net.clonecomputers.lab.queueing.calculate.filters.*;
import net.clonecomputers.lab.queueing.generate.*;
import net.clonecomputers.lab.util.*;

import org.apache.commons.csv.*;
import org.reflections.*;
import org.reflections.scanners.*;

@SuppressWarnings("serial")
public class StatsMain {
	
	private JPanel analyzePanel;
	private FilterPanel filterPanel;
	private Container mainPanel;
	
	private JFrame mainWindow; // USE ONLY AS mainWindow.pack() AFTER COMPONENT CHANGE
	
	private ExecutorService exec = Executors.newCachedThreadPool();
	
	private final JFileChooser fileChooser = new JFileChooser();
	
	private SimulationData data = new SimulationData(new DataSnapshot[0], 0, 0, 0);
	
	private Set<AbstractAnalyzer> analyzers;
	private JList analyzersList;
	private AbstractAnalyzer showing = null;
	
	private Set<Filter> filters;

	public static void main(String[] args) {
		final StatsMain app = new StatsMain();
		app.loadAnalyzersFromDefaultPackage();
		app.loadFiltersFromDefaultPackage();
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				app.initGui();
			}
		});
	}
	
	private <T> HashSet<Class<? extends T>> findAllImplementations(String p, Class<T> superclass){
		Reflections ref = new Reflections(p,new SubTypesScanner(false));
		HashSet<Class<? extends T>> allImpl = new HashSet<Class<? extends T>>();
		Set<Class<?>> allClasses = ref.getSubTypesOf(Object.class);
		for(Class<?> c: allClasses){
			if(superclass.isAssignableFrom(c) &&
					!c.isInterface() &&
					!c.isAnonymousClass() &&
					!Modifier.isAbstract(c.getModifiers())){
				allImpl.add((Class<? extends T>)c);
			}
		}
		return allImpl;
	}
	
	private void loadAnalyzersFromDefaultPackage() {
		Set<Class<? extends AbstractAnalyzer>> analyzersInPackage = findAllImplementations("net.clonecomputers.lab.queueing.calculate.analyzers", AbstractAnalyzer.class);
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
	
	private void loadFiltersFromDefaultPackage() { // FIXME: doesn't load anything
		Set<Class<? extends Filter>> filtersInPackage = findAllImplementations("net.clonecomputers.lab.queueing.calculate.filters", Filter.class);
		filters = new HashSet<Filter>();
		for(Class<? extends Filter> a : filtersInPackage) {
			try {
				Constructor<? extends Filter> constructor = a.getConstructor();
				Filter newFilter = constructor.newInstance();
				filters.add(newFilter);
			} catch(NoSuchMethodException e) {
				System.err.println(a.getSimpleName() + " needs a default constructor");
			} catch(Exception e) {
				System.err.println("Error initializing filter class " + a.getSimpleName());
				e.printStackTrace();
			}
		}
	}
	
	private void initGui() {
		mainWindow = new JFrame();
		
		analyzePanel = new JPanel();
		filterPanel = new FilterPanel(this);
		mainPanel = mainWindow.getContentPane();
		
		JPanel buttonPanel = new JPanel();
		JPanel analyzeLeftPanel = new JPanel();
		
		mainPanel.setLayout(new BorderLayout());
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		analyzePanel.setLayout(new BorderLayout());
		filterPanel.setLayout(new BorderLayout());
		analyzeLeftPanel.setLayout(new BorderLayout());
		
		JButton generateData = new JButton("Generate Data");
		JButton openData = new JButton("Load Data");
		JButton saveData = new JButton("Save Data");
		JButton analyzeData = new JButton("Analyze Data");
		JButton filterData = new JButton("Filter Data");
		
		filterPanel.initGUI(filters);
		
		JButton openAnalyzer = new JButton("Open Analyzer");
		
		analyzersList = new JList(analyzers.toArray());
		analyzersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		analyzersList.setCellRenderer(new DefaultListCellRenderer() {         // what is this about?  -Gavin
			
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				return super.getListCellRendererComponent(list, value.getClass().getSimpleName(), index, isSelected, cellHasFocus);
			}
			
		});
		analyzersList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				setShowingAnalyzer((AbstractAnalyzer) analyzersList.getSelectedValue());
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
				exec.execute(new Runnable(){
					@Override public void run() {
						openData();
					}
				});
			}
		});
		saveData.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				exec.execute(new Runnable(){
					@Override public void run() {
						saveData();
					}
				});
			}
		});
		analyzeData.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Component[] comps = mainPanel.getComponents();
				boolean b = false;
				for(Component c: comps) if(c.equals(analyzePanel)) b = true;
				mainPanel.remove(analyzePanel);
				mainPanel.remove(filterPanel);
				if(!b) mainPanel.add(analyzePanel, BorderLayout.CENTER);
				mainPanel.validate();
				mainWindow.pack();
			}
		});
		filterData.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Component[] comps = mainPanel.getComponents();
				boolean b = false;
				for(Component c: comps) if(c.equals(filterPanel)) b = true;
				mainPanel.remove(analyzePanel);
				mainPanel.remove(filterPanel);
				if(!b) mainPanel.add(filterPanel, BorderLayout.CENTER);
				mainPanel.validate();
				mainWindow.pack();
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
		
		buttonPanel.add(generateData);
		buttonPanel.add(openData);
		buttonPanel.add(saveData);
		buttonPanel.add(analyzeData);
		buttonPanel.add(filterData);
		
		analyzeLeftPanel.add(new JScrollPane(analyzersList), BorderLayout.PAGE_END);
		analyzeLeftPanel.add(openAnalyzer, BorderLayout.PAGE_START);
		analyzePanel.add(analyzeLeftPanel,BorderLayout.LINE_START);
		
		mainPanel.add(buttonPanel,BorderLayout.LINE_START);
		
		buttonPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
		
		mainWindow.setResizable(false);
		mainWindow.pack();
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setVisible(true);
	}
	
	public void filterData() {
		for(Filter f: filterPanel.getActiveFilters()){
			data = f.processEvents(data);
		}
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
		
		Queueing q = new Queueing();
		q.setup(pipeInput);
		q.run();
		
		System.setIn(oldIn);
		System.setOut(oldOut);
		System.setErr(oldErr);
		consoleWindow.dispose();
		
		loadCsvData(new BufferedReader(pipeOutput));
	}
	
	private void saveCsvData(File csvFile) throws IOException {
		CSVPrinter csv = new CSVPrinter(new BufferedWriter(new FileWriter(csvFile)), CSVFormat.EXCEL);
		csv.printRecord("delta t","shopping","in line","at checkout","lambda","mu","number of cashiers","how long to run");
		csv.printRecord(null,null,null,null,data.getLambda(),data.getMu(), data.getNumberOfCashiers(),data.size());
		for(DataSnapshot s: data){
			csv.printRecord(s.getTime(),s.getCustomersShopping(),s.getQueueLength(),s.getCashiersBusy());
		}
		csv.flush();
		csv.close();
	}
	
	private void loadCsvData(File csv) throws IOException {
		loadCsvData(new BufferedReader(new FileReader(csv)));
	}
	
	private void loadCsvData(Reader csvInput) throws IOException { //FIXME: doesn't properly load time for first snapshot
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
	
	public SimulationData getData() {
		return data;
	}
	
	private void openAnalyzer() {
		//JFileChooser analyzerChooser = new JFileChooser();
		//analyzerChooser.setFileFilter(new FileNameExtensionFilter("Allow .class or .jar files", "class", "jar"));
		//analyzerChooser.showOpenDialog(this);
		JOptionPane.showMessageDialog(mainPanel, "Sorry loading analyzers from file is not yet implemented",
				"Not Implemented", JOptionPane.ERROR_MESSAGE);
	}
	
	private void saveData() {
		fileChooser.setFileFilter(new FileNameExtensionFilter("*.csv", "csv"));
		if(fileChooser.showSaveDialog(mainPanel) == JFileChooser.APPROVE_OPTION) {
			try {
				saveCsvData(fileChooser.getSelectedFile());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void openData() {
		fileChooser.setFileFilter(new FileNameExtensionFilter("*.csv", "csv"));
		if(fileChooser.showOpenDialog(mainPanel) == JFileChooser.APPROVE_OPTION) {
			try {
				loadCsvData(fileChooser.getSelectedFile());
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(mainPanel, "Failed to find CSV file!", "Error!", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch(IOException e) {
				JOptionPane.showMessageDialog(mainPanel, "Error parsing CSV file!", "Error!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void setShowingAnalyzer(AbstractAnalyzer a) {
		if(showing != null) {
			analyzePanel.remove(showing);
		}
		if(a != null) {
			JPanel newPanel = new JPanel(new BorderLayout());
			JPanel spacerPanel = new JPanel();
			spacerPanel.setPreferredSize(new Dimension(2, 2));
			newPanel.add(spacerPanel,BorderLayout.LINE_START);
			newPanel.add(a, BorderLayout.LINE_END);
			analyzePanel.add(newPanel,BorderLayout.LINE_END);
		}
		showing = a;
		analyzePanel.validate();
		mainWindow.pack();
	}

}
