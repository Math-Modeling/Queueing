package net.clonecomputers.lab.queueing.calculate.filters;

import java.io.*;
import java.util.*;

import net.clonecomputers.lab.queueing.calculate.*;

public abstract class AbstractFilter implements Filter {
	public abstract List<DataSnapshot> processEvent(DataSnapshot e);
	
	protected double mu;
	protected double lambda;
	protected int numberOfCashiers;
	
	public SimulationData processEvents(SimulationData history){
		mu = history.getMu();
		lambda = history.getLambda();
		numberOfCashiers = history.getNumberOfCashiers();
		//List<DataSnapshot> filteredHistory = new ArrayList<DataSnapshot>();
		FileBackedSimulationData.Generator filteredHistory;
		try {
			filteredHistory = new FileBackedSimulationData.Generator();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		for(DataSnapshot e: history){
			List<DataSnapshot> newE = processEvent(e);
			if(newE != null && newE.size() > 0) filteredHistory.addAll(newE);
		}
		return filteredHistory.finish(mu,lambda,numberOfCashiers);
		//return new ArrayBackedSimulationData(filteredHistory.toArray(new DataSnapshot[0]), mu, lambda, numberOfCashiers);
	}
}
