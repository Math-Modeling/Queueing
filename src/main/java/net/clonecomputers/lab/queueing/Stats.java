package net.clonecomputers.lab.queueing;

import java.util.*;

public class Stats {
	public Set<Customer> finishedCustomers = new HashSet<Customer>();
	public double averageQueueLength = 0;
	public double totalTime = 0;
	
	public void update(double timeLength, Queueing queueing){
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

	public void printStats() {
		System.out.println(averageQueueLength);
	}
}
