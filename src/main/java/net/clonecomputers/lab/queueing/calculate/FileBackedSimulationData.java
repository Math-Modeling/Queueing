package net.clonecomputers.lab.queueing.calculate;

import java.io.*;
import java.util.*;

import net.clonecomputers.lab.queueing.calculate.DataSnapshot.QueueingEvent;

import org.apache.commons.csv.*;
import org.apache.commons.io.*;

public class FileBackedSimulationData implements SimulationData {
	private final File f;
	private final double lambda;
	private final double mu;
	private final int numberOfCashiers;
	private final int length;
	private CSVParser currentCSV;
	
	public FileBackedSimulationData(File f) throws IOException,FileNotFoundException {
		this.f = f;
		currentCSV = newCSV();
		CSVRecord firstLine = currentCSV.iterator().next();
		lambda = Double.parseDouble(firstLine.get("lambda"));
		mu = Double.parseDouble(firstLine.get("mu"));
		numberOfCashiers = Integer.parseInt(firstLine.get("number of cashiers"));
		length = Integer.parseInt(firstLine.get("how long to run"));
	}
	
	private CSVParser newCSV() {
		try {
			return new CSVParser(new BufferedReader(new FileReader(f)), CSVFormat.EXCEL.withIgnoreEmptyLines(true)
							.withHeader().withSkipHeaderRecord(true));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Iterator<DataSnapshot> iterator() {
		return new FileBackedSimulationDataIterator(newCSV().iterator());
	}
	
	private class FileBackedSimulationDataIterator implements Iterator<DataSnapshot> {
		private final Iterator<CSVRecord> i;
		DataSnapshot lastSnap = null;
		public FileBackedSimulationDataIterator(Iterator<CSVRecord> i){
			this.i = i;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public DataSnapshot next() {
			return getSnap(i.next(), lastSnap);
		}
		
		@Override
		public boolean hasNext() {
			return i.hasNext();
		}
	}

	/*@Override
	public DataSnapshot get(int index) {
		if(index >= length) throw new ArrayIndexOutOfBoundsException(index);
		if(currentCSV.getRecordNumber() >= index) currentCSV = newCSV();
		Iterator<CSVRecord> i = currentCSV.iterator();
		if(index == 0) return getSnap(i.next(),(DataSnapshot)null);
		if(index == 1) return getSnap(i.next(),i.next());
		while(i.hasNext() && i.next().getRecordNumber() < index-1);
		return getSnap(i.next(),i.next());
	}*/
	
	private static DataSnapshot getSnap(CSVRecord pastLine, CSVRecord data){
		return getSnap(data,getSnap(pastLine,QueueingEvent.OTHER));
	}
	
	private static DataSnapshot getSnap(CSVRecord data, QueueingEvent e){
		return new DataSnapshot(Double.parseDouble(data.get("delta t")),Integer.parseInt(data.get("customers shopping")),
				Integer.parseInt(data.get("queue length")),Integer.parseInt(data.get("cashiers busy")),e);
	}
	
	private static DataSnapshot getSnap(CSVRecord data, DataSnapshot last){
		return new DataSnapshot(Double.parseDouble(data.get("delta t")),Integer.parseInt(data.get("customers shopping")),
				Integer.parseInt(data.get("queue length")),Integer.parseInt(data.get("cashiers busy")),last);
	}

	@Override
	public double getLambda() {
		return lambda;
	}

	@Override
	public double getMu() {
		return mu;
	}

	@Override
	public int getNumberOfCashiers() {
		return numberOfCashiers;
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public void saveData(File dataFile) throws IOException {
		FileUtils.copyFile(f, dataFile);
	}

}
