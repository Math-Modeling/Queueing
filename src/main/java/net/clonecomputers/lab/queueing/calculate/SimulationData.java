package net.clonecomputers.lab.queueing.calculate;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SimulationData implements Iterable<DataSnapshot> {
	
	private final DataSnapshot[] data;
	private final double lambda;
	private final double mu;
	private final int numCashiers;

	SimulationData(DataSnapshot[] simData, double lambda, double mu, int numberOfCashiers) {
		data = simData;
		this.lambda = lambda;
		this.mu = mu;
		numCashiers = numberOfCashiers;
	}
	
	public DataSnapshot get(int index) {
		if(index >= data.length) throw new ArrayIndexOutOfBoundsException(index);
		return data[index];
	}
	
	public double getLambda() {
		return lambda;
	}
	
	public double getMu() {
		return mu;
	}
	
	public int getNumberOfCashiers() {
		return numCashiers;
	}
	
	public Iterator<DataSnapshot> iterator() {
		return new DataItr();
	}
	
	private class DataItr implements Iterator<DataSnapshot> {

		private int next = 0;
		
		public boolean hasNext() {
			return next < data.length;
		}

		public DataSnapshot next() {
			if(!hasNext()) throw new NoSuchElementException();
			return data[next++];
		}

		public void remove() {
			throw new UnsupportedOperationException("You may not remove stuff from the data array in DataIterator");
		}
	}

}
