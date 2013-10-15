package net.clonecomputers.lab.queueing.calculate;

import java.util.*;

import net.clonecomputers.lab.queueing.generate.*;

public class Stats {
	public Set<Customer> finishedCustomers = new HashSet<Customer>();
	public double averageQueueLength = 0;
	public double totalTime = 0;
	
	private Queueing queueing;
	
	public Stats(Queueing queueing){
		this.queueing = queueing;
	}
	
	public void update(double timeLength){
		if(timeLength == 0) return;
		averageQueueLength = weightedAverage(averageQueueLength,totalTime,
				queueing.customersInQueue.size(),timeLength);
		totalTime += timeLength;
	}
	
	public double weightedAverage(double... args){
		double[] items = new double[args.length/2];
		double[] weights = new double[args.length/2];
		for(int i = 0; i < args.length-1; i+=2){
			items[i/2] = args[i];
			weights[i/2] = args[i+1];
		}
		double weightedSum = 0;
		double sumOfWeights = 0;
		for(int i = 0; i < items.length; i++){
			weightedSum += items[i]*weights[i];
			sumOfWeights += weights[i];
		}
		return weightedSum / sumOfWeights;
	}

	public double averageQueueLength(){
		return averageQueueLength;
	}
	
	public double averageTimeSpentInSupermarket(){
		double totalTimeInQueue = 0;
		for(Customer c: finishedCustomers){
			totalTimeInQueue += c.timeSpentInSupermarket;
		}
		return totalTimeInQueue / finishedCustomers.size();
	}
	
	public double averageTimeSpentInQueue(){
		double totalTimeInQueue = 0;
		for(Customer c: finishedCustomers){
			totalTimeInQueue += c.timeSpentInQueue;
		}
		return totalTimeInQueue / finishedCustomers.size();
	}
	
	public double averageTimeSpentAtCheckout(){
		double totalTimeInQueue = 0;
		for(Customer c: finishedCustomers){
			totalTimeInQueue += c.timeSpentAtCheckout;
		}
		return totalTimeInQueue / finishedCustomers.size();
	}
	
	public void printStats() {
		System.out.println("Average queue length: " + averageQueueLength());
		double s = averageTimeSpentInSupermarket(),
				q = averageTimeSpentInQueue(),
				c = averageTimeSpentAtCheckout();
		System.out.println("Average time spent shopping: " + s);
		System.out.println("Average time spent in queue: " + q);
		System.out.println("Average time spent at checkout: " + c);
		System.out.println("Average time spent in store: " + (s+q+c));
		System.out.println("Simulated "+finishedCustomers.size()+" customers");
	}
}
