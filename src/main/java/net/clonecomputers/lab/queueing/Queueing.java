package net.clonecomputers.lab.queueing;

import java.io.*;
import java.util.*;
import static java.lang.Math.*;

public class Queueing {
	
	private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	private Cashier[] cashiers;
	private double timeToNextCustomer;
	private Set<Customer> customers;
	private Queue<Customer> customersInQueue;
	private Set<Customer> finishedCustomers;

	private double mu;
	private double lambda;

	public void setup() throws IOException {
		System.out.println("Input how many cashiers: ");
		cashiers = new Cashier[Integer.parseInt(in.readLine().trim())];
		for(int i = 0; i < cashiers.length; i++) cashiers[i] = new Cashier();
		timeToNextCustomer = 0;
		customers = new HashSet<Customer>();
		finishedCustomers = new HashSet<Customer>();
		customersInQueue = new LinkedList<Customer>();
		mu = .25;
		lambda = 5;
	}
	
	public void run() throws IOException {
		System.out.println("type \"quit\" to quit");
		while(!in.ready() && !in.readLine().trim().equalsIgnoreCase("quit")){
			double intervalLength = howLongCurrentStateWillLast();
			updateTime(intervalLength);
			updateState();
			updateStats(intervalLength);
		}
		printSystemState();
	}

	private void updateStats(double intervalLength) {
		//TODO: implement me
	}

	private void printSystemState() {
		//TODO Implement me
	}

	private void updateState() {
		if(timeToNextCustomer == 0){
			Customer c = new Customer();
			customersInQueue.add(c);
			c.inQueue = true;
			timeToNextCustomer = randomCustomerInterval();
		}
		for(Cashier c: cashiers){
			if(c.howLongUntilDone == 0){
				c.currentCustomer.atCheckout = false;
				customers.remove(c.currentCustomer);
				finishedCustomers.add(c.currentCustomer);
				c.currentCustomer = null;
			}
			if(c.currentCustomer == null){
				c.currentCustomer = customersInQueue.poll();
				if(c.currentCustomer != null){
					c.currentCustomer.inQueue = false;
					c.currentCustomer.atCheckout = true;
					c.howLongUntilDone = randomCashierTime();
				}
			}
		}
	}

	private double randomCashierTime() {
		return -log(random()/mu);
	}

	private double randomCustomerInterval() {
		return -log(random()/lambda);
	}

	private void updateTime(double howFarToAdvance) {
		timeToNextCustomer -= howFarToAdvance;
		for(Cashier c: cashiers){
			if(c.currentCustomer == null){
				c.howLongUntilDone -= howFarToAdvance;
			}
		}
		for(Customer c: customers){
			c.totalTimeSoFar += howFarToAdvance;
		}
	}

	private double howLongCurrentStateWillLast() {
		double minTimeInCurrentState = timeToNextCustomer;
		for(Cashier c: cashiers){
			if(c.currentCustomer == null){
				if(c.howLongUntilDone < minTimeInCurrentState){
					minTimeInCurrentState = c.howLongUntilDone;
				}
			}
		}
		return minTimeInCurrentState;
	}

	public static void main(String[] args) throws IOException {
		Queueing q = new Queueing();
		q.setup();
		q.run();
	}
}
