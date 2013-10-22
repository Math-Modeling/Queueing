package net.clonecomputers.lab.queueing.calculate;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DataIterator implements Iterator<DataSnapshot> {
	
	private DataSnapshot[] data;
	
	private int i = 0;
	
	DataIterator(DataSnapshot[] simData) {
		data = simData;
	}

	public boolean hasNext() {
		return i < data.length;
	}

	public DataSnapshot next() {
		if(!hasNext()) throw new NoSuchElementException();
		return data[i++];
	}

	public void remove() {
		throw new UnsupportedOperationException("You may not remove stuff from the data array in DataIterator");
	}

}
