package net.clonecomputers.lab.queueing.calculate;

import java.io.*;
import java.util.*;

import org.apache.commons.csv.*;

public class ArrayBackedSimulationData implements SimulationData {
	
	private final DataSnapshot[] data;
	private final double lambda;
	private final double mu;
	private final int numCashiers;

	public ArrayBackedSimulationData(DataSnapshot[] simData, double lambda, double mu, int numberOfCashiers) {
		data = simData;
		this.lambda = lambda;
		this.mu = mu;
		numCashiers = numberOfCashiers;
	}
	
	public ArrayBackedSimulationData(File csvInput) throws IOException {
		this(new BufferedReader(new FileReader(csvInput)));
	}
	
	public ArrayBackedSimulationData(Reader csvInput) throws IOException {
		CSVParser parser = new CSVParser(csvInput, CSVFormat.EXCEL
				.withSkipHeaderRecord(true).withHeader().withIgnoreEmptyLines(true));
		DataSnapshot[] tempData = null;
		double tempLambda, tempMu;
		int tempNumCashiers = 0;
		tempLambda = tempMu = Double.NaN;
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
							tempLambda = Double.parseDouble(r.get("lambda"));
							tempMu = Double.parseDouble(r.get("mu"));
							tempNumCashiers = Integer.parseInt(r.get("number of cashiers"));
						} catch(NumberFormatException e) {}
						if(length < 1) {
							throw new IOException("error parsing how long to run (must be a positve int)");
						} else if(Double.isNaN(tempLambda)) {
							throw new IOException("error parsing lambda (must be a double)");
						} else if(Double.isNaN(tempMu)) {
							throw new IOException("error parsing mu (must be a double)");
						} else if(tempNumCashiers < 1) {
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
			data = tempData;
			lambda = tempLambda;
			mu = tempMu;
			numCashiers = tempNumCashiers;
			System.out.println("Done reading CSV");
		} finally {
			parser.close();
		}
	}
	
	/*public DataSnapshot get(int index) {
		if(index >= data.length) throw new ArrayIndexOutOfBoundsException(index);
		return data[index];
	}*/
	
	public double getLambda() {
		return lambda;
	}
	
	public double getMu() {
		return mu;
	}
	
	public int getNumberOfCashiers() {
		return numCashiers;
	}
	
	public long length() {
		return data.length;
	}
	
	public Iterator<DataSnapshot> iterator() {
		return new DataItr();
	}
	
	public void saveData(File f) throws IOException {
		CSVPrinter csv = new CSVPrinter(new BufferedWriter(new FileWriter(f)), CSVFormat.EXCEL);
		csv.printRecord("delta t","shopping","in line","at checkout","lambda","mu","number of cashiers","how long to run");
		csv.printRecord(null,null,null,null,getLambda(),getMu(), getNumberOfCashiers(),length());
		for(DataSnapshot s: data){
			csv.printRecord(s.getTime(),s.getCustomersShopping(),s.getQueueLength(),s.getCashiersBusy());
		}
		csv.flush();
		csv.close();
	}
	
	private class DataItr implements Iterator<DataSnapshot> {

		private int next = 0;
		
		public boolean hasNext() {
			return data != null && next < data.length;
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
