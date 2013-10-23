package net.clonecomputers.lab.queueing.calculate;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SimulationData implements Iterable<DataSnapshot> {
	
	private final DataSnapshot[] data;

	SimulationData(DataSnapshot[] simData) {
		data = simData;
	}
	
	public DataSnapshot get(int index) {
		if(index >= data.length) throw new ArrayIndexOutOfBoundsException(index);
		return data[index];
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
