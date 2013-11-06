package net.clonecomputers.lab.queueing.generate;

import java.io.*;
import java.util.*;

import static java.lang.Math.*;

public class Queueing {
	
	private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public Cashier[] cashiers;
	private double timeToNextCustomer;
	public Set<Customer> customers;
	public Queue<Customer> customersInQueue;
	
	private CSVExport csv;

	public double mu;
	public double lambda;

	public boolean extraTime;
	
	public long maxIterations;

	public void setup() throws IOException {
		System.out.println("Input how many steps to run");
		maxIterations = Long.parseLong(in.readLine().trim());
		System.out.println("Input how many cashiers: ");
		cashiers = new Cashier[Integer.parseInt(in.readLine().trim())];
		System.out.println("Should I add extra time for queue length? (Y/n)");
		extraTime = isTrue(in.readLine().trim(),true);
		for(int i = 0; i < cashiers.length; i++) cashiers[i] = new Cashier();
		customers = new HashSet<Customer>();
		customersInQueue = new LinkedList<Customer>();
		mu = .25;
		lambda = 5;
		timeToNextCustomer = randomCustomerInterval();
		csv = new CSVExport(this);
		System.out.println("Input filepath");
		String filepath = in.readLine().trim();
		csv.startCSV(getWriterFromFileString(filepath));
	}
	
	public void setup(Appendable csvOutput) throws NumberFormatException, IOException{
		System.out.println("Input how many steps to run");
		maxIterations = Long.parseLong(in.readLine().trim());
		System.out.println("Input how many cashiers: ");
		cashiers = new Cashier[Integer.parseInt(in.readLine().trim())];
		System.out.println("Should I add extra time for queue length? (Y/n)");
		extraTime = isTrue(in.readLine().trim(),true);
		for(int i = 0; i < cashiers.length; i++) cashiers[i] = new Cashier();
		timeToNextCustomer = 0;
		customers = new HashSet<Customer>();
		customersInQueue = new LinkedList<Customer>();
		mu = .25;
		lambda = 5;
		csv = new CSVExport(this);
		csv.startCSV(csvOutput);
	}
	
	public BufferedWriter getWriterFromFileString(String filepath) throws IOException{
		filepath = filepath.replace("~", System.getProperty("user.home"));
		if(!filepath.startsWith("/") && !filepath.startsWith("~")){
			filepath = System.getProperty("user.home") + "/" + filepath;
		}
		File f = new File(filepath);
		return new BufferedWriter(new FileWriter(f));
	}
	
	public void setup(long maxIterations, int howManyCashiers,
			boolean addExtraTimeForLine, Appendable csvOutput) throws IOException {
		
		for(int i = 0; i < cashiers.length; i++) cashiers[i] = new Cashier();
		timeToNextCustomer = 0;
		customers = new HashSet<Customer>();
		customersInQueue = new LinkedList<Customer>();
		mu = .25;
		lambda = 5;
		csv = new CSVExport(this);
		csv.startCSV(csvOutput);
	}
	
	private boolean isTrue(String s, boolean defaultValue) {
		if(defaultValue == false){
			return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes") ||
					s.equalsIgnoreCase("t") || s.equalsIgnoreCase("true");
		}else{
			return !(s.equalsIgnoreCase("n") || s.equalsIgnoreCase("no") ||
					s.equalsIgnoreCase("f") || s.equalsIgnoreCase("false"));
		}
	}

	public void run() throws IOException {
		System.out.println("Generating data");
		for(long i = 0; i < maxIterations; i++){ // now discards last data, so must do one more iteration
			// in order to get the desired number of iterations
			double intervalLength = howLongCurrentStateWillLast();
			csv.record(howLongCurrentStateWillLast());
			updateTime(intervalLength);
			updateState();
		}
		System.out.println("Done");
	}

	private void updateState() {
		if(timeToNextCustomer == 0){
			Customer c = new Customer();
			customers.add(c);
			c.inSupermarket = true;
			c.timeLeftInSupermarket = supermarketTime();
			timeToNextCustomer = randomCustomerInterval();
		}
		for(Customer c: customers){
			if(c.timeLeftInSupermarket == 0 && c.inSupermarket){
				c.inSupermarket = false;
				c.inQueue = true;
				customersInQueue.add(c);
				c.inQueue = true;
			}
		}
		for(Cashier c: cashiers){
			if(c.howLongUntilDone <= 0 && c.currentCustomer != null){
				c.currentCustomer.atCheckout = false;
				customers.remove(c.currentCustomer);
				//stats.finishedCustomers.add(c.currentCustomer);
				c.currentCustomer = null;
				//System.out.println("removed customer from "+c.id);
			}
			if(c.currentCustomer == null){
				c.currentCustomer = customersInQueue.poll();
				if(c.currentCustomer != null){
					c.currentCustomer.inQueue = false;
					c.currentCustomer.atCheckout = true;
					c.howLongUntilDone = randomCashierTime();
					//System.out.println("added customer to "+c.id+" for "+c.howLongUntilDone);
				}
			}
		}
	}

	private double supermarketTime() {
		if(extraTime){
			return 15 + customersInQueue.size()/5.0;
		}else{
			return 15;
		}
	}

	private double randomCashierTime() {
		return -log(random())/mu;
	}

	private double randomCustomerInterval() {
		return -log(random())/lambda;
	}

	private void updateTime(double howFarToAdvance) {
		timeToNextCustomer -= howFarToAdvance;
		for(Cashier c: cashiers){
			if(c.currentCustomer != null){
				c.howLongUntilDone -= howFarToAdvance;
			}
		}
		for(Customer c: customers){
			if(c.atCheckout) c.timeSpentAtCheckout += howFarToAdvance;
			else if(c.inQueue) c.timeSpentInQueue += howFarToAdvance;
			else if(c.inSupermarket){
				c.timeSpentInSupermarket += howFarToAdvance;
				c.timeLeftInSupermarket -= howFarToAdvance;
			}else System.err.println(c+" in wrong set");
		}
	}

	private double howLongCurrentStateWillLast() {
		double minTimeInCurrentState = timeToNextCustomer;
		for(Cashier c: cashiers){
			if(c.currentCustomer != null){
				if(c.howLongUntilDone < minTimeInCurrentState){
					minTimeInCurrentState = c.howLongUntilDone;
				}
			}
		}
		for(Customer c: customers){
			if(c.inSupermarket){
				if(c.timeLeftInSupermarket < minTimeInCurrentState){
					minTimeInCurrentState = c.timeLeftInSupermarket;
				}
			}
		}
		return minTimeInCurrentState;
	}

	private void finish() throws IOException {
		csv.finishCSV();
	}

	public static void main(String[] args) throws IOException {
		Queueing q = new Queueing();
		q.setup();
		q.run();
		q.finish();
	}
}
